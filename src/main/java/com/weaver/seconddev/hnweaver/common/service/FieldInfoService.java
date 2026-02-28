package com.weaver.seconddev.hnweaver.common.service;

import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormFieldEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 表单字段信息
 * @date 2025/9/28
 **/
public interface FieldInfoService {

    /**
     * 获取字段选项名称
     *
     * @param groupType 数据库组类型，用于在微服务找到正确的数据库
     * @param fieldId   字段id
     * @param value     字段值
     * @return 选项名称
     */
    String getFieldOptionName(DatasourceGroupType groupType, long fieldId, String value);

    /**
     * 根据表单id获取字段信息
     *
     * @param groupType 数据库组类型，用于在微服务找到正确的数据库
     * @param formId    表单id
     * @return 字段信息
     */
    List<FormFieldEntity> getFieldsByFormId(DatasourceGroupType groupType, long formId);

    /**
     * 根据表单id和字段名称获取字段信息
     *
     * @param groupType 数据库组类型，用于在微服务找到正确的数据库
     * @param formId    表单id
     * @param subFormId 明细表id，如果是明细字段则需要指定
     * @param fieldName 字段名称
     * @return 字段信息
     */
    Optional<FormFieldEntity> getFieldByName(DatasourceGroupType groupType, long formId, @Nullable Long subFormId,
                                             String fieldName);

    /**
     * 根据字段id获取字段信息
     * @param groupType  数据库组类型，用于在微服务找到正确的数据库
     * @param fieldId   字段id
     * @return 字段信息
     */
    Optional<FormFieldEntity> getFieldById(DatasourceGroupType groupType, long fieldId);
}
