package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormEntity;
import com.weaver.seconddev.hnweaver.common.service.FormInfoService;
import com.weaver.seconddev.hnweaver.common.util.SqlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 表单信息
 * @date 2025/9/28
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class FormInfoServiceImpl implements FormInfoService {
    private final SqlExecuteClient sqlExecuteClient;

    @Override
    public Optional<FormEntity> getFormByFormId(DatasourceGroupType groupType, long formId, String tenantKey) {
        log.debug("表单id：{}", formId);
        String sql = getSelectSql() + " WHERE f.ID=? AND f." + SqlUtil.NO_DELETE;
        List<Object> params = new ArrayList<>();
        params.add(formId);
        return queryForm(groupType,sql, tenantKey, params);
    }

    @Override
    public Optional<FormEntity> getFormByTableName(DatasourceGroupType groupType, String tableName, String tenantKey) {
        log.debug("表单表名：{}", tableName);
        String sql = getSelectSql() + " WHERE ft.TABLE_NAME=? AND f." + SqlUtil.NO_DELETE;
        log.debug("根据表单名称获取表单信息，sql:{}", sql);
        List<Object> params = new ArrayList<>();
        params.add(tableName);
        return queryForm(groupType, sql, tenantKey, params);
    }

    @Override
    public Optional<Long> getSubFormId(DatasourceGroupType groupType, String tableName, String tenantKey) {
        String sql = "SELECT ID FROM sub_form WHERE DATA_KEY =? AND " + SqlUtil.NO_DELETE;
        List<Object> params = new ArrayList<>();
        params.add(tableName);
        if (CharSequenceUtil.isNotBlank(tenantKey)) {
            addTenantKeyCondition(tenantKey, sql, params, null);
        }

        SqlExecuteResult result = sqlExecuteClient.executeSql(groupType,
                sql, params.toArray());
        if (isEmptyRecord(result, sql)) {
            return Optional.empty();
        }
        return Optional.of(Convert.toLong(SqlExecuteClient.getFieldValueIgnoreCase(result.getRecords().get(0),
                "ID")));
    }

    @Override
    public Optional<Long> getFormIdBySubFormName(DatasourceGroupType groupType, String subFormTableName, String tenantKey) {
        String sql = "SELECT FORM_ID FROM sub_form WHERE DATA_KEY =? AND " + SqlUtil.NO_DELETE;
        List<Object> params = new ArrayList<>();
        params.add(subFormTableName);
        if (CharSequenceUtil.isNotBlank(tenantKey)) {
            addTenantKeyCondition(tenantKey, sql, params, null);
        }

        SqlExecuteResult result = sqlExecuteClient.executeSql(groupType,
                sql, params.toArray());
        if (isEmptyRecord(result, sql)) {
            return Optional.empty();
        }
        return Optional.of(Convert.toLong(SqlExecuteClient.getFieldValueIgnoreCase(result.getRecords().get(0),
                "FORM_ID")));
    }

    private @NotNull Optional<FormEntity> queryForm(DatasourceGroupType groupType, String sql,
                                                    String tenantKey , List<Object> params) {
        log.debug("查询表单，sql:{}", sql);
        if (CharSequenceUtil.isNotBlank(tenantKey)) {
            sql = addTenantKeyCondition(tenantKey, sql, params, "f");
            log.debug("添加租户条件后sql：{}", sql);
        }
        SqlExecuteResult result = sqlExecuteClient.executeSql(groupType, sql, params.toArray());
        if (isEmptyRecord(result, sql)) {
            return Optional.empty();
        }
        FormEntity formEntity = buildFormEntity(result);
        return Optional.of(formEntity);
    }

    private static @NotNull String addTenantKeyCondition(String tenantKey, String sql, List<Object> params,
                                                         String tableAlias) {
        if (CharSequenceUtil.isNotBlank(tableAlias)) {
            sql += " AND "+tableAlias+".TENANT_KEY=? ";
        }else {
            sql += " AND TENANT_KEY=? ";
        }
        params.add(tenantKey);
        return sql;
    }

    private String getSelectSql() {
        return "  SELECT f.ID,f.TARGET_ID,ft.TABLE_NAME " +
                "        FROM form f" +
                "        JOIN form_table ft ON f.ID = ft.FORM_ID ";
    }

    private static @NotNull FormEntity buildFormEntity(SqlExecuteResult result) {
        Map<String, Object> data = result.getRecords().get(0);
        FormEntity formEntity = new FormEntity();
        formEntity.setId(Convert.toLong(SqlExecuteClient.getFieldValueIgnoreCase(data, "ID")));
        formEntity.setTargetId(Convert.toLong(SqlExecuteClient.getFieldValueIgnoreCase(data, "TARGET_ID")));
        formEntity.setTableName(SqlExecuteClient.getFieldValueIgnoreCase(data, "TABLE_NAME"));
        return formEntity;
    }

    private boolean isEmptyRecord(SqlExecuteResult result, String sql) {
        if (!result.isSuccess()) {
            log.error("查询表单信息失败，sql执行异常，sql:{}", sql);
            return true;
        }
        log.debug("查询结果：{}", JSON.toJSONString(result.getRecords()));
        if (result.getRecords().isEmpty()) {
            log.warn("查询结果为空");
            return true;
        }
        return false;
    }
}
