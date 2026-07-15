package com.weaver.seconddev.hnweaver.common.controller;

import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.AbstractEsbAction;
import com.weaver.seconddev.hnweaver.common.constants.EsbAction;
import com.weaver.seconddev.hnweaver.common.domain.dto.ActionParamDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * WorkflowActionParamController 单元测试<br>
 * <p>验证按 Spring Bean 名称获取 Action 的输入与输出参数。</p>
 *
 * @author 姚礼林
 * @date 2026/7/10
 **/
@ExtendWith(MockitoExtension.class)
@DisplayName("WorkflowActionParamController 参数查询测试")
class WorkflowActionParamControllerTest {
    private static final String ACTION_GROUP_ID = "testAction";

    @Mock
    private ListableBeanFactory beanFactory;

    @InjectMocks
    private EsbActionParamController controller;

    @Test
    @DisplayName("应当根据 groupId 获取 Action 输入参数")
    void shouldGetInputParamsByGroupId() {
        mockActionBean();

        WeaResult<List<ActionParamDTO>> result = controller.getInputParams(ACTION_GROUP_ID);

        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("input", result.getData().get(0).getName());
    }

    @Test
    @DisplayName("应当根据 groupId 获取 Action 输出参数")
    void shouldGetOutputParamsByGroupId() {
        mockActionBean();

        WeaResult<List<ActionParamDTO>> result = controller.getOutputParams(ACTION_GROUP_ID);

        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("output", result.getData().get(0).getName());
    }

    @Test
    @DisplayName("groupId 不存在时应当返回失败结果")
    void shouldReturnFailureWhenGroupIdDoesNotExist() {
        when(beanFactory.containsBean(ACTION_GROUP_ID)).thenReturn(false);

        WeaResult<List<ActionParamDTO>> result = controller.getInputParams(ACTION_GROUP_ID);

        assertNull(result.getData());
        assertEquals("未找到对应的 AbstractEsbAction：" + ACTION_GROUP_ID, result.getMsg());
    }

    private void mockActionBean() {
        when(beanFactory.containsBean(ACTION_GROUP_ID)).thenReturn(true);
        doReturn(TestAction.class).when(beanFactory).getType(ACTION_GROUP_ID);
    }

    @EsbAction(value = ACTION_GROUP_ID, desc = "<p>测试 Action 描述</p>")
    public static class TestAction extends AbstractEsbAction<InputParam, OutputParam> {
        @Override
        protected WeaResult<OutputParam> doExecute(InputParam params) {
            return WeaResult.success(new OutputParam());
        }

        @Override
        protected InputParam convertToParamObj(Map<String, Object> params) {
            return new InputParam();
        }
    }

    public static class InputParam {
        private String input;
    }

    public static class OutputParam {
        private String output;
    }
}
