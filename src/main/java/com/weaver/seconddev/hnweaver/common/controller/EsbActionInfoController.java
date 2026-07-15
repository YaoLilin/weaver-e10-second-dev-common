package com.weaver.seconddev.hnweaver.common.controller;

import com.weaver.common.authority.annotation.WeaPermission;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.domain.dto.EsbActionInfoDTO;
import com.weaver.seconddev.hnweaver.common.service.EsbActionInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ESB Action 信息查询接口。
 *
 * @author 姚礼林
 * @date 2026/7/14
 */
@RestController
@RequestMapping("/api/secondev/esb/actions")
@WeaPermission(publicPermission = true)
@RequiredArgsConstructor
public class EsbActionInfoController {
    private final EsbActionInfoService esbActionInfoService;

    /**
     * 获取全部 ESB Action 信息。
     *
     * @return Action 信息列表
     */
    @GetMapping
    public WeaResult<List<EsbActionInfoDTO>> getActionInfos() {
        return WeaResult.success(esbActionInfoService.getActionInfos());
    }

    /**
     * 根据 groupId 获取 Action 描述和类路径。
     *
     * @param groupId Action 的 Spring Bean 名称
     * @return Action 描述和类全限定名
     */
    @GetMapping("/{groupId}")
    public WeaResult<EsbActionInfoDTO> getActionInfo(@PathVariable String groupId) {
        return esbActionInfoService.getActionInfo(groupId)
                .map(WeaResult::success)
                .orElseGet(() -> WeaResult.fail("未找到对应的 ESB Action：" + groupId));
    }
}
