package com.weaver.seconddev.hnweaver.common.constants;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author 姚礼林
 * @desc Esb 动作流 Action 注解，使用此注解时无需再加上 @Service 注解，可使用此注解生成 Action 说明文档。
 * @date 2026/7/13
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface EsbAction {
    String desc() default "";

    @AliasFor(annotation = Service.class, attribute = "value")
    String value();
}
