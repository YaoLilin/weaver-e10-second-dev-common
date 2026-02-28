package com.weaver.seconddev.hnweaver.common.bean;

import lombok.Data;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 导入eb表单的明细数据
 * @date 2025/7/31
 **/
@Data
public class FormDetailData {
    private Long subFormId;
    private List<List<FormFieldData>> detailFieldData;
}
