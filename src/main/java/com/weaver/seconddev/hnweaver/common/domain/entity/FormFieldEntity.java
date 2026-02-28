package com.weaver.seconddev.hnweaver.common.domain.entity;

import lombok.Data;

/**
 * @author 姚礼林
 * @desc 字段实体类
 * @date 2025/9/26
 **/
@Data
public class FormFieldEntity {
    private Long id;
    private String title;
    private String dataKey;
    private String componentKey;
    private String dataType;
    private Long formId;
    private Long subFormId;
}
