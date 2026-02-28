package com.weaver.seconddev.hnweaver.common.service;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 获取eb表单字段转换
 * @date 2025/9/20
 **/
public interface EbFieldConvertorGruopInterface {

    /**
     * 获取不同类型的字段转换器
     * @return 集合里包含不同类型的字段转换器
     */
    List<FieldConvertor> getConvertors();

}
