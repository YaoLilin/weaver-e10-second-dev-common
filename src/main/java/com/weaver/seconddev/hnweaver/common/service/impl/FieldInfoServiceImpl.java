package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.convert.Convert;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormFieldEntity;
import com.weaver.seconddev.hnweaver.common.service.FieldInfoService;
import com.weaver.seconddev.hnweaver.common.util.SqlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author 姚礼林
 * @desc 表单字段信息
 * @date 2025/9/28
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class FieldInfoServiceImpl implements FieldInfoService {
    private final SqlExecuteClient sqlExecuteClient;

    @Override
    public String getFieldOptionName(DatasourceGroupType groupType, long fieldId, String value) {
        String sql = "SELECT NAME FROM field_option WHERE field_id =? AND value_key=? AND " + SqlUtil.NO_DELETE;
        SqlExecuteResult result = sqlExecuteClient.executeSql(groupType,
                sql, fieldId, value);
        if (!verifySqlResult(result, sql)) {
            log.error("无法查询到选项名称，字段id：{}", fieldId);
            return "";
        }
        return Convert.toStr(SqlExecuteClient.getFieldValueIgnoreCase(result.getRecords().get(0),"NAME"));
    }

    @Override
    public List<FormFieldEntity> getFieldsByFormId(DatasourceGroupType groupType, long formId) {
        String sql = getQuerySql() + " WHERE FORM_ID=? AND " + SqlUtil.NO_DELETE;
        SqlExecuteResult result = sqlExecuteClient.executeSql(groupType,
                sql, formId);
        if (!verifySqlResult(result, sql)) {
            log.error("无法根据表单id查询到字段，表单id：{}", formId);
            return Collections.emptyList();
        }

        List<FormFieldEntity> list = new ArrayList<>();
        List<Map<String, Object>> data = result.getRecords();
        for (Map<String, Object> row : data) {
            FormFieldEntity entity = buildFieldEntity(row);

            list.add(entity);
        }
        return list;
    }

    @Override
    public Optional<FormFieldEntity> getFieldByName(DatasourceGroupType groupType, long formId,
                                                    @Nullable Long subFormId, String fieldName) {
        String sql = getQuerySql() + " WHERE FORM_ID=? AND DATA_KEY=? AND " + SqlUtil.NO_DELETE;
        SqlExecuteResult result;
        if (subFormId != null) {
            sql += " AND SUB_FORM_ID=? ";
            result = sqlExecuteClient.executeSql(groupType,
                    sql, formId, fieldName, subFormId);
        } else {
            result = sqlExecuteClient.executeSql(groupType,
                    sql, formId, fieldName);
        }
        if (!verifySqlResult(result, sql)) {
            log.error("无法查询到字段，表单id：{},明细表id：{},字段名称：{}", formId, subFormId, fieldName);
            return Optional.empty();
        }
        Map<String, Object> data = result.getRecords().get(0);

        return Optional.of(buildFieldEntity(data));
    }

    @Override
    public Optional<FormFieldEntity> getFieldById(DatasourceGroupType groupType, long fieldId) {
        String sql = getQuerySql() + " WHERE ID=? AND "+ SqlUtil.NO_DELETE;
        SqlExecuteResult result = sqlExecuteClient.executeSql(groupType, sql, fieldId);
        if (!verifySqlResult(result, sql)) {
            log.error("无法查询到字段，字段id：{}", fieldId);
            return Optional.empty();
        }
        Map<String, Object> data = result.getRecords().get(0);
        return Optional.of(buildFieldEntity(data));
    }

    private static @NotNull FormFieldEntity buildFieldEntity(Map<String, Object> row) {
        FormFieldEntity entity = new FormFieldEntity();
        entity.setId(Convert.toLong(SqlExecuteClient.getFieldValueIgnoreCase(row, "ID")));
        entity.setFormId(Convert.toLong(SqlExecuteClient.getFieldValueIgnoreCase(row, "FORM_ID")));
        entity.setTitle(Convert.toStr(SqlExecuteClient.getFieldValueIgnoreCase(row, "TITLE")));
        entity.setDataType(Convert.toStr(SqlExecuteClient.getFieldValueIgnoreCase(row, "DATA_TYPE")));
        entity.setDataKey(Convert.toStr(SqlExecuteClient.getFieldValueIgnoreCase(row, "DATA_KEY")));
        entity.setComponentKey(Convert.toStr(SqlExecuteClient.getFieldValueIgnoreCase(row, "COMPONENT_KEY")));
        entity.setSubFormId(Convert.toLong(SqlExecuteClient.getFieldValueIgnoreCase(row, "SUB_FORM_ID")));
        return entity;
    }

    private static boolean verifySqlResult(SqlExecuteResult result, String sql) {
        if (!result.isSuccess()) {
            log.error("sql 错误，sql:{}", sql);
            return false;
        }
        if (result.getRecords().isEmpty()) {
            log.error("查询结果为空");
            return false;
        }
        return true;
    }

    private static @NotNull String getQuerySql() {
        return "SELECT ID,TITLE,DATA_KEY,COMPONENT_KEY,DATA_TYPE,FORM_ID,SUB_FORM_ID FROM form_field ";
    }


}
