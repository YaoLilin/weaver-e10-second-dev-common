package com.weaver.seconddev.hnweaver.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc sql 工具类
 * @date 2023/9/5
 */
@UtilityClass
public class SqlUtil {
    public static final String NO_DELETE = "IS_DELETE=0 ";

    /**
     * 生成查询sql
     * @param fieldNames 查询字段名集合
     * @param tableName 表名
     * @return 查询sql
     */
    @NotNull
    public static String buildQuerySql(List<String> fieldNames, String tableName) {
        return buildQuerySql(fieldNames, tableName, null);
    }

    /**
     * 生成查询sql
     * @param fieldNames 查询字段名集合
     * @param tableName 表名
     * @param tableAlias 表别名
     * @return 查询sql
     */
    @NotNull
    public static String buildQuerySql(List<String> fieldNames, String tableName, @Nullable String tableAlias) {
        if (CollUtil.isEmpty(fieldNames) || StrUtil.isBlank(tableName)) {
            return "";
        }
        StringBuilder sql = new StringBuilder("SELECT ");
        fieldNames.forEach(i -> {
            if (StrUtil.isNotBlank(tableAlias)) {
                sql.append(tableAlias).append(".").append(i).append(",");
            }else {
                sql.append(i).append(",");
            }
        });
        sql.delete(sql.length() - 1, sql.length());
        sql.append(" FROM ").append(tableName);
        if (StrUtil.isNotBlank(tableAlias)) {
            sql.append(" ").append(tableAlias);
        }
        return sql.toString();
    }

    /**
     * 生成sql更新语句的set部分，例如 name=?,age=?，语句里使用了占位符，执行时请使用参数进行替换
     *
     * @param fieldNames 字段名
     * @return sql set语句
     */
    public static String buildUpdateSql(List<String> fieldNames) {
        StringBuilder sql = new StringBuilder();
        fieldNames.forEach(i -> sql.append(i).append("=?").append(","));
        sql.delete(sql.length() - 1, sql.length());
        return sql.toString();
    }

    /**
     * 生成sql更新语句，不包括where部分，例如 update table_name1 set name=?,age=?，语句里使用了占位符，执行时请使用参数进行替换
     *
     * @param tableName  更新表名
     * @param fieldNames 字段名
     * @return sql set语句
     */
    public static String buildUpdateSql(String tableName, List<String> fieldNames) {
        return "update " + tableName + " set " + buildUpdateSql(fieldNames);
    }

    /**
     * 生成sql插入语句,语句中字段的值用?占位符表示
     *
     * @param fieldNames 字段名
     * @return sql 插入语句
     */
    public static String buildInsertSql(String tableName, List<String> fieldNames) {
        StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
        fieldNames.forEach(i -> sql.append(i).append(","));
        sql.delete(sql.length() - 1, sql.length());
        sql.append(") values(");
        for (int i = 0; i < fieldNames.size(); i++) {
            sql.append("?,");
        }
        sql.delete(sql.length() - 1, sql.length());
        sql.append(")");
        return sql.toString();
    }

    /**
     * 构件相等条件的where语句<br>
     * 如果没有条件语句，泽返回空串，表示没有条件
     *
     * @param conditions  条件，key为字段名，value为字段值
     * @param paramValues sql 占位符对应的值，当生成条件语句时会将条件中的值插入到此列表中
     * @return 带where关键字的SQL部分
     */
    public static String buildEqualsWhere(Map<String, Object> conditions, List<Object> paramValues) {
        if (CollUtil.isEmpty(conditions)) {
            return StrUtil.EMPTY;
        }
        Entity entity = Entity.create();
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            entity.set(entry.getKey(), entry.getValue());
        }
        final StringBuilder sb = new StringBuilder(" WHERE ");
        boolean isNotFirst = false;
        for (Map.Entry<String, Object> entry : entity.entrySet()) {
            if (isNotFirst) {
                sb.append(" and ");
            } else {
                isNotFirst = true;
            }
            sb.append(entry.getKey()).append(" = ?");
            paramValues.add(entry.getValue());
        }
        return sb.toString();
    }
}
