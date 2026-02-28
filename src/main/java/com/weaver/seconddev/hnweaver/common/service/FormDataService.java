package com.weaver.seconddev.hnweaver.common.service;

import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;

/**
 * @author 姚礼林
 * @desc 工作流表单数据服务接口
 * @date 2025/12/9
 **/
public interface FormDataService {

    /**
     * 查询表单字段值
     *
     * @param groupType 数据源组，根据数据库表所在服务，选择组类型
     * @param tableName 表名
     * @param fieldName 字段名
     * @param dataId 数据ID
     * @return 字段值，如果查询不到则返回null
     */
    String getFieldValue(DatasourceGroupType groupType, String tableName, String fieldName, long dataId);

    /**
     * 更新表单字段值
     *
     * @param groupType 数据源组，根据数据库表所在服务，选择组类型
     * @param tableName 表名
     * @param fieldName 字段名
     * @param fieldValue 字段值
     * @param dataId 数据ID
     * @return 是否更新成功
     */
    boolean updateFieldValue(DatasourceGroupType groupType,String tableName, String fieldName,
                             String fieldValue, long dataId);

    /**
     * 检查字段值是否存在
     *
     * @param groupType 数据源组，根据数据库表所在服务，选择组类型
     * @param tableName 表名
     * @param fieldName 字段名
     * @param fieldValue 字段值
     * @return 是否存在
     */
    boolean checkFieldValueExists(DatasourceGroupType groupType,String tableName, String fieldName, String fieldValue);


}

