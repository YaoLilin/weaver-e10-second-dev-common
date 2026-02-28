package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.weaver.common.form.dto.data.FormDataOptionDto;
import com.weaver.common.form.metadata.ModuleSource;
import com.weaver.common.form.metadata.field.FormField;
import com.weaver.common.hrm.service.HrmCommonDepartmentService;
import com.weaver.ebuilder.form.client.entity.data.EBDataReqDetailDto;
import com.weaver.seconddev.hnweaver.common.bean.FormFieldData;
import com.weaver.seconddev.hnweaver.common.constants.FieldComponentKeys;
import com.weaver.seconddev.hnweaver.common.service.FieldConvertor;
import com.weaver.teams.security.context.UserContext;
import com.weaver.teams.security.user.User;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.weaver.seconddev.hnweaver.common.util.FieldConvertHelpUtil.handleBrowserField;

/**
 * @author 姚礼林
 * @desc 部门字段转换
 * @date 2025/9/18
 **/
@RequiredArgsConstructor
@Component
@Slf4j
public class DepartmentFieldConvertor implements FieldConvertor {
    private final HrmCommonDepartmentService departmentService;
    @Setter
    private User user;

    @Override
    public EBDataReqDetailDto convert(FormFieldData fieldData, FormField field) {
        EBDataReqDetailDto dto = new EBDataReqDetailDto(field.getId().toString(), fieldData.getValue());
        if (!NumberUtil.isNumber(fieldData.getValue())) {
            List<FormDataOptionDto> options = handleDepartmentField(fieldData, fieldData.getValue());
            dto.setDataOptions(options);
        }

        return null;
    }

    @Override
    public List<String> getComponentKey() {
        return CollUtil.toList(FieldComponentKeys.DEPARTMENT);
    }

    private @NotNull List<FormDataOptionDto> handleDepartmentField(FormFieldData fieldData, String value) {
        return handleBrowserField(ModuleSource.department, value, v -> {
            Long depId = departmentService.queryByName(fieldData.getValue(), getTenantKey());
            log.info("转换部门字段，字段值：{},根据名称查询部门id，结果：{}", fieldData.getValue(), depId);
            return depId.toString();
        });
    }

    private String getTenantKey() {
        if (this.user != null) {
            return user.getTenantKey();
        }
        return UserContext.getCurrentUser().getTenantKey();
    }
}
