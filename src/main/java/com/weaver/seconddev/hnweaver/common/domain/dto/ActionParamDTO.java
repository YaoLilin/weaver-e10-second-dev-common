package com.weaver.seconddev.hnweaver.common.domain.dto;

import com.weaver.seconddev.hnweaver.common.constants.ParamType;
import lombok.Data;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 动作流 Action 参数
 * @date 2026/7/9
 **/
@Data
public class ActionParamDTO {
    /**
     * 参数名（与前端组件字段名一致）
     */
    private String name;

    /**
     * 显示名称（与前端组件字段名一致）
     */
    private String showName;

    /**
     * 是否必需（boolean 类型）
     */
    private Boolean required;

    /**
     * 参数类型（枚举类型）
     */
    private ParamType type;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 描述
     */
    private String desc;

    /**
     * 子级参数列表（用于构建树形结构）
     */
    private List<ActionParamDTO> children;
}
