package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.weaver.eteams.file.client.file.FileObj;
import com.weaver.eteams.file.client.remote.FileClientService;
import com.weaver.framework.rpc.annotation.RpcReference;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.exception.SqlExecuteException;
import com.weaver.seconddev.hnweaver.common.service.FileInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shade.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 姚礼林
 * @desc 文件信息业务
 * @date 2026/8/6
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class FileInfoServiceImpl implements FileInfoService {
    private final SqlExecuteClient sqlExecuteClient;
    @RpcReference
    private FileClientService fileClientService;

    @Override
    @NotNull
    public String getFilePath(Long fileId) {
        Objects.requireNonNull(fileId, "fileId 不能为空");
        FileObj fileObj = fileClientService.getById(fileId);
        if (fileObj == null) {
            throw new IllegalArgumentException("不能获取文件信息，文件id：" + fileId);
        }
        String url = fileObj.getUrl();
        String sql = "SELECT file_path FROM file_storage_info WHERE file_url = ?";
        SqlExecuteResult result = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_FILE_SERVICE, sql, url);
        if (!result.isSuccess()) {
            throw new SqlExecuteException("查询文件信息失败：" + result.getMessage());
        }
        List<Map<String, Object>> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            log.error("该文件id:{} 的 url:{} 无法获取文件存储路径，sql 查询无结果", fileId, url);
            return "";
        }
        Map<String, Object> row1 = records.get(0);
        String fileDirPath = SqlExecuteClient.getFieldValueIgnoreCase(row1, "file_path");
        if (StrUtil.isBlank(fileDirPath)) {
            log.warn("获取到的文件路径为空，文件id：{}", fileId);
            return  "";
        }
        return Paths.get(fileDirPath, fileObj.getUrl()).toString();
    }

}
