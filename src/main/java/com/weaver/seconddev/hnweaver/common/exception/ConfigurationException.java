package com.weaver.seconddev.hnweaver.common.exception;

/**
 * @author 姚礼林
 * @desc 配置信息异常，用于配置属性没有配置，配置值不正确等场景
 * @date 2025/11/28
 **/
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message) {
        super(message);
    }
}
