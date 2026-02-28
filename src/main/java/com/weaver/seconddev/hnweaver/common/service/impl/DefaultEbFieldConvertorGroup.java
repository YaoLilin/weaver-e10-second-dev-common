package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.weaver.seconddev.hnweaver.common.service.EbFieldConvertorGruopInterface;
import com.weaver.seconddev.hnweaver.common.service.FieldConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 默认的eb表单字段转换
 * @date 2025/9/20
 **/
@RequiredArgsConstructor
@Component
public class DefaultEbFieldConvertorGroup implements EbFieldConvertorGruopInterface {
    private final FileFieldConvertor fileFieldConvertor;
    private final EmployeeFieldConvertor employeeFieldConvertor;
    private final DepartmentFieldConvertor departmentFieldConvertor;
    private final SelectorFieldConvertor selectorFieldConvertor;


    @Override
    public List<FieldConvertor> getConvertors() {
        return CollUtil.toList(fileFieldConvertor,employeeFieldConvertor,departmentFieldConvertor,selectorFieldConvertor);
    }
}
