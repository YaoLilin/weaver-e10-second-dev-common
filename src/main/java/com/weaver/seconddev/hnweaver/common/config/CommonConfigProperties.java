package com.weaver.seconddev.hnweaver.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author 姚礼林
 * @desc   所有项目系统都可以获取到的公共配置
 * @date 2025/10/24
 **/
@Data
@Configuration
@RefreshScope
public class CommonConfigProperties {

    /**
     * 系统管理员用户id
     */
    @Value("${sysadminUserId:}")
    private String sysadminUserId;

    /**
     * OA访问地址
     */
    @Value("${oaAddress:}")
    private String oaAddress;
}
