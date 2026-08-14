package com.weaver.seconddev.hnweaver.common.util;

import cn.hutool.core.util.StrUtil;
import com.weaver.seconddev.hnweaver.common.AbstractEsbAction;
import com.weaver.seconddev.hnweaver.common.constants.ActionParam;
import com.weaver.seconddev.hnweaver.common.constants.ParamType;
import com.weaver.seconddev.hnweaver.common.domain.dto.ActionParamDTO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 动作流 Acton 参数解析工具类
 * 解析规则：
 * 1. 简单类型（String/数值/Boolean）与 List 字段默认作为参数，List 的元素对象内的属性也会被解析为 Action 参数；
 * 2. Map 字段作为对象参数解析，不解析其键和值类型；静态字段和合成字段不会被解析。
 * 3. 其它嵌套对象会解析为 Action 参数
 * @date 2026/1/15
 **/
@Slf4j
@UtilityClass
public class WorkflowActionParamParser {

    /**
     * 解析 Action 的输入参数
     *
     * @param clazz AbstractEsbAction 的子类
     * @return 参数列表
     */
    public static List<ActionParamDTO> parseInputParams(Class<? extends AbstractEsbAction<?, ?>> clazz){
        List<ActionParamDTO> result = new ArrayList<>();

        try {
            Class<?> paramType = EsbActionGenericTypeUtil.getParamClass(clazz);
            if (paramType != null) {
                result = parseParams(paramType);
            }
        } catch (Exception e) {
            log.error("解析 Action 参数失败: {}", clazz.getName(), e);
        }

        return result;
    }

    /**
     * 解析 Action 的输出参数
     *
     * @param clazz AbstractEsbAction 的子类
     * @return 参数列表
     */
    public static List<ActionParamDTO> parseOutputParams(Class<? extends AbstractEsbAction<?, ?>> clazz) {
        List<ActionParamDTO> result = new ArrayList<>();

        try {
            Class<?> resultType = EsbActionGenericTypeUtil.getResultClass(clazz);
            if (resultType != null) {
                result = parseParams(resultType);
            }
        } catch (Exception e) {
            log.error("解析 Action 输出参数失败: {}", clazz.getName(), e);
        }

        return result;
    }

    /**
     * 解析参数类型中的字段，包括父类中的字段
     *
     * @param paramType 参数类型
     * @return 参数列表
     */
    public static List<ActionParamDTO> parseParams(Class<?> paramType) {
        List<ActionParamDTO> result = new ArrayList<>();
        if (paramType == null) {
            return result;
        }

        Class<?> current = paramType;
        while (current != null && current != Object.class) {
            Field[] fields = current.getDeclaredFields();
            for (Field field : fields) {
                if (shouldSkip(field)) {
                    continue;
                }

                ActionParamDTO dto = new ActionParamDTO();
                dto.setName(field.getName());

                ActionParam annotation = field.getAnnotation(ActionParam.class);
                boolean isAnnotated = annotation != null;

                Class<?> fieldType = field.getType();
                boolean isSimpleType = isSimpleType(fieldType);
                boolean isList = List.class.isAssignableFrom(fieldType);
                boolean isMap = Map.class.isAssignableFrom(fieldType);
                boolean isNestedObject = !isSimpleType && !isList && !isMap;

                // 简单类型、List、Map、嵌套对象或被注解标记的字段才视为 Action 参数
                if (isSimpleType || isList || isMap || isNestedObject || isAnnotated) {
                    // 设置注解属性
                    if (isAnnotated) {
                        // 显示名读取 ActionParam 注解中的 displayName 字段
                        if (StrUtil.isNotBlank(annotation.displayName())) {
                            dto.setShowName(annotation.displayName());
                        } else {
                            dto.setShowName(field.getName());
                        }
                        dto.setRequired(annotation.required());
                        dto.setDefaultValue(annotation.defaultValue());
                        dto.setDesc(annotation.desc());
                    } else {
                        dto.setShowName(field.getName());
                        dto.setRequired(false);
                    }

                    // List 参数使用元素类型作为参数类型，数组属性单独标识
                    Class<?> valueType = isList ? getListElementType(field) : fieldType;
                    dto.setType(getTypeValue(valueType));
                    dto.setJavaType(valueType.getName());
                    dto.setArray(isList);

                    // 处理嵌套对象或 List<T> 中的泛型参数
                    List<ActionParamDTO> children = parseNestedType(field);
                    if (children != null && !children.isEmpty()) {
                        dto.setChildren(children);
                    }

                    result.add(dto);
                }
            }
            current = current.getSuperclass();
        }
        return result;
    }

    /**
     * 解析嵌套类型的字段
     * 支持：普通对象类型、List<T> 泛型类型
     *
     * @param field 字段
     * @return 子参数列表
     */
    private static List<ActionParamDTO> parseNestedType(Field field) {
        Class<?> fieldType = field.getType();

        // 如果是 List 类型，获取泛型参数
        if (List.class.isAssignableFrom(fieldType)) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
                    Class<?> elementType = (Class<?>) actualTypeArguments[0];
                    // 如果泛型参数不是简单类型，则解析其字段
                    if (!isSimpleType(elementType) && !Map.class.isAssignableFrom(elementType)) {
                        return parseParams(elementType);
                    }
                }
            }
            return null;
        }

        // 如果是普通对象类型（非简单类型、非 Map、非 List）
        if (!isSimpleType(fieldType) && !Map.class.isAssignableFrom(fieldType) && !List.class.isAssignableFrom(fieldType)) {
            return parseParams(fieldType);
        }

        return null;
    }

    /**
     * 获取 List 字段的元素类型。<br>
     * <p>
     * 未声明或无法解析泛型时按 Object 处理，使前端以 JSON 数组展示。
     * </p>
     *
     * @param field List 类型字段
     * @return List 元素类型
     */
    private static Class<?> getListElementType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
                return (Class<?>) actualTypeArguments[0];
            }
        }
        return Object.class;
    }

    private static boolean shouldSkip(Field field) {
        int modifiers = field.getModifiers();
        // 排除静态字段和合成字段
        return Modifier.isStatic(modifiers) || field.isSynthetic();
    }

    private static boolean isSimpleType(Class<?> type) {
        return type == String.class || type == Integer.class || type == int.class ||
                type == Boolean.class || type == boolean.class ||
                type == Double.class || type == double.class ||
                type == Float.class || type == float.class ||
                type == BigDecimal.class || type == Long.class || type == long.class;
    }

    private static ParamType getTypeValue(Class<?> type) {
        if (type == Integer.class || type == int.class ||
                type == Double.class || type == double.class ||
                type == Float.class || type == float.class ||
                type == BigDecimal.class) {
            return ParamType.NUMBER;
        } else if (type == Boolean.class || type == boolean.class) {
            return ParamType.BOOLEAN;
        } else if (!isSimpleType(type)) {
            return ParamType.OBJECT;
        }
        return ParamType.STRING;
    }
}
