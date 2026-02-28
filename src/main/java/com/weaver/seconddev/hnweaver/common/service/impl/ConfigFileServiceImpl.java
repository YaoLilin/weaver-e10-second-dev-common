package com.weaver.seconddev.hnweaver.common.service.impl;

import com.weaver.common.component.exception.WeaException;
import com.weaver.seconddev.hnweaver.common.service.ConfigFileService;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 姚礼林
 * @desc 配置文件服务实现类
 * @date 2025/9/11
 **/
@Service
@Slf4j
public class ConfigFileServiceImpl implements ConfigFileService {

    private static final String CONFIG_FILE_PATH =
            "webapps/ROOT/WEB-INF/classes/weaver/config/config-center/weaver-secondev-service.properties";
    private static final String LOG_LEVEL_KEY = "logging.level.com.weaver.seconddev";

    @Override
    public String updateLogLevel(String logLevel) {
        try {
            // 如果没有传入日志级别，默认为INFO
            if (logLevel == null || logLevel.trim().isEmpty()) {
                logLevel = "INFO";
                log.info("未传入日志级别，使用默认值: INFO");
            }

            // 验证日志级别
            if (!isValidLogLevel(logLevel)) {
                throw new IllegalArgumentException("无效的日志级别，支持：DEBUG, INFO, WARN, ERROR");
            }

            // 获取配置文件路径
            String configPath = getConfigFilePath();
            if (configPath == null) {
                throw new IllegalStateException("无法找到配置文件路径");
            }

            // 修改配置文件
            boolean success = updateLogLevelInConfig(configPath, logLevel);
            if (success) {
                log.info("成功修改日志级别为: {}", logLevel);
                return "日志级别修改成功: " + logLevel;
            } else {
                throw new WeaException("修改配置文件失败，请检查配置文件是否存在");
            }

        } catch (Exception e) {
            log.error("修改日志级别失败", e);
            throw new WeaException("修改日志级别失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getCurrentLogLevel() {
        try {
            String configPath = getConfigFilePath();
            if (configPath == null) {
                log.warn("无法找到配置文件路径");
                return null;
            }
            
            File configFile = new File(configPath);
            
            if (!configFile.exists()) {
                log.info("配置文件不存在，当前日志级别未配置");
                return null;
            }
            
            // 逐行读取配置文件查找日志级别配置
            String logLevelProperty = LOG_LEVEL_KEY + "=";
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith(logLevelProperty)) {
                        String currentLevel = line.substring(logLevelProperty.length()).trim();
                        log.info("当前日志级别: {}", currentLevel);
                        return currentLevel;
                    }
                }
            }
            
            log.info("当前日志级别: 未配置");
            return null;
            
        } catch (Exception e) {
            log.error("获取当前日志级别失败", e);
            return null;
        }
    }

    @Override
    public boolean isValidLogLevel(String level) {
        if (level == null || level.trim().isEmpty()) {
            return false;
        }
        String upperLevel = level.trim().toUpperCase();
        return "DEBUG".equals(upperLevel) || "INFO".equals(upperLevel) ||
               "WARN".equals(upperLevel) || "ERROR".equals(upperLevel);
    }

    /**
     * 获取配置文件路径
     */
    private String getConfigFilePath() {
        try {
            // 获取当前工作目录
           File tmpDir = FileUtil.getTmpDir();
           Path rootDir = tmpDir.getParentFile().toPath();
           log.info("当前应用目录: {}", rootDir);

            // 构建配置文件路径
            Path path = rootDir.resolve(CONFIG_FILE_PATH);

            if (Files.exists(path)) {
                log.info("找到配置文件: {}", path);
                return path.toString();
            } else {
                log.warn("配置文件不存在: {}", path);
                return null;
            }
        } catch (Exception e) {
            log.error("获取配置文件路径失败", e);
            return null;
        }
    }

    /**
     * 更新配置文件中的日志级别
     */
    private boolean updateLogLevelInConfig(String configPath, String logLevel) {
        File configFile = new File(configPath);
        
        try {
            // 检查配置文件是否存在
            if (!configFile.exists()) {
                log.error("配置文件不存在，无法修改: {}", configPath);
                return false;
            }
            
            // 读取配置文件所有行
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            
            log.info("成功读取配置文件，共 {} 行", lines.size());
            
            // 查找并更新日志级别配置
            boolean found = false;
            String logLevelProperty = LOG_LEVEL_KEY + "=";
            
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().startsWith(logLevelProperty)) {
                    // 找到现有配置，更新它
                    String oldValue = line.substring(logLevelProperty.length()).trim();
                    lines.set(i, logLevelProperty + logLevel);
                    log.info("更新现有日志级别配置: {} -> {}", oldValue, logLevel);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                // 没有找到现有配置，添加到最后一行
                lines.add("");
                lines.add("# 日志级别配置");
                lines.add(logLevelProperty + logLevel);
                log.info("添加新的日志级别配置: {}", logLevel);
            }
            
            // 写回配置文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            
            log.info("成功更新配置文件: {}", configPath);
            return true;
            
        } catch (IOException e) {
            log.error("更新配置文件失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
