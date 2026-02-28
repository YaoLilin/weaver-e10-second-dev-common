package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.exception.SqlExecuteException;
import com.weaver.seconddev.hnweaver.common.service.FormDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 姚礼林
 * @desc 工作流表单数据服务实现类
 * @date 2025/12/9
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class FormDataServiceImpl implements FormDataService {
    private final SqlExecuteClient sqlExecuteClient;

    @Override
    public String getFieldValue(DatasourceGroupType groupType,String tableName, String fieldName, long dataId) {
        String sql = "SELECT " + fieldName + " FROM " + tableName + " WHERE id = ?";
        SqlExecuteResult result = sqlExecuteClient.executeSql(groupType, sql, dataId);

        if (!result.isSuccess()) {
            log.error("查询字段值失败，sql：{}，错误信息：{}", sql, result.getMessage());
            throw new SqlExecuteException("查询字段值失败，sql：" + sql + ",错误信息：" + result.getMessage());
        }

        List<Map<String, Object>> records = result.getRecords();
        if (records.isEmpty()) {
            log.warn("根据dataId {} 查询不到表单数据", dataId);
            return null;
        }

        return SqlExecuteClient.getFieldValueIgnoreCase(records.get(0), fieldName);
    }

    @Override
    public boolean updateFieldValue(DatasourceGroupType groupType,String tableName, String fieldName,
                                    String fieldValue, long dataId) {
        String sql = "UPDATE " + tableName + " SET " + fieldName + " = ? WHERE id = ?";
        SqlExecuteResult result = sqlExecuteClient.executeSql(groupType, sql, fieldValue, dataId);

        if (!result.isSuccess()) {
            log.error("更新字段值失败，sql：{}，错误信息：{}", sql, result.getMessage());
            throw new SqlExecuteException("更新字段值失败，sql：" + sql + ",错误信息：" + result.getMessage());
        }

        return true;
    }

    @Override
    public boolean checkFieldValueExists(DatasourceGroupType groupType,String tableName, String fieldName, String fieldValue) {
        String sql = "SELECT COUNT(*) as cnt FROM " + tableName
                + " WHERE " + fieldName + " = ?";

        SqlExecuteResult result = sqlExecuteClient.executeSql(groupType, sql, fieldValue);

        if (!result.isSuccess()) {
            log.error("检查字段值是否存在失败，sql：{}，错误信息：{}", sql, result.getMessage());
            throw new SqlExecuteException("检查字段值是否存在失败，sql：" + sql + ",错误信息：" + result.getMessage());
        }

        List<Map<String, Object>> records = result.getRecords();
        if (records.isEmpty()) {
            return false;
        }

        String cntStr = SqlExecuteClient.getFieldValueIgnoreCase(records.get(0), "cnt");
        if (CharSequenceUtil.isBlank(cntStr)) {
            return false;
        }

        try {
            int count = Integer.parseInt(cntStr);
            return count > 0;
        } catch (NumberFormatException e) {
            log.error("解析计数结果失败：{}", cntStr, e);
            return false;
        }
    }
}

