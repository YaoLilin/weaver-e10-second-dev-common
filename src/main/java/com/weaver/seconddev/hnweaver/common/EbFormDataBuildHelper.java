package com.weaver.seconddev.hnweaver.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.weaver.common.form.metadata.field.FormField;
import com.weaver.ebuilder.form.client.entity.data.EBDataReqDetailDto;
import com.weaver.ebuilder.form.client.entity.data.EBDataReqDto;
import com.weaver.seconddev.hnweaver.common.bean.FormData;
import com.weaver.seconddev.hnweaver.common.bean.FormDetailData;
import com.weaver.seconddev.hnweaver.common.bean.FormFieldData;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.constants.FieldComponentKeys;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormFieldEntity;
import com.weaver.seconddev.hnweaver.common.exception.FieldNotFoundException;
import com.weaver.seconddev.hnweaver.common.exception.FormNotFoundException;
import com.weaver.seconddev.hnweaver.common.service.EbFieldConvertorGruopInterface;
import com.weaver.seconddev.hnweaver.common.service.FieldConvertor;
import com.weaver.seconddev.hnweaver.common.service.FieldInfoService;
import com.weaver.seconddev.hnweaver.common.service.impl.AbstractFileFieldConvertor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 姚礼林
 * @desc 构建eb表单导入数据
 * 可对不同字段类型进行转换，如果 {@link FormFieldData#convertFunc} 不为空，则使用此转换函数，
 * 其次如果 {@link #fieldConvertorGroup} 不为空且存在对应的字段类型转换器，则使用此转换器，最后如果是上传字段类型字段，
 * 使用 {@link #fileFieldConvertor} 进行转换。
 * @date 2025/9/16
 **/
@Slf4j
@RequiredArgsConstructor
@Component
final class EbFormDataBuildHelper {
    private final FieldInfoService fieldInfoService;
    /**
     * 可选传入字段转换器
     */
    @Setter
    private EbFieldConvertorGruopInterface fieldConvertorGroup;
    private AbstractFileFieldConvertor fileFieldConvertor;

    /**
     * 自定义文件上传字段转换
     * @param fileFieldConvertor  文件上传字段转换器
     */
    @Qualifier(value = "fileFieldConvertor")
    @Autowired
    void setFileFieldConvertor(AbstractFileFieldConvertor fileFieldConvertor) {
        this.fileFieldConvertor = fileFieldConvertor;
    }

    /**
     * 构建eb表单导入数据，生成导入接口接受的导入数据类型
     * @param formDataList 导入数据
     * @param formId 导入的表单id
     * @return eb表单导入数据
     */
    @NotNull List<EBDataReqDto> buildDataList(List<FormData> formDataList, long formId) {
        List<FormFieldEntity> fields = fieldInfoService.getFieldsByFormId(DatasourceGroupType.WEAVER_EBUILDER_FORM_SERVICE,
                formId);
        log.debug("表单字段信息：{}", JSON.toJSONString(fields));
        Map<String, FormFieldEntity> mainFormFieldMap = getMainFormFields(fields);
        Map<Long, Map<String, FormFieldEntity>> detailFormFieldMap = getDetailFieldMap(formDataList, fields);

        List<EBDataReqDto> dataList = new ArrayList<>();
        for (FormData formData : formDataList) {
            EBDataReqDto ebDataReqDto = buildEbDataReqDto(formData, mainFormFieldMap, detailFormFieldMap);
            dataList.add(ebDataReqDto);
        }

        return dataList;
    }

    /**
     * 根据表单ID获取主表字段信息
     */
    Map<String, FormFieldEntity> getMainFormFields(List<FormFieldEntity> fields) {
        return fields.stream()
                .filter(field -> field.getSubFormId() == null)
                .collect(Collectors.toMap(FormFieldEntity::getDataKey, field -> field));
    }

    /**
     * 根据表单ID获取主表字段信息
     */
    Map<String, FormFieldEntity> getMainFormFields(long formId) {
        List<FormFieldEntity> fields = fieldInfoService.getFieldsByFormId(DatasourceGroupType.WEAVER_EBUILDER_FORM_SERVICE,
                formId);
        return getMainFormFields(fields);
    }

    /**
     * 根据明细表ID获取明细表字段信息
     */
    Map<String, FormFieldEntity> getDetailFormFields(List<FormFieldEntity> fields, Long subFormId) {
        return fields.stream()
                .filter(field -> field.getSubFormId() != null
                        && field.getSubFormId().equals(subFormId))
                .collect(Collectors.toMap(FormFieldEntity::getDataKey, field -> field));
    }

    private Map<Long, Map<String, FormFieldEntity>> getDetailFieldMap(List<FormData> formDataList,
                                                                      List<FormFieldEntity> fields) {
        Map<Long, Map<String, FormFieldEntity>> detailFormFieldMap = new HashMap<>(3);
        if (CollUtil.isNotEmpty(formDataList)) {
            for (FormData formData : formDataList) {
                if (CollUtil.isNotEmpty(formData.getDetailFieldData())) {
                    for (FormDetailData detailData : formData.getDetailFieldData()) {
                        if (!detailFormFieldMap.containsKey(detailData.getSubFormId())) {
                            detailFormFieldMap.put(detailData.getSubFormId(),
                                    getDetailFormFields(fields, detailData.getSubFormId()));
                        }
                    }
                }
            }
        }
        return detailFormFieldMap;
    }

    private EBDataReqDto buildEbDataReqDto(FormData formData, Map<String, FormFieldEntity> mainFormFieldMap,
                                           Map<Long, Map<String, FormFieldEntity>> detailFormFieldMap) {
        // 主表数据参数
        List<EBDataReqDetailDto> mainData = buildMainFieldData(formData, mainFormFieldMap);

        // 明细数据参数
        Map<Long, List<List<EBDataReqDetailDto>>> detailDatas = buildDetailFieldData(formData, detailFormFieldMap);

        EBDataReqDto ebDataReqDto = new EBDataReqDto();
        ebDataReqDto.setMainDatas(mainData);
        ebDataReqDto.setDetailDatas(detailDatas);
        return ebDataReqDto;
    }

    private @NotNull Map<Long, List<List<EBDataReqDetailDto>>>
    buildDetailFieldData(FormData formData, Map<Long, Map<String, FormFieldEntity>> detailFormFieldMap) {
        Map<Long, List<List<EBDataReqDetailDto>>> detailDatas = new HashMap<>(3);
        if (formData.getDetailFieldData() != null) {
            // 遍历所有明细表
            for (FormDetailData detailData : formData.getDetailFieldData()) {
                List<List<EBDataReqDetailDto>> subFormDatas = new ArrayList<>();
                // 遍历所有明细表行
                for (List<FormFieldData> detailRow : detailData.getDetailFieldData()) {
                    // 遍历行中的所有字段，生成 EBDataReqDetailDto
                    List<EBDataReqDetailDto> subFormRowData = detailRow.stream().map(i -> {
                                if ("id".equalsIgnoreCase(i.getFieldName())) {
                                    return new EBDataReqDetailDto("id", i.getValue());
                                }
                                FormFieldEntity field = getDetailFieldInfo(detailData, i, detailFormFieldMap);
                                return buildFieldData(i, field);
                            })
                            .collect(Collectors.toList());
                    subFormDatas.add(subFormRowData);
                }
                detailDatas.put(detailData.getSubFormId(), subFormDatas);
            }
        }
        return detailDatas;
    }

    /**
     * 构建导入字段数据，并执行字段转换
     */
    private EBDataReqDetailDto buildFieldData(FormFieldData fieldData, FormFieldEntity field) {
        // 使用 CopyOptions 忽略空值和转换错误，避免将空字符串转换为枚举时出错
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreNullValue(true)
                .setIgnoreError(true);
        FormField formField = BeanUtil.toBean(field, FormField.class, copyOptions);
        // 如果字段里有转换函数，则使用转换函数
        if (fieldData.getConvertFunc() != null) {
            return fieldData.getConvertFunc().apply(fieldData, formField);
        }
        // 转换文件上传字段
        if (FieldComponentKeys.FILE.equals(field.getComponentKey())) {
            // 如果传入的字段转换器组里有文件上传字段转换器，则使用它进行转换
            if (this.fieldConvertorGroup != null) {
                List<FieldConvertor> convertors = this.fieldConvertorGroup.getConvertors();
                if (CollUtil.isNotEmpty(convertors)) {
                    Optional<FieldConvertor> fileConvertorOp = convertors.stream()
                            .filter(i -> i.getComponentKey().contains(FieldComponentKeys.FILE)).findAny();
                    if (fileConvertorOp.isPresent()) {
                        return fileConvertorOp.get().convert(fieldData, formField);
                    }
                }
            }
            // 使用默认或传入的文件上传字段转换器进行转换
            return fileFieldConvertor.convert(fieldData, formField);
        }

        // 使用传入的字段转换器组对不用类型的字段进行转换
        if (this.fieldConvertorGroup != null) {
            List<FieldConvertor> convertors = this.fieldConvertorGroup.getConvertors();
            for (FieldConvertor convertor : convertors) {
                if (convertor.getComponentKey().contains(field.getComponentKey())) {
                    return convertor.convert(fieldData, formField);
                }
            }
        }
        return new EBDataReqDetailDto(field.getId().toString(), fieldData.getValue());
    }

    private @NotNull List<EBDataReqDetailDto> buildMainFieldData(FormData formData,
                                                                 Map<String, FormFieldEntity> mainFormFieldMap) {
        List<EBDataReqDetailDto> mainData = new ArrayList<>();
        for (FormFieldData field : formData.getMainFieldData()) {
            if ("id".equalsIgnoreCase(field.getFieldName())) {
                mainData.add(new EBDataReqDetailDto("id", field.getValue()));
                continue;
            }
            FormFieldEntity fieldInfo = mainFormFieldMap.get(field.getFieldName());
            if (fieldInfo == null) {
                throw new FieldNotFoundException("该主表字段不存在：" + field.getFieldName());
            }
            mainData.add(buildFieldData(field, fieldInfo));
        }
        return mainData;
    }


    private @NotNull FormFieldEntity getDetailFieldInfo(FormDetailData detailData, FormFieldData fieldData,
                                                  Map<Long, Map<String, FormFieldEntity>> detailFormFieldMap) {
        if (!detailFormFieldMap.containsKey(detailData.getSubFormId())) {
            throw new FormNotFoundException("明细表不存在：" + detailData.getSubFormId());
        }
        Map<String, FormFieldEntity> fieldMap = detailFormFieldMap.get(detailData.getSubFormId());
        FormFieldEntity field = fieldMap.get(fieldData.getFieldName());
        if (field == null) {
            throw new FieldNotFoundException("明细表字段不存在：" + fieldData.getFieldName() + ",明细表id："
                    + detailData.getSubFormId());
        }
        return field;
    }

}
