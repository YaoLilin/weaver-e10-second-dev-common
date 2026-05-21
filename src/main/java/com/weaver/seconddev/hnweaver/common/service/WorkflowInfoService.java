package com.weaver.seconddev.hnweaver.common.service;

import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 工作流信息服务接口
 * @date 2025/12/9
 **/
public interface WorkflowInfoService {

    /**
     * 根据流程请求ID查询表单数据ID
     *
     * @param requestId 流程请求ID
     * @return 表单数据ID，如果查询不到则返回null
     */
    Long getDataIdByRequestId(long requestId);

    /**
     * 根据流程请求ID查询表单ID
     *
     * @param requestId 流程请求ID
     * @return 表单ID，如果查询不到则返回Optional.empty()
     */
    Optional<Long> getFormIdByRequestId(long requestId);

    /**
     * 根据流程请求ID查询流程定义ID
     *
     * @param requestId 流程请求ID
     * @return 流程定义ID，如果查询不到则返回Optional.empty()
     */
    Optional<String> getFormTableNameByRequestId(long requestId);
}

