package com.weaver.seconddev.hnweaver.common;

import com.alibaba.fastjson.JSON;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.constants.ActionParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ujmp.core.util.JsonUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AbstractEsbAction 单元测试<br>
 * <p>验证动作流 Action 输入参数必填校验。</p>
 *
 * @author 姚礼林
 * @date 2026/7/10
 **/
@DisplayName("AbstractEsbAction 输入参数校验测试")
class AbstractEsbActionTest {

    @Test
    @DisplayName("必填字符串缺失时不应执行Action")
    void shouldRejectMissingRequiredString() {
        ValidationAction action = new ValidationAction();

        WeaResult<Map<String, Object>> result = action.execute(new HashMap<String, Object>());

        assertEquals("[name] 输入参数值不能为空", result.getMsg());
        assertFalse(action.executed);
    }

    @Test
    @DisplayName("必填字符串为空白时不应执行Action")
    void shouldRejectBlankRequiredString() {
        ValidationAction action = new ValidationAction();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "  ");

        WeaResult<Map<String, Object>> result = action.execute(params);

        assertEquals("[name] 输入参数值不能为空", result.getMsg());
        assertFalse(action.executed);
    }

    @Test
    @DisplayName("嵌套对象必填字符串为空白时不应执行Action")
    void shouldRejectBlankRequiredNestedString() {
        ValidationAction action = new ValidationAction();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "测试名称");
        params.put("detail", new DetailParam(""));

        WeaResult<Map<String, Object>> result = action.execute(params);
        System.out.printf("result: %s%n", JSON.toJSONString(result));

        assertEquals("[detail] > [code] 输入参数值不能为空", result.getMsg());
        assertFalse(action.executed);
    }

    @Test
    @DisplayName("必填参数有效时应当执行Action")
    void shouldExecuteWhenRequiredParamsAreValid() {
        ValidationAction action = new ValidationAction();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "测试名称");
        params.put("detail", new DetailParam("A001"));

        WeaResult<Map<String, Object>> result = action.execute(params);

        assertFalse(result.isFail());
        assertTrue(action.executed);
    }

    private static class ValidationAction extends AbstractEsbAction<ValidationParam, Map<String, Object>> {
        private boolean executed;

        @Override
        protected WeaResult<Map<String, Object>> doExecute(ValidationParam params) {
            executed = true;
            return WeaResult.success(Collections.<String, Object>singletonMap("result", "success"));
        }

        @Override
        protected ValidationParam convertToParamObj(Map<String, Object> params) {
            ValidationParam result = new ValidationParam();
            result.name = (String) params.get("name");
            result.detail = (DetailParam) params.get("detail");
            return result;
        }
    }

    private static class ValidationParam {
        @ActionParam(required = true, displayName = "名称")
        private String name;
        private DetailParam detail;
    }

    private static class DetailParam {
        @ActionParam(required = true, displayName = "编码")
        private String code;

        private DetailParam(String code) {
            this.code = code;
        }
    }

}
