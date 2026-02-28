package com.weaver.seconddev.hnweaver.common.exception;

/**
 * @author 姚礼林
 * @desc 配置校验失败异常，例如配置文件属性是必填，校验是否为空，为空时抛出此异常
 * @date 2025/10/23
 **/
public class ConfigurationInvalidException extends Exception {
    public ConfigurationInvalidException(String message) {
        super(message);
    }
}
