package com.weaver.seconddev.hnweaver.common.service;

import com.weaver.common.form.metadata.field.FormField;
import com.weaver.ebuilder.form.client.entity.data.EBDataReqDetailDto;
import com.weaver.seconddev.hnweaver.common.bean.FormFieldData;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 处理表单导入的字段转换
 * @date 2025/9/18
 **/
public interface FieldConvertor {

    /**
     * 执行转换
     *
     * @param fieldData 字段数据
     * @param field     字段对象
     * @return 转换结果，用于导入表单数据
     */
    EBDataReqDetailDto convert(FormFieldData fieldData, FormField field);

    /**
     * 获取组件key，对应 {@link FormField#getComponentKey()}，如果此转换适配多个组件，则返回多个组件的key
     * @return 组件key
     */
    List<String> getComponentKey();
}
