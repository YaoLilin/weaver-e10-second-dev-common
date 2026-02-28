package com.weaver.seconddev.hnweaver.common.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONUtil;
import com.weaver.datasource.utils.rest.CommonRestService;
import com.weaver.ebuilder.datasource.api.entity.ExecuteSqlEntity;
import com.weaver.ebuilder.datasource.api.entity.SqlParamEntity;
import com.weaver.ebuilder.datasource.api.enums.SourceType;
import com.weaver.ebuilder.datasource.api.enums.SqlParamType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Author: 李玉逸
 * Describe:
 * Date: 2024/11/7 13:47
 */
@Slf4j
@Component
public class DataSetUtil {

    @Autowired
    private CommonRestService commonRestService;

    public static String base64(String sql) {
        return Base64.encode(sql);
    }

    /**
     * 根据数据库类型 找到对应数据库
     *
     * @param sourceType sourceType 枚举类
     *                   ETEAMS  :数据仓库
     *                   FORM: ebuilder表单
     *                   LOGIC: 各模块提供业务数据(逻辑表)
     *                   EXTERNAL： 外部数据源
     * @return
     */
    public Map<String, Object> getDataGroups(String sourceType) {
        try {
            //拼接参数
            MultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
            valueMap.add("sourceType", sourceType);

            return commonRestService.postForObject("/sapi/datasource/ds/group", MediaType.APPLICATION_FORM_URLENCODED, Map.class, valueMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;

        }
    }

    /**
     * 执行服务内部sql:/sapi/secondev/ds/executeSql
     * sourceType  :LOGIC
     * groupId  :   weaver-ebuilder-app-service 可以通过group接口获取
     * sql :      select * from ebda_app limit  10
     * @param sourceType LOGIC: 各模块提供业务数据(逻辑表)
     * @param groupId
     * @param sql
     * @return
     */
    public Map<String, Object> executeLogicSql(SourceType sourceType, String groupId, String sql) {
        //拼接参数
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("sourceType", sourceType);
        valueMap.put("groupId", groupId);
        //sql select * from table where tenant_key = '租户key' and delete_dype = 0 and id in (1,2,3)
        valueMap.put("sql", base64(sql));

        try {
            return commonRestService.getForObject("/sapi/secondev/ds/executeSql", valueMap, MediaType.APPLICATION_FORM_URLENCODED, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 执行外部数据库sql: /sapi/datasearch/external/data/executeSql
     * sourceType  : EXTERNAL: 外部数据源
     * groupId  :   842668710322556928L 通过group接口获取数据加工内配置的连接id
     * sql :      select * from ebda_app limit  10
     *
     * @param
     * @return
     */
    public Map<String, Object> executeExternalSql(SourceType sourceType, String groupId, String sql, List<SqlParamEntity> sqlParams) {
        //执行sql  参数sourceType   groupId  sql
        ExecuteSqlEntity executeSqlEntity = new ExecuteSqlEntity();
        executeSqlEntity.setSql(base64(sql));
        executeSqlEntity.setGroupId(groupId);
        executeSqlEntity.setSourceType(sourceType);

        //若通过占位符方式查询外部数据库,需增加此参数
        //占位符list 的顺序  要与 sql 的?占位符顺序一致
        if (sqlParams != null && !sqlParams.isEmpty()) {
            executeSqlEntity.setParams(sqlParams);
        }

        //拼接参数
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("params", JSONUtil.toJsonStr(executeSqlEntity));

        try {
            return commonRestService.postForForm("/sapi/datasearch/external/data/executeSql", Map.class, valueMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 构建占位符参数
     * 注意:list的参数顺序 必须要与 sql内的占位符参数顺序一致
     * 此案例构建的json字符串 是提供给sql:select * from table where tenant_key = ? and delete_dype = ? and id in (?, ?, ?) 使用
     * @return
     */
    public List<SqlParamEntity> getSqlParams() {
        List<SqlParamEntity> sqlParams = new ArrayList<>();

        SqlParamEntity sqlParam1 = new SqlParamEntity();
        sqlParam1.setParamType(SqlParamType.VARCHAR);
        sqlParam1.setValue("thsv5s4n2c");
        sqlParams.add(sqlParam1);

        SqlParamEntity sqlParam2 = new SqlParamEntity();
        sqlParam2.setParamType(SqlParamType.INTEGER);
        sqlParam2.setValue("0");
        sqlParams.add(sqlParam2);

        List<Long> ids = new ArrayList<>();
        ids.add(1054407582048280585L);
        ids.add(1054407345976074241L);
        ids.add(1054406667262189570L);
        for (Long id : ids) {
            SqlParamEntity sqlParam3 = new SqlParamEntity();
            sqlParam3.setParamType(SqlParamType.LONG);
            sqlParam3.setValue(String.valueOf(id));
            sqlParams.add(sqlParam3);
        }

        return sqlParams;
    }

    //1201基线后支持的接口---------------------------------------------------
    /**
     * 1201基线后支持的聚合执行接口
     * LOGIC:支持普通sql与预编译sql执行,支持带事务执行
     * EXTERNAL:支持普通sql与预编译sql执行,不支持带事务执行
     * @param sourceType 连接类型
     * @param groupId 连接id
     * @param sql sql语句
     * @param sqlParams 预编译sql参数
     * @param transId 事务id
     * @param startTrans 是否开启事务
     * @param commit 是否提交事务
     * @param rollback 是否回滚事务
     */
    public Map<String, Object> executeSqlWithTrans(SourceType sourceType, String groupId, String sql,
                                                   List<SqlParamEntity> sqlParams, String transId,
                                                   Boolean startTrans, Boolean commit, Boolean rollback) {
        //执行sql  参数sourceType   groupId  sql
        ExecuteSqlEntity executeSqlEntity = new ExecuteSqlEntity();
        executeSqlEntity.setSql(base64(sql));
        executeSqlEntity.setGroupId(groupId);
        executeSqlEntity.setSourceType(sourceType);

        //若通过占位符方式查询外部数据库,需增加此参数
        //占位符list 的顺序  要与 sql 的?占位符顺序一致
        if (sqlParams != null && !sqlParams.isEmpty()) {
            executeSqlEntity.setParams(sqlParams);
        }
        //设置事务id
        if (transId != null && !transId.isEmpty()) {
            executeSqlEntity.setTransactionId(transId);
        }
        //是否开启事务
        if (startTrans != null && startTrans) {
            executeSqlEntity.setStartTransaction(true);
        }
        //是否提交事务
        if (commit != null && commit) {
            executeSqlEntity.setCommitTransaction(true);
        }
        //是否回滚事务
        if (rollback != null && rollback) {
            executeSqlEntity.setRollbackTransaction(true);
        }

        //拼接参数
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("entity", JSONUtil.toJsonStr(executeSqlEntity));

        try {
            return commonRestService.postForForm("/sapi/secondev/ds/executeSqlAll", Map.class, valueMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

}
