package com.weaver.seconddev.hnweaver.common.exception;

/**
 * @author yaolilin
 * @desc 配置异常
 * @date 2024/8/27
 **/
public class ConfigurationApiException extends RuntimeException implements ApiExceptionMsgInterface {
    public ConfigurationApiException() {
    }

    public ConfigurationApiException(String message) {
        super(message);
    }
}
