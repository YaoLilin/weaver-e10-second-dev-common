package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.weaver.common.form.metadata.field.FormField;
import com.weaver.ebuilder.form.client.entity.data.EBDataReqDetailDto;
import com.weaver.seconddev.hnweaver.common.bean.FormFieldData;
import com.weaver.seconddev.hnweaver.common.constants.FieldComponentKeys;
import com.weaver.seconddev.hnweaver.common.service.FieldConvertor;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 选择类型字段转换
 * @date 2025/9/18
 **/
public class SelectorFieldConvertor implements FieldConvertor {
    @Override
    public EBDataReqDetailDto convert(FormFieldData fieldData, FormField field) {
        return new EBDataReqDetailDto(field.getId().toString(), fieldData.getValue(), true);
    }

    @Override
    public List<String> getComponentKey() {
        return CollUtil.toList(FieldComponentKeys.RADIO_BOX,FieldComponentKeys.CHECK_BOX,
                FieldComponentKeys.SELECT,FieldComponentKeys.SELECT_MULTIPLE);
    }
}
