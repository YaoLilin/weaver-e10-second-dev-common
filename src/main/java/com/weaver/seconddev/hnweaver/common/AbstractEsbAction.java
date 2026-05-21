package com.weaver.seconddev.hnweaver.common;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.esb.api.rpc.EsbServerlessRpcRemoteInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 姚礼林
 * @desc 动作流 Action 抽象类，可明确传入参数和返回参数类型，替换原来的 Map 形式。
 * @date 2025/11/25
 **/
@Slf4j
public abstract class AbstractEsbAction <T,R> implements EsbServerlessRpcRemoteInterface {

    @Override
    public WeaResult<Map<String, Object>> execute(Map<String, Object> params) {
        log.debug("传入参数：{}", JSONObject.toJSONString(params));
        try {
            T paramObj = convertToParamObj(params);
            WeaResult<R> result = doExecute(paramObj);
            if (result.isFail()) {
                log.error("执行失败，错误信息：{}", result.getMsg());
                return WeaResult.fail(result.getMsg());
            }
            log.info("执行成功，结果：{}", JSONObject.toJSONString(result));
            return WeaResult.success(Convert.toMap(String.class, Object.class, result.getData()));
        } catch (Exception e) {
            log.error("执行失败", e);
            return WeaResult.fail("执行失败:" + e.getMessage()+";\n stacks:"+
                    Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).
                            collect(Collectors.joining("\n")));
        }
    }

    /**
     * 执行动作逻辑
     *
     * @param params 参数
     * @return 执行结果
     */
    protected abstract WeaResult<R> doExecute(T params);

    /**
     * 获取参数对象
     *
     * @param params action 传入的参数
     * @return 参数对象
     */
    protected abstract T convertToParamObj(Map<String, Object> params);

    /**
     * 将 map 转换为 java 对象。<br>
     * 注意，如果 Action 传入参数中包含 JSON 对象或数组，不可使用本方法进行转换，会导致报错，因为 JSON 对象或数组
     * 在 Action 中会被转为字符串。
     *
     * @param params action 传入的参数
     * @param clazz  参数对象类型
     * @return 参数对象
     */
    protected static <T> T convertToParamObj(Map<String, Object> params, Class<T> clazz) {
        return JSONObject.parseObject(JSONObject.toJSONString(params), clazz);
    }
}
