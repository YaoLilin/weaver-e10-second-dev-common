package com.weaver.seconddev.hnweaver.common.bean;

import lombok.Data;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 导入eb表单的数据
 * @date 2025/7/31
 **/
@Data
public class FormData {
    private Long formId;
    private List<FormFieldData> mainFieldData;
    private List<FormDetailData> detailFieldData;
}
