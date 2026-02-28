package com.weaver.seconddev.hnweaver.common.domain.entity;

import lombok.Data;

/**
 * @author 姚礼林
 * @desc 业务表单信息
 * @date 2025/9/26
 **/
@Data
public class FormEntity {
    private Long id;
    private Long targetId;
    private String tableName;
}
