package com.weaver.seconddev.hnweaver.common.constants;

import java.lang.annotation.*;

/**
 * @author 姚礼林
 * @desc 后端埋点信息注解，在埋点类上使用此注解用于获取埋点的描述，可将描述展示在前端，建议所有埋点都加上这个注解
 * @date 2026/8/6
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HookInfo {
    String value();
}
