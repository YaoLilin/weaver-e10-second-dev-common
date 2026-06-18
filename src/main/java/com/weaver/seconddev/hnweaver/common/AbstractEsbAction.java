package com.weaver.seconddev.hnweaver.common;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.esb.api.rpc.EsbServerlessRpcRemoteInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 动作流 Action 抽象类，可明确传入参数和返回参数类型，替换原来的 Map 形式。
 *
 * @param <T> 参数类型，由 convertToParamObj 将 Map 转换为此类型的对象
 * @param <R> 返回数据类型，不能为基本类型包装对象（如 String、Long 等），
 *          必需能通过 Convert.toMap() 转换为 Map&lt;String, Object&gt;，
 *          推荐使用 Java Bean 或 Map 子类
 * @author 姚礼林
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
     * 自动将第一层值为 JSON 字符串（以 {@code &#123;} 或 {@code &#91;} 开头）的参数
     * 解析为 Map 或 List，再进行转换，避免 Action 中 JSON 对象/数组被转为字符串导致反序列化失败。
     *
     * @param params action 传入的参数
     * @param clazz  参数对象类型
     * @return 参数对象
     */
    protected static <T> T convertToParamObj(Map<String, Object> params, Class<T> clazz) {
        Map<String, Object> parsedParams = new HashMap<>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String && isJsonLike((String) value)) {
                try {
                    Object parsed = JSONObject.parse((String) value);
                    parsedParams.put(entry.getKey(), parsed);
                } catch (Exception e) {
                    log.debug("参数 {} 的值不是有效 JSON，保留原始字符串", entry.getKey());
                    parsedParams.put(entry.getKey(), value);
                }
            } else {
                parsedParams.put(entry.getKey(), value);
            }
        }
        return JSONObject.parseObject(JSONObject.toJSONString(parsedParams), clazz);
    }

    /**
     * 判断字符串是否形似 JSON（以 {@code &#123;} 或 {@code &#91;} 开头）
     */
    private static boolean isJsonLike(String str) {
        String trimmed = str.trim();
        return trimmed.startsWith("{") || trimmed.startsWith("[");
    }
}
