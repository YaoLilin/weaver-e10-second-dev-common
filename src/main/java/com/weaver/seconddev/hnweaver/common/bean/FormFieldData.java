package com.weaver.seconddev.hnweaver.common.bean;

import com.weaver.common.form.metadata.field.FormField;
import com.weaver.ebuilder.form.client.entity.data.EBDataReqDetailDto;
import lombok.Data;

import java.util.function.BiFunction;

/**
 * @author 姚礼林
 * @desc 表单字段信息
 * @date 2025/7/31
 **/
@Data
public class FormFieldData {
    private String fieldName;
    private String value;
    private boolean isFile;
    private String filePath;
    private BiFunction<FormFieldData, FormField, EBDataReqDetailDto> convertFunc;

    public FormFieldData() {
    }

    public FormFieldData(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public FormFieldData(String fieldName, String value, BiFunction<FormFieldData, FormField,
                EBDataReqDetailDto> convertFunc) {
        this.fieldName = fieldName;
        this.value = value;
        this.convertFunc = convertFunc;
    }
}
