package com.weaver.seconddev.hnweaver.common.service;

import shade.jetbrains.annotations.NotNull;

/**
 * @author 姚礼林
 * @desc 文件信息业务接口
 * @date 2026/8/6
 **/
public interface FileInfoService {

    /**
     * 获取文件存储路径
     *
     * @param fileId 文件id
     * @return 文件存储路径，如果获取不到则返回空字符串
     */
    @NotNull String getFilePath(Long fileId);
}
