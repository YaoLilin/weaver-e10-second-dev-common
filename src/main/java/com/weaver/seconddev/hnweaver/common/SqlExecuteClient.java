package com.weaver.seconddev.hnweaver.common;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.ebuilder.datasource.api.entity.SqlParamEntity;
import com.weaver.ebuilder.datasource.api.enums.SourceType;
import com.weaver.ebuilder.datasource.api.enums.SqlParamType;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.bean.SqlParam;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.util.DataSetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 姚礼林
 * @desc sql 执行工具类 ，可执行 sql 并返回结果
 * @date 2025/7/25
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class SqlExecuteClient {
    private final DataSetUtil dataSetUtil;

    /**
     * 执行 sql 语句，支持查询与修改（含新增）
     *
     * @param groupType 数据源组类型，根据执行 sql 对应的业务模块进行选择
     * @param sql       sql 语句，可包含占位符（? 符号）
     * @param params    sql 参数
     * @return sql 执行结果
     */
    public SqlExecuteResult executeSql(DatasourceGroupType groupType, String sql, SqlParam... params) {
        List<SqlParamEntity> paramEntities = new ArrayList<>();
        if (ArrayUtil.isNotEmpty(params)) {
            paramEntities = Arrays.stream(params).map(SqlExecuteClient::toSqlParamEntity)
                    .collect(Collectors.toList());
        }
        log.debug("执行sql:{}", sql);
        log.debug("执行参数：{}", JSON.toJSONString(paramEntities));
        Map<String, Object> result = dataSetUtil.executeSqlWithTrans(SourceType.LOGIC, groupType.getId(),
                sql, paramEntities, "", false, false, false);
        log.debug("执行结果：{}", JSON.toJSONString(result));
        WeaResult<Map<String, Object>> apiResult = JSON.toJavaObject(new JSONObject(result), WeaResult.class);
        if (apiResult.isFail()) {
            SqlExecuteResult executeResult = new SqlExecuteResult();
            executeResult.setSuccess(false);
            executeResult.setStatus("failed");
            executeResult.setStatus("FIAL");
            executeResult.setMessage("执行sql失败，错误信息：" + apiResult.getMsg());
            return executeResult;
        }
        SqlExecuteResult executeResult = JSON.toJavaObject(new JSONObject(apiResult.getData()), SqlExecuteResult.class);
        executeResult.setSuccess("OK".equals(executeResult.getStatus()));
        return executeResult;
    }

    /**
     * 执行 sql 语句，支持查询与修改（含新增）
     *
     * @param groupType 数据源组类型，根据执行 sql 对应的业务模块进行选择
     * @param sql       sql 语句，可包含占位符（? 符号）
     * @param params    sql 参数
     * @return sql 执行结果
     */
    public SqlExecuteResult executeSql(DatasourceGroupType groupType, String sql, Object... params) {
        List<SqlParam> paramList = new ArrayList<>();
        if (ArrayUtil.isNotEmpty(params)) {
            for (Object param : params) {
                SqlParamType type = null;
                if (param instanceof String) {
                    type = SqlParamType.STRING;
                } else if (param instanceof Integer) {
                    type = SqlParamType.INTEGER;
                } else if (param instanceof Long) {
                    type = SqlParamType.LONG;
                } else if (param instanceof Double) {
                    type = SqlParamType.DOUBLE;
                } else if (param instanceof Float) {
                    type = SqlParamType.FLOAT;
                } else if (param instanceof Boolean) {
                    type = SqlParamType.BOOLEAN;
                }
                if (type == null) {
                    type = SqlParamType.STRING;
                }
                paramList.add(new SqlParam(param.toString(), type));
            }
        }
        return executeSql(groupType, sql, ArrayUtil.toArray(paramList, SqlParam.class));
    }

    /**
     * 忽略大小写的获取 record 中的字段值，如果获取不到则返回 null
     *
     * @param record    sql 查询结果
     * @param fieldName 字段名
     * @return 字段值，如果获取不到则返回 null
     */
    @Nullable
    public static String getFieldValueIgnoreCase(Map<String, Object> record, String fieldName) {
        if (record.containsKey(fieldName)) {
            return Convert.toStr(record.get(fieldName));
        }
        if (record.containsKey(fieldName.toUpperCase())) {
            return Convert.toStr(record.get(fieldName.toUpperCase()));
        }
        if (record.containsKey(fieldName.toLowerCase())) {
            return Convert.toStr(record.get(fieldName.toLowerCase()));
        }
        return null;
    }

    private static SqlParamEntity toSqlParamEntity(SqlParam sqlParam) {
        SqlParamEntity entity = new SqlParamEntity();
        entity.setValue(sqlParam.getValue());
        entity.setParamType(sqlParam.getType());
        return entity;
    }
}
