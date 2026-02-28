package com.weaver.seconddev.hnweaver.common.util;

import cn.hutool.core.collection.CollUtil;
import com.weaver.common.form.api.rest.FormRestFactory;
import com.weaver.common.form.api.rest.field.FormFieldRest;
import com.weaver.common.form.metadata.field.FormField;
import com.weaver.seconddev.hnweaver.common.constants.RpcGroup;
import com.weaver.teams.domain.user.SimpleEmployee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 表单字段工具类
 * @date 2025/9/26
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class FieldRpcUtil {
    private final FormRestFactory formRestFactory;

    /**
     * 根据字段名称获取字段
     * @param rpcGroup RPC组标识，用于在微服务指定对应的服务
     * @param formId 表单id
     * @param subFormId 明细表id，如果是明细字段则必需传入，主表字段可以为空
     * @param fieldName 字段名称
     * @param employee 用户
     * @return 字段信息
     */
    public Optional<FormField> getFieldByName(RpcGroup rpcGroup, long formId, @Nullable Long subFormId,
                                              String  fieldName,
                                              SimpleEmployee employee) {
        Map<Long, FormField> fieldMap = getFieldMapByForm(rpcGroup, formId, employee);
        log.debug("是否有字段数据：{}", CollUtil.isNotEmpty(fieldMap));

        return fieldMap.values().stream().filter(i -> {
                    log.debug("字段名称：{}", i.getDataKey());
                    if (subFormId != null && i.getSubForm() != null) {
                        log.debug("明细表字段，明细表id：{}", i.getSubForm().getId());
                        return i.getDataKey().equals(fieldName) &&
                                subFormId.equals(i.getSubForm().getId());
                    }
                    return i.getDataKey().equals(fieldName);
                })
                .findAny();
    }

    /**
     * 根据表单获取该表所有字段，包括明细字段
     * @param rpcGroup RPC组标识，用于在微服务指定对应的服务
     * @param formId 表单id
     * @param employee 用户
     * @return 字段信息，包括明细字段，map形式，key 为字段id
     */
    public Map<Long, FormField> getFieldMapByForm(RpcGroup rpcGroup, long formId, SimpleEmployee employee) {
        FormFieldRest formFieldRest = formRestFactory.getFormFieldRest(rpcGroup.getValue());
        return formFieldRest.getFieldMapByFormId(formId, employee);
    }
}
