package com.weaver.seconddev.hnweaver.common;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.weaver.common.form.dto.enums.DataUpdatePolicy;
import com.weaver.ebuilder.form.client.entity.data.*;
import com.weaver.ebuilder.form.client.service.data.RemoteSimpleDataService;
import com.weaver.ebuilder.teams.etform.base.query.ConditionTreeDto;
import com.weaver.framework.rpc.annotation.RpcReference;
import com.weaver.seconddev.hnweaver.common.bean.FormData;
import com.weaver.seconddev.hnweaver.common.bean.FormDetailData;
import com.weaver.seconddev.hnweaver.common.bean.FormFieldData;
import com.weaver.seconddev.hnweaver.common.bean.ResultAndMsg;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.constants.DetailUpdateType;
import com.weaver.seconddev.hnweaver.common.constants.RpcGroupConstants;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormEntity;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormFieldEntity;
import com.weaver.seconddev.hnweaver.common.exception.FieldNotFoundException;
import com.weaver.seconddev.hnweaver.common.service.EbFieldConvertorGruopInterface;
import com.weaver.seconddev.hnweaver.common.service.FormInfoService;
import com.weaver.seconddev.hnweaver.common.service.impl.AbstractFileFieldConvertor;
import com.weaver.teams.domain.user.SimpleEmployee;
import com.weaver.teams.security.context.UserContext;
import com.weaver.teams.security.user.User;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author 姚礼林
 * @desc 导入ebuilder表单数据，请参考 <a href="https://weapp.eteams.cn/build/techdoc/wdoc/index.html#/public/doc/c98b2645-ea0a-465e-a00c-19b36aa50c4b">数据操作相关接口</a> <br>
 * 注意：此方式是使用 RPC 接口进行数据修改，问题可能会比较多，最好是使用 OPEN API 进行数据修改。<br>
 * 可自定义字段转换，如果 {@link FormFieldData#convertFunc} 不为空，则使用此转换函数，
 * 其次如果 {@link #fieldConvertorGroup} 不为空且存在对应的字段类型转换器，则使用此转换器，最后如果是上传字段类型字段，
 * 使用 {@link #fileFieldConvertor} 进行转换，对于文件上传类型字段，也遵循上面的优先级顺序，
 * 最后才使用{@link #fileFieldConvertor} 进行转换。
 * @date 2025/8/1
 **/
@Component
@Slf4j
@RequiredArgsConstructor
@Setter
public class EbFormDataChangeClient {
    private final EbFormDataBuildHelper formDataBuildHelper;
    private final FormInfoService formInfoService;
    @RpcReference(group = RpcGroupConstants.EBUILDER_FORM)
    private final RemoteSimpleDataService remoteSimpleDataService;
    /**
     * 是否异步处理数据，若数据写入后就要从前台看到数据或者立即操作本次写入的数据, 需要调整后置处理为同步，权限处理完成再返回
     */
    private boolean asyncPostProcess = false;

    /**
     * 字段转换器组，对应不同类型字段的转换器，如果不传入则不进行转换（除文件上传字段外，此字段有独立的字段转换器）
     */
    private EbFieldConvertorGruopInterface fieldConvertorGroup;

    /**
     * 自定义文件上传字段转换，如不传入则使用默认的文件上传字段转换处理。
     */
    private AbstractFileFieldConvertor fileFieldConvertor;

    /**
     * 保存表单数据，只限同一个表单id
     *
     * @param formDataList 保存表单数据
     * @param operateUser  操作用户，如果为空则为当前用户
     * @param formId       表单id，对应 Form 对象的 formId
     */
    public ResultAndMsg batchInsert(List<FormData> formDataList, long formId, @Nullable SimpleEmployee operateUser) {
        try {
            Optional<Long> formDataIdOp = getFormDataId(formId);
            if (!formDataIdOp.isPresent()) {
                return new ResultAndMsg(false, "无法获取表单信息");
            }
            Long formDataId = formDataIdOp.get();
            initDataBuildHelper();
            log.info("开始保存表单数据：{}", JSON.toJSONString(formDataList));
            log.info("表单ID：{},表单数据id：{}", formId, formDataId);

            EBDataChangeReqDto ebDataChangeReqDto = getEBDataChangeReqDto(formDataId.toString(), operateUser);
            // 构建导入数据
            List<EBDataReqDto> dataList = formDataBuildHelper.buildDataList(formDataList, formId);
            ebDataChangeReqDto.setDatas(dataList);
            // 调用RPC接口保存数据
            EBDataChangeResult result = remoteSimpleDataService.saveFormData(ebDataChangeReqDto);
            log.info("rpc接口结果：{}", JSON.toJSONString(result));
            if (!result.getStatus()) {
                log.error("保存表单数据失败，{}", result);
                return new ResultAndMsg(false, result.getMessage());
            }
            log.info("表单数据保存成功，数据ID：{}", result.getDataIds());
            return new ResultAndMsg(true, "成功");
        } catch (Exception e) {
            log.error("保存表单数据失败", e);
            return new ResultAndMsg(false, "保存表单数据发生异常");
        }
    }

    /**
     * 批量更新表单数据,最大支持1000条数据
     *
     * @param formDataList 需要更新的表单数据
     * @param formId       表单id，对应 Form 对象的 formId
     * @param operation    更新操作，可指定更新模式
     * @param operateUser  操作用户，如果为空则为当前用户
     * @return 更新结果
     */
    public ResultAndMsg batchUpdate(List<FormData> formDataList, long formId,
                                    @Nullable EBDataReqOperation operation, DetailUpdateType detailUpdateType,
                                    @Nullable SimpleEmployee operateUser) {
        try {
            Optional<Long> formDataIdOp = getFormDataId(formId);
            if (!formDataIdOp.isPresent()) {
                return new ResultAndMsg(false, "无法获取表单信息");
            }
            Long formDataId = formDataIdOp.get();

            initDataBuildHelper();
            log.info("开始修改表单数据：{}", JSON.toJSONString(formDataList));
            log.info("表单ID：{},表单数据id：{}", formId, formDataId);
            EBDataReqOperation reqOperation;
            if (operation != null) {
                reqOperation = operation;
            } else {
                reqOperation = new EBDataReqOperation();
                reqOperation.setAsyncPostProcess(this.asyncPostProcess);
            }

            // 设置明细操作方式
            EBDataReqOperationInfo detailOperation = buildDetailOperation(detailUpdateType);
            Map<Long, EBDataReqOperationInfo> detailDatas = buildDetailOperationData(formDataList, detailOperation);
            reqOperation.setDetailDatas(detailDatas);

            // 构建导入数据
            List<EBDataReqDto> dataList = formDataBuildHelper.buildDataList(formDataList, formId);

            EBDataChangeReqDto ebDataChangeReqDto = getEBDataChangeReqDto(formDataId.toString(),operateUser);
            ebDataChangeReqDto.setOperation(reqOperation);
            ebDataChangeReqDto.setDatas(dataList);
            EBDataChangeResult result = remoteSimpleDataService.updateFormData(ebDataChangeReqDto);
            log.info("rpc接口结果：{}", JSON.toJSONString(result));
            if (!result.getStatus()) {
                log.error("修改表单数据失败，{}", result);
                return new ResultAndMsg(false, result.getMessage());
            }
            log.info("表单数据修改成功，数据ID：{}", result.getDataIds());
            return new ResultAndMsg(true, "成功");
        } catch (Exception e) {
            log.info("修改表单数据失败",e);
            log.error("修改表单数据失败", e);
            return new ResultAndMsg(false, "修改表单数据发生异常:" + e.getMessage());
        }
    }

    /**
     * 批量修改表单数据，根据id进行更新，更新数据中必需传入id。最大支持1000条数据
     *
     * @param formDataList       要修改的表单数据
     * @param formId             表单数据ID，对应 Form 对象的 formId
     * @param insertWhenNotExist 更新条件不符合是否插入
     * @param detailUpdateType   更新行为
     * @param operateUser  操作用户，如果为空则为当前用户
     * @return 更新结果
     */
    public ResultAndMsg batchUpdateById(List<FormData> formDataList, long formId,
                                        boolean insertWhenNotExist, DetailUpdateType detailUpdateType,
                                        SimpleEmployee operateUser) {
        EBDataReqOperation operation = new EBDataReqOperation();
        operation.setUpdateType(EBDataUpdateType.ids);
        operation.setAsyncPostProcess(this.asyncPostProcess);
        if (insertWhenNotExist) {
            EBDataReqOperationInfo operationInfo = new EBDataReqOperationInfo();
            operationInfo.setNeedAdd(true);
            operation.setMainData(operationInfo);
        }
        return this.batchUpdate(formDataList, formId, operation, detailUpdateType, operateUser);
    }

    /**
     * 根据条件批量更新表单数据。最大支持1000条数据
     *
     * @param formDataList       表单数据
     * @param formId             表单id，对应 Form 对象的 formId
     * @param conditionTreeDto   更新条件
     * @param insertWhenNotExist 当表单数据不存在时是否插入
     * @param detailUpdateType   更新类型
     * @param operateUser  操作用户，如果为空则为当前用户
     * @return ResultAndMsg 批量更新结果
     */
    public ResultAndMsg batchUpdateByCondition(List<FormData> formDataList, long formId,
                                               ConditionTreeDto conditionTreeDto, boolean insertWhenNotExist,
                                               DetailUpdateType detailUpdateType,SimpleEmployee operateUser) {

        EBDataReqOperationInfo operationInfo = new EBDataReqOperationInfo();
        operationInfo.setConditionTreeDto(conditionTreeDto);
        operationInfo.setNeedAdd(insertWhenNotExist);
        EBDataReqOperation operation = new EBDataReqOperation();
        operation.setAsyncPostProcess(this.asyncPostProcess);
        operation.setUpdateType(EBDataUpdateType.conditions);
        operation.setMainData(operationInfo);
        return this.batchUpdate(formDataList, formId, operation, detailUpdateType, operateUser);
    }

    /**
     * 根据条件字段批量更新表单数据，需指定更新条件字段，例如 WHERE a=1 AND b=2，并且传入的更新数据中需要包含更新条件字段。
     * 最大支持1000条数据
     *
     * @param formDataList       表单数据
     * @param formId             表单id，对应 Form 对象的 formId
     * @param conditionFields    更新条件字段名
     * @param insertWhenNotExist 当表单数据不存在时是否插入
     * @param detailUpdateType   更新类型
     * @param operateUser  操作用户，如果为空则为当前用户
     * @return ResultAndMsg 批量更新结果
     */
    public ResultAndMsg batchUpdateByConditionFields(List<FormData> formDataList, long formId,
                                               List<String > conditionFields, boolean insertWhenNotExist,
                                               DetailUpdateType detailUpdateType,SimpleEmployee operateUser) {
        Map<String, FormFieldEntity> mainFormFields = formDataBuildHelper.getMainFormFields(formId);
        // 构造更新条件字段id
        List<String> updateConditionFieldIds = new ArrayList<>();
        for (String fieldName : conditionFields) {
            FormFieldEntity field = mainFormFields.get(fieldName);
            if (field == null) {
                throw new FieldNotFoundException("更新条件字段找不到，字段名：" + fieldName);
            }
            updateConditionFieldIds.add(field.getId().toString());
        }

        EBDataReqOperationInfo operationInfo = new EBDataReqOperationInfo();
        operationInfo.setUpdateFieldIds(updateConditionFieldIds);
        operationInfo.setNeedAdd(insertWhenNotExist);
        EBDataReqOperation operation = new EBDataReqOperation();
        operation.setAsyncPostProcess(this.asyncPostProcess);
        operation.setUpdateType(EBDataUpdateType.updatePolicy);
        operation.setMainData(operationInfo);
        return this.batchUpdate(formDataList, formId, operation, detailUpdateType, operateUser);
    }

    private Optional<Long> getFormDataId(long formId) {
        Optional<FormEntity> formEntityOptional = formInfoService
                .getFormByFormId(DatasourceGroupType.WEAVER_EBUILDER_FORM_SERVICE, formId, null);
        if (!formEntityOptional.isPresent()) {
            return Optional.empty();
        }
        FormEntity formEntity = formEntityOptional.get();
        return Optional.of(formEntity.getTargetId());
    }

    private void initDataBuildHelper() {
        if (this.fileFieldConvertor != null) {
            formDataBuildHelper.setFileFieldConvertor(this.fileFieldConvertor);
        }
        if (this.fieldConvertorGroup != null) {
            formDataBuildHelper.setFieldConvertorGroup(this.fieldConvertorGroup);
        }
    }

    private static @NotNull Map<Long, EBDataReqOperationInfo> buildDetailOperationData(List<FormData> formDataList,
                                                                                       EBDataReqOperationInfo detailOperation) {
        Map<Long, EBDataReqOperationInfo> detailDatas = new HashMap<>(4);
        for (FormData formData : formDataList) {
            List<FormDetailData> detailFieldData = formData.getDetailFieldData();
            if (CollUtil.isNotEmpty(detailFieldData)) {
                for (FormDetailData detailData : detailFieldData) {
                    if (!detailDatas.containsKey(detailData.getSubFormId())) {
                        detailDatas.put(detailData.getSubFormId(), detailOperation);
                    }
                }
            }
        }
        return detailDatas;
    }

    private static @NotNull EBDataReqOperationInfo buildDetailOperation(DetailUpdateType detailUpdateType) {
        EBDataReqOperationInfo detailOperation = new EBDataReqOperationInfo();
        switch (detailUpdateType) {
            case INSERT:
                detailOperation.setCover(false);
                detailOperation.setNeedAdd(true);
                break;
            case UPDATE:
                detailOperation.setNeedAdd(false);
                detailOperation.setUpdatePolicy(DataUpdatePolicy.ALL);
                break;
            case INSERT_OR_UPDATE:
                detailOperation.setNeedAdd(true);
                detailOperation.setUpdatePolicy(DataUpdatePolicy.ALL);
                break;
            case COVER:
                detailOperation.setCover(true);
                detailOperation.setNeedAdd(true);
                break;
            case DELETE:
                detailOperation.setUpdatePolicy(DataUpdatePolicy.DELETE);
                break;
            case DELETE_ALL:
                detailOperation.setCover(true);
                detailOperation.setUpdatePolicy(DataUpdatePolicy.DELETE);
                break;
            default:
        }
        return detailOperation;
    }

    private  @NotNull EBDataChangeReqDto getEBDataChangeReqDto(String formDataId ,@Nullable User operatorUser) {
        EBDataChangeReqDto ebDataChangeReqDto = new EBDataChangeReqDto();
        if (operatorUser == null) {
            operatorUser = UserContext.getCurrentUser();
        }
        ebDataChangeReqDto.setHeader(new EBDataReqHeader(formDataId,
                operatorUser.getEmployeeId().toString(), operatorUser.getTenantKey()));
        return ebDataChangeReqDto;
    }

}
