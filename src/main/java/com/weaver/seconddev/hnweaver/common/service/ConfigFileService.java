package com.weaver.seconddev.hnweaver.common.service;

import com.weaver.common.component.exception.WeaException;

/**
 * @author 姚礼林
 * @desc 配置文件服务接口
 * @date 2025/9/11
 **/
public interface ConfigFileService {

    /**
     * 修改日志级别配置
     * @param logLevel 日志级别 (DEBUG, INFO, WARN, ERROR)，如果为null或空字符串则默认为INFO
     * @return 修改结果
     * @throws WeaException 当配置文件不存在时抛出异常
     */
    String updateLogLevel(String logLevel)throws WeaException;

    /**
     * 获取当前日志级别配置
     * @return 当前日志级别，如果未配置则返回null
     */
    String getCurrentLogLevel();

    /**
     * 验证日志级别是否有效
     * @param level 日志级别
     * @return 是否有效
     */
    boolean isValidLogLevel(String level);
}
