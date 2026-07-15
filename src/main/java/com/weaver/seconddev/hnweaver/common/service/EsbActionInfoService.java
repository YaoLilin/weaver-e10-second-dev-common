package com.weaver.seconddev.hnweaver.common.service;

import com.weaver.seconddev.hnweaver.common.domain.dto.EsbActionInfoDTO;

import java.util.List;
import java.util.Optional;

/**
 * ESB Action 信息服务。
 *
 * @author 姚礼林
 * @date 2026/7/14
 */
public interface EsbActionInfoService {
    /**
     * 获取全部 ESB Action 信息。
     *
     * @return Action 信息列表
     */
    List<EsbActionInfoDTO> getActionInfos();

    /**
     * 根据 groupId 获取 Action 信息。
     *
     * @param groupId Action 的 Spring Bean 名称
     * @return Action 信息；未找到时返回 Optional.empty()
     */
    Optional<EsbActionInfoDTO> getActionInfo(String groupId);
}
