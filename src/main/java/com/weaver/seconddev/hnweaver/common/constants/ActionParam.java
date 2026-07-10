package com.weaver.seconddev.hnweaver.common.constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 姚礼林
 * @desc 动作流 action 参数注解，可标记在动作流输入/输出参数对象的属性中，用于在前端自动获取参数
 * @date 2025/8/6
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ActionParam {
    boolean required() default  false;
    /**
     * 显示名称
     */
    String displayName() default "";
    String desc() default  "";
    String defaultValue() default "";
}
