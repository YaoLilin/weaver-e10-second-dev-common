package com.weaver.seconddev.hnweaver.common.exception;

/**
 * @author 姚礼林
 * @desc Action 类参数校验异常
 * @date 2025/10/22
 **/
public class ActionParamInvalidException extends RuntimeException {
    public ActionParamInvalidException(String message) {
        super(message);
    }
}
