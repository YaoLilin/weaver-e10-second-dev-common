package com.weaver.seconddev.hnweaver.common;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.esb.api.rpc.EsbServerlessRpcRemoteInterface;
import com.weaver.seconddev.hnweaver.common.constants.ActionParam;
import com.weaver.seconddev.hnweaver.common.util.EsbActionGenericTypeUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 动作流 Action 抽象类，可明确传入参数和返回参数类型，替换原来的 Map 形式，并支持校验必填参数。
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

    /**
     * 获取当前 Action 的第一个泛型参数类型。
     *
     * @return 参数泛型对应的 Class，无法解析时返回 null
     */
    public Class<?> getParamType() {
        return EsbActionGenericTypeUtil.getParamClass(getActionClass());
    }

    /**
     * 获取当前 Action 的第一个泛型参数完整类型。
     *
     * @return 参数泛型完整类型
     */
    public Type getParamGenericType() {
        return EsbActionGenericTypeUtil.getParamType(getActionClass());
    }

    /**
     * 获取当前 Action 的第二个泛型参数原始类型。
     *
     * @return 返回值泛型对应的原始 Class，无法解析时返回 null
     */
    public Class<?> getResultType() {
        return EsbActionGenericTypeUtil.getResultClass(getActionClass());
    }

    /**
     * 获取当前 Action 的第二个泛型参数完整类型。
     *
     * @return 返回值泛型完整类型
     */
    public Type getResultGenericType() {
        return EsbActionGenericTypeUtil.getResultType(getActionClass());
    }

    @Override
    public WeaResult<Map<String, Object>> execute(Map<String, Object> params) {
        log.debug("传入参数：{}", JSONObject.toJSONString(params));
        try {
            T paramObj = convertToParamObj(params);
            String requiredParamError = validateRequiredParams(paramObj);
            if (requiredParamError != null) {
                log.warn("输入参数校验失败：{}", requiredParamError);
                return WeaResult.fail(requiredParamError);
            }
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

    private String validateRequiredParams(T paramObj) {
        if (paramObj == null) {
            return "输入参数不能为空";
        }
        return validateRequiredFields(paramObj, new IdentityHashMap<Object, Boolean>(), new ArrayList<String>());
    }

    private String validateRequiredFields(Object paramObj, Map<Object, Boolean> checkedObjects,
                                          List<String> fieldPath) {
        if (checkedObjects.put(paramObj, Boolean.TRUE) != null) {
            return null;
        }
        for (Class<?> current = paramObj.getClass(); current != null && current != Object.class;
             current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }
                String validationError = validateField(paramObj, field, checkedObjects, fieldPath);
                if (validationError != null) {
                    return validationError;
                }
            }
        }
        return null;
    }

    private String validateField(Object paramObj, Field field, Map<Object, Boolean> checkedObjects,
                                 List<String> fieldPath) {
        try {
            field.setAccessible(true);
            Object value = field.get(paramObj);
            List<String> currentPath = new ArrayList<String>(fieldPath);
            currentPath.add(field.getName());
            ActionParam annotation = field.getAnnotation(ActionParam.class);
            if (annotation != null && annotation.required() && isRequiredValueEmpty(value)) {
                return buildRequiredParamError(currentPath);
            }
            return validateNestedValue(value, checkedObjects, currentPath);
        } catch (IllegalAccessException | SecurityException e) {
            log.error("校验输入参数 {} 失败", field.getName(), e);
            return "输入参数校验失败";
        }
    }

    private String validateNestedValue(Object value, Map<Object, Boolean> checkedObjects, List<String> fieldPath) {
        if (value == null || value instanceof CharSequence || value instanceof Number || value instanceof Boolean
                || value instanceof Character || value instanceof Map || value.getClass().isEnum()
                || value.getClass().getName().startsWith("java.")) {
            return null;
        }
        if (value instanceof Iterable<?>) {
            for (Object item : (Iterable<?>) value) {
                String validationError = validateNestedValue(item, checkedObjects, fieldPath);
                if (validationError != null) {
                    return validationError;
                }
            }
            return null;
        }
        return validateRequiredFields(value, checkedObjects, fieldPath);
    }

    private boolean isRequiredValueEmpty(Object value) {
        return value == null || (value instanceof CharSequence
                && CharSequenceUtil.isBlank((CharSequence) value));
    }

    private String buildRequiredParamError(List<String> fieldPath) {
        return "[" + String.join("] > [", fieldPath) + "] 输入参数值不能为空";
    }

    @SuppressWarnings("unchecked")
    private Class<? extends AbstractEsbAction<?, ?>> getActionClass() {
        return (Class<? extends AbstractEsbAction<?, ?>>) getClass();
    }

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
            if (value instanceof String && isJsonLike((String) value)
                    && !isStringField(clazz, entry.getKey())) {
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
     * 判断目标类中指定字段是否为 String 类型<br>
     * <p>
     * 若为 String 类型，则 JSON 字符串值应保留原始字符串不做自动解析，
     * 避免如加密场景下原始 JSON 内容被重新序列化导致结果不一致。
     * </p>
     *
     * @param clazz     目标类
     * @param fieldName 字段名
     * @return 如果字段存在且类型为 String 返回 true，否则返回 false
     */
    private static <T> boolean isStringField(Class<T> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.getType() == String.class;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否形似 JSON（以 {@code &#123;} 或 {@code &#91;} 开头）
     */
    private static boolean isJsonLike(String str) {
        String trimmed = str.trim();
        return trimmed.startsWith("{") || trimmed.startsWith("[");
    }
}
