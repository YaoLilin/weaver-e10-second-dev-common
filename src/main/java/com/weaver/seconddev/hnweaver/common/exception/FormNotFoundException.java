package com.weaver.seconddev.hnweaver.common.exception;

/**
 * @author 姚礼林
 * @desc 没有找到表单异常
 * @date 2025/7/31
 **/
public class FormNotFoundException extends RuntimeException{
    public FormNotFoundException() {
    }

    public FormNotFoundException(String message) {
        super(message);
    }
}
