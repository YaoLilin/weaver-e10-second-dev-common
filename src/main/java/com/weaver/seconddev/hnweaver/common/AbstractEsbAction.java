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
                return WeaResult.fail(result.getMsg());
            }
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
     * 将 map 转换为 java 对象
     *
     * @param params action 传入的参数
     * @param clazz  参数对象类型
     * @return 参数对象
     */
    protected static <T> T convertToParamObj(Map<String, Object> params, Class<T> clazz) {
        return JSONObject.parseObject(JSONObject.toJSONString(params), clazz);
    }
}
