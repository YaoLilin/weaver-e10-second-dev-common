package com.weaver.seconddev.hnweaver.common.handler;

import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.exception.ApiExceptionMsgInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，仅作用于 com.weaver.seconddev.hnweaver 包下的 Controller
 */
@RestControllerAdvice(basePackages = "com.weaver.seconddev.hnweaver")
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public WeaResult<String> handleException(Exception ex) {
        log.error("接口发生异常:{}",ex.getMessage(), ex);
        if (ex instanceof ApiExceptionMsgInterface) {
            return WeaResult.fail("系统异常：" + ex.getMessage());
        } else if (ex instanceof MissingServletRequestParameterException) {
            return WeaResult.fail("缺少参数：" + ex.getMessage());
        }

        return WeaResult.fail("系统发生异常");
    }
}
