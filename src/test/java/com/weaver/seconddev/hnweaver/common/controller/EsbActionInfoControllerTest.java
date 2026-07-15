package com.weaver.seconddev.hnweaver.common.controller;

import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.domain.dto.EsbActionInfoDTO;
import com.weaver.seconddev.hnweaver.common.service.EsbActionInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * EsbActionInfoController 单元测试。
 *
 * @author 姚礼林
 * @date 2026/7/14
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EsbActionInfoController 信息查询测试")
class EsbActionInfoControllerTest {
    private static final String ACTION_GROUP_ID = "testAction";

    @Mock
    private EsbActionInfoService esbActionInfoService;

    @InjectMocks
    private EsbActionInfoController controller;

    @Test
    @DisplayName("应当根据 groupId 获取 Action 描述和类路径")
    void shouldGetActionInfoByGroupId() {
        EsbActionInfoDTO actionInfo = createActionInfo();
        when(esbActionInfoService.getActionInfo(ACTION_GROUP_ID)).thenReturn(Optional.of(actionInfo));

        WeaResult<EsbActionInfoDTO> result = controller.getActionInfo(ACTION_GROUP_ID);

        assertNotNull(result.getData());
        assertEquals("<p>测试 Action 描述</p>", result.getData().getDesc());
        assertEquals("com.example.TestAction", result.getData().getClassPath());
    }

    @Test
    @DisplayName("应当扫描全部 ESB Action 信息")
    void shouldGetAllActionInfos() {
        when(esbActionInfoService.getActionInfos()).thenReturn(List.of(createActionInfo()));

        WeaResult<List<EsbActionInfoDTO>> result = controller.getActionInfos();

        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals(ACTION_GROUP_ID, result.getData().get(0).getGroupId());
    }

    private EsbActionInfoDTO createActionInfo() {
        EsbActionInfoDTO actionInfo = new EsbActionInfoDTO();
        actionInfo.setGroupId(ACTION_GROUP_ID);
        actionInfo.setClassName("TestAction");
        actionInfo.setClassPath("com.example.TestAction");
        actionInfo.setDesc("<p>测试 Action 描述</p>");
        return actionInfo;
    }
}
