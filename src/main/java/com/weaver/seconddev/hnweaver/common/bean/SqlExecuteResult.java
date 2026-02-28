package com.weaver.seconddev.hnweaver.common.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc sql执行返回结果
 * @date 2025/7/25
 **/
@Data
public class SqlExecuteResult {
    private String sqlType;
    private String status;
    private boolean success;
    private String message;
    private List<Map<String ,Object>> records;
}
