package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.service.WorkflowInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
}

