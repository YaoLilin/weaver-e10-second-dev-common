package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.weaver.common.form.dto.data.FormDataOptionDto;
import com.weaver.common.form.metadata.ModuleSource;
import com.weaver.common.form.metadata.field.FormField;
import com.weaver.common.hrm.service.HrmCommonEmployeeService;
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
 * @desc 人力资源字段转换
 * @date 2025/9/18
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class EmployeeFieldConvertor implements FieldConvertor {
    private final HrmCommonEmployeeService employeeService;
    @Setter
    private User user;

    @Override
    public EBDataReqDetailDto convert(FormFieldData fieldData, FormField field) {
        EBDataReqDetailDto dto = new EBDataReqDetailDto(field.getId().toString(), fieldData.getValue());
        if (!NumberUtil.isNumber(fieldData.getValue())) {
            List<FormDataOptionDto> options = handleEmployeeField(fieldData.getValue());
            dto.setDataOptions(options);
        }
        return null;
    }

    @Override
    public List<String> getComponentKey() {
        return CollUtil.toList(FieldComponentKeys.EMPLOYEE);
    }

    private @NotNull List<FormDataOptionDto> handleEmployeeField(String value) {
        return handleBrowserField(ModuleSource.resource, value, v -> {
            List<Long> empIds = employeeService.getEmpIdsByUserName(v,
                    getTenantKey());
            if (empIds.size() > 1) {
                log.warn("根据姓名在系统中找到多条用户信息，姓名：{}", v);
            }
            return empIds.get(0).toString();
        });
    }

    private String getTenantKey() {
        if (this.user != null) {
            return user.getTenantKey();
        }
        return UserContext.getCurrentUser().getTenantKey();
    }
}
