package com.weaver.seconddev.hnweaver.common.util;

import com.weaver.seconddev.hnweaver.common.AbstractEsbAction;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * AbstractEsbAction 泛型参数解析工具类。
 *
 * <p>支持从子类或实例中解析 {@code AbstractEsbAction<T, R>} 的两个实际泛型参数，
 * 并兼容中间父类继续传递类型变量的场景。</p>
 *
 * @author 姚礼林
 * @date 2026/7/9
 */
@UtilityClass
public class EsbActionGenericTypeUtil {

    /**
     * 解析当前 Action 的两个泛型参数。
     *
     * @param actionClass Action 子类
     * @return 泛型参数解析结果
     */
    public static GenericTypes resolve(Class<? extends AbstractEsbAction<?,?>> actionClass) {
        Objects.requireNonNull(actionClass, "actionClass 不能为空");
        if (!AbstractEsbAction.class.isAssignableFrom(actionClass)) {
            throw new IllegalArgumentException("类 " + actionClass.getName() + " 不是 AbstractEsbAction 的子类");
        }

        Map<TypeVariable<?>, Type> typeMapping = new HashMap<>();
        Class<?> currentClass = actionClass;
        while (currentClass != Object.class) {
            Type genericSuperclass = currentClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

                for (int i = 0; i < typeParameters.length; i++) {
                    typeMapping.put(typeParameters[i], resolveType(actualTypeArguments[i], typeMapping));
                }

                if (rawType == AbstractEsbAction.class) {
                    Type paramType = resolveType(actualTypeArguments[0], typeMapping);
                    Type resultType = resolveType(actualTypeArguments[1], typeMapping);
                    return new GenericTypes(paramType, resultType);
                }
                currentClass = rawType;
                continue;
            }

            if (genericSuperclass instanceof Class<?>) {
                currentClass = (Class<?>) genericSuperclass;
                continue;
            }
            break;
        }

        throw new IllegalArgumentException("无法从类 " + actionClass.getName() + " 解析 AbstractEsbAction 泛型参数");
    }

    /**
     * 获取 Action 的输入参数完整泛型类型。
     *
     * @param actionClass Action 子类
     * @return 输入参数泛型类型
     */
    public static Type getParamType(Class<? extends AbstractEsbAction<?, ?>> actionClass) {
        return resolve(actionClass).getParamType();
    }

    /**
     * 获取 Action 的输出参数完整泛型类型。
     *
     * @param actionClass Action 子类
     * @return 输出参数泛型类型
     */
    public static Type getResultType(Class<? extends AbstractEsbAction<?, ?>> actionClass) {
        return resolve(actionClass).getResultType();
    }

    /**
     * 获取 Action 的输入参数原始类型。
     *
     * @param actionClass Action 子类
     * @return 输入参数原始类型，无法解析时返回 {@code null}
     */
    public static Class<?> getParamClass(Class<? extends AbstractEsbAction<?, ?>> actionClass) {
        return toClass(getParamType(actionClass));
    }

    /**
     * 获取 Action 的输出参数原始类型。
     *
     * @param actionClass Action 子类
     * @return 输出参数原始类型，无法解析时返回 {@code null}
     */
    public static Class<?> getResultClass(Class<? extends AbstractEsbAction<?, ?>> actionClass) {
        return toClass(getResultType(actionClass));
    }

    /**
     * 将泛型类型转换为原始 Class 类型。
     *
     * @param type 泛型类型
     * @return 原始 Class 类型，无法转换时返回 {@code null}
     */
    public static Class<?> toClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            return rawType instanceof Class<?> ? (Class<?>) rawType : null;
        }
        if (type instanceof GenericArrayType) {
            Class<?> componentType = toClass(((GenericArrayType) type).getGenericComponentType());
            return componentType == null ? null : Array.newInstance(componentType, 0).getClass();
        }
        return null;
    }

    private static Type resolveType(Type type, Map<TypeVariable<?>, Type> typeMapping) {
        if (type instanceof TypeVariable<?>) {
            Type mappedType = typeMapping.get(type);
            return mappedType == null ? type : resolveType(mappedType, typeMapping);
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] resolvedArguments = Arrays.stream(parameterizedType.getActualTypeArguments())
                    .map(argument -> resolveType(argument, typeMapping))
                    .toArray(Type[]::new);
            Type ownerType = parameterizedType.getOwnerType() == null
                    ? null
                    : resolveType(parameterizedType.getOwnerType(), typeMapping);
            return new SimpleParameterizedType(
                    parameterizedType.getRawType(),
                    resolvedArguments,
                    ownerType
            );
        }

        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type componentType = resolveType(genericArrayType.getGenericComponentType(), typeMapping);
            return new SimpleGenericArrayType(componentType);
        }

        return type;
    }

    /**
     * Action 输入与输出泛型类型解析结果。<br>
     * <p>保留完整 Type 信息，并提供获取原始 Class 类型的方法。</p>
     */
    public static final class GenericTypes {
        private final Type paramType;
        private final Type resultType;

        /**
         * 创建 Action 泛型类型解析结果。
         *
         * @param paramType 输入参数泛型类型
         * @param resultType 输出参数泛型类型
         */
        public GenericTypes(Type paramType, Type resultType) {
            this.paramType = paramType;
            this.resultType = resultType;
        }

        /**
         * 获取输入参数完整泛型类型。
         *
         * @return 输入参数泛型类型
         */
        public Type getParamType() {
            return paramType;
        }

        /**
         * 获取输出参数完整泛型类型。
         *
         * @return 输出参数泛型类型
         */
        public Type getResultType() {
            return resultType;
        }

        /**
         * 获取输入参数原始类型。
         *
         * @return 输入参数原始类型，无法转换时返回 {@code null}
         */
        public Class<?> getParamClass() {
            return EsbActionGenericTypeUtil.toClass(paramType);
        }

        /**
         * 获取输出参数原始类型。
         *
         * @return 输出参数原始类型，无法转换时返回 {@code null}
         */
        public Class<?> getResultClass() {
            return EsbActionGenericTypeUtil.toClass(resultType);
        }
    }

    private static final class SimpleParameterizedType implements ParameterizedType {
        private final Type rawType;
        private final Type[] actualTypeArguments;
        private final Type ownerType;

        private SimpleParameterizedType(Type rawType, Type[] actualTypeArguments, Type ownerType) {
            this.rawType = rawType;
            this.actualTypeArguments = actualTypeArguments.clone();
            this.ownerType = ownerType;
        }

        @Override
        public Type @NotNull [] getActualTypeArguments() {
            return actualTypeArguments.clone();
        }

        @Override
        public @NotNull Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }
    }

    private static final class SimpleGenericArrayType implements GenericArrayType {
        private final Type componentType;

        private SimpleGenericArrayType(Type componentType) {
            this.componentType = componentType;
        }

        @Override
        public @NotNull Type getGenericComponentType() {
            return componentType;
        }
    }
}
