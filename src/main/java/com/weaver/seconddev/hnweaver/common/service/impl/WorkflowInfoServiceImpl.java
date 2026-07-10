package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormEntity;
import com.weaver.seconddev.hnweaver.common.service.FormInfoService;
import com.weaver.seconddev.hnweaver.common.service.WorkflowInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 工作流信息服务实现类
 * @date 2025/12/9
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowInfoServiceImpl implements WorkflowInfoService {
    private final SqlExecuteClient sqlExecuteClient;
    private final FormInfoService formInfoService;

    @Override
    public Long getDataIdByRequestId(long requestId) {
        String sql = "SELECT dataid FROM wfc_form_data WHERE requestid = ?";
        SqlExecuteResult result = sqlExecuteClient.executeSql(
                DatasourceGroupType.WEAVER_WORKFLOW_LIST_SERVICE, sql, requestId);

        if (!result.isSuccess()) {
            log.error("查询dataId失败，sql：{}，错误信息：{}", sql, result.getMessage());
            return null;
        }

        List<Map<String, Object>> records = result.getRecords();
        if (records.isEmpty()) {
            log.warn("根据requestId {} 查询不到dataId", requestId);
            return null;
        }

        String dataIdStr = SqlExecuteClient.getFieldValueIgnoreCase(records.get(0), "dataid");
        if (CharSequenceUtil.isBlank(dataIdStr)) {
            return null;
        }

        try {
            return Long.parseLong(dataIdStr);
        } catch (NumberFormatException e) {
            log.error("dataId格式错误：{}", dataIdStr, e);
            return null;
        }
    }

    @Override
    public Optional<Long> getFormIdByRequestId(long requestId) {
        Optional<Long> workflowIdOp = getWorkflowIdByRequestId(requestId);
        if (!workflowIdOp.isPresent()) {
            log.warn("无法获取requestId {} 对应的workflowId，无法查询formId", requestId);
            return Optional.empty();
        }

        Long workflowId = workflowIdOp.get();
        String sql = "SELECT relatekey FROM wfp_relateform WHERE workflowid = ?";
        SqlExecuteResult result = sqlExecuteClient.executeSql(
                DatasourceGroupType.WEAVER_WORKFLOW_LIST_SERVICE, sql, workflowId);
        if (!result.isSuccess()) {
            log.error("查询formId失败，sql：{}，错误信息：{}", sql, result.getMessage());
            return Optional.empty();
        }

        List<Map<String, Object>> records = result.getRecords();
        if (records.isEmpty()) {
            log.warn("根据workflowId {} 查询不到formId", workflowId);
            return Optional.empty();
        }

        String formIdStr = SqlExecuteClient.getFieldValueIgnoreCase(records.get(0), "relatekey");
        if (CharSequenceUtil.isBlank(formIdStr)) {
            log.warn("根据workflowId {} 查询到的formId为空", workflowId);
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(formIdStr));
        } catch (NumberFormatException e) {
            log.error("formId格式错误：{}", formIdStr, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> getWorkflowIdByRequestId(long requestId) {
        String sql = "SELECT workflowid FROM wfc_requestbase WHERE requestid = ?";
        SqlExecuteResult result = sqlExecuteClient.executeSql(
                DatasourceGroupType.WEAVER_WORKFLOW_LIST_SERVICE, sql, requestId);
        if (!result.isSuccess()) {
            log.error("查询workflowId失败，sql：{}，错误信息：{}", sql, result.getMessage());
            return Optional.empty();
        }

        List<Map<String, Object>> records = result.getRecords();
        if (records.isEmpty()) {
            log.warn("根据requestId {} 查询不到workflowId", requestId);
            return Optional.empty();
        }

        String workflowIdStr = SqlExecuteClient.getFieldValueIgnoreCase(records.get(0), "workflowid");
        if (CharSequenceUtil.isBlank(workflowIdStr)) {
            log.warn("根据requestId {} 查询到的workflowId为空", requestId);
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(workflowIdStr));
        } catch (NumberFormatException e) {
            log.error("workflowId格式错误：{}", workflowIdStr, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getFormTableNameByRequestId(long requestId) {
        Optional<Long> formIdOpt = getFormIdByRequestId(requestId);
        if (!formIdOpt.isPresent()) {
            log.warn("无法获取requestId {} 对应的formId，无法查询表名", requestId);
            return Optional.empty();
        }

        Long formId = formIdOpt.get();
        Optional<FormEntity> formEntityOp = formInfoService.getFormByFormId(DatasourceGroupType.WEAVER_WORKFLOW_LIST_SERVICE,
                formId, null);
        if (!formEntityOp.isPresent()) {
            log.warn("无法获取formId {} 对应的FormEntity，无法查询表名", formId);
            return Optional.empty();
        }

        return Optional.ofNullable(formEntityOp.get().getTableName());
    }
}
