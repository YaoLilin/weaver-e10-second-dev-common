package com.weaver.seconddev.hnweaver.common.service;

import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 表单信息
 * @date 2025/9/28
 **/
public interface FormInfoService {

    /**
     * 根据表单ID获取表单信息
     *
     * @param groupType 数据库标识，用于在微服务找到对应的数据库
     * @param formId    表单id
     * @param tenantKey 租户key，不为空时只获取指定租户表单，传入空时不限定租户
     * @return 表单信息
     */
    Optional<FormEntity> getFormByFormId(DatasourceGroupType groupType, long formId, @Nullable String tenantKey);

    /**
     * 根据表单数据库表名获取表单信息
     *
     * @param groupType 数据库标识，用于在微服务找到对应的数据库
     * @param tableName 表名
     * @param tenantKey 租户key，不为空时只获取指定租户表单，传入空时不限定租户
     * @return 表单信息
     */
    Optional<FormEntity> getFormByTableName(DatasourceGroupType groupType, String  tableName,@Nullable String tenantKey);


    /**
     * 根据表名查询明细表id
     *
     * @param groupType 数据库标识，用于在微服务找到对应的数据库
     * @param tableName 明细表名
     * @param tenantKey 租户key，不为空时只获取指定租户表单，传入空时不限定租户
     * @return 明细表id
     */
    Optional<Long> getSubFormId(DatasourceGroupType groupType, String tableName,@Nullable String tenantKey);

    /**
     * 根据明细表名查询主表id
     *
     * @param groupType        数据库标识，用于在微服务找到对应的数据库
     * @param subFormTableName 明细表名
     * @param tenantKey        租户key，不为空时只获取指定租户表单，传入空时不限定租户
     * @return 明细表id
     */
    Optional<Long> getFormIdBySubFormName(DatasourceGroupType groupType, String subFormTableName,
                                          @Nullable String tenantKey);


}
