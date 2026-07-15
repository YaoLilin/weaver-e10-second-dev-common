package com.weaver.seconddev.hnweaver.common.domain.dto;

import lombok.Data;

/**
 * ESB Action 基本信息。
 *
 * @author 姚礼林
 * @date 2026/7/14
 */
@Data
public class EsbActionInfoDTO {
    /**
     * Action 描述，内容可为 HTML 文本。
     */
    private String desc;

    /**
     * Action 类全限定名，包含包路径和类名称。
     */
    private String classPath;

    /**
     * Action 类名。
     */
    private String className;

    /**
     * Action 的 Spring Bean 名称。
     */
    private String groupId;

    /**
     * 是否支持基于 AbstractEsbAction 泛型自动解析输入和输出参数。
     */
    private Boolean supportsParamParsing;
}
