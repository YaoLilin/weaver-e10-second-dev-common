package com.weaver.seconddev.hnweaver.common.controller;

import com.weaver.common.authority.annotation.WeaPermission;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.domain.param.LogLevelParam;
import com.weaver.seconddev.hnweaver.common.service.ConfigFileService;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author 姚礼林
 * @desc 开发者工具接口
 * @date 2025/9/11
 **/
@RestController
@RequestMapping({"/api/secondev/dev-tools"})
@WeaPermission(publicPermission = true)
@RequiredArgsConstructor
@Slf4j
public class DevToolsContrller {

    private final ConfigFileService configFileService;

    @PostMapping("/log-level")
    public WeaResult<String> changeLogLevel(@RequestBody(required = false) LogLevelParam  param) {
        try {
            String logLevel;
            if (param == null) {
                logLevel = "INFO";
            }else {
                // 如果没有传入日志级别，默认为INFO
                logLevel = CharSequenceUtil.isBlank(param.getLevel()) ? "INFO" : param.getLevel();
            }
            String result = configFileService.updateLogLevel(logLevel);
            return WeaResult.success(result);
        } catch (Exception e) {
            log.error("修改日志级别失败", e);
            return WeaResult.fail(e.getMessage());
        }
    }
    
    @GetMapping("/log-level")
    public WeaResult<String> getCurrentLogLevel() {
        try {
            String currentLevel = configFileService.getCurrentLogLevel();
            if (currentLevel != null) {
                return WeaResult.success("当前日志级别: " + currentLevel);
            } else {
                return WeaResult.success("当前日志级别未配置");
            }
        } catch (Exception e) {
            log.error("获取当前日志级别失败", e);
            return WeaResult.fail(e.getMessage());
        }
    }
}
