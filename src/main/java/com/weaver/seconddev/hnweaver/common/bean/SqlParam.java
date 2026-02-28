package com.weaver.seconddev.hnweaver.common.bean;

import com.weaver.ebuilder.datasource.api.enums.SqlParamType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 姚礼林
 * @desc sql 预编译参数
 * @date 2025/7/25
 **/
@Data
@AllArgsConstructor
public class SqlParam {
    private String value;
    private SqlParamType type;
}
