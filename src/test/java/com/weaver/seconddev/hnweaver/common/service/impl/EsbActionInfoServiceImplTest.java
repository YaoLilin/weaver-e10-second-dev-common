package com.weaver.seconddev.hnweaver.common.service.impl;

import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.esb.api.rpc.EsbServerlessRpcRemoteInterface;
import com.weaver.seconddev.hnweaver.common.AbstractEsbAction;
import com.weaver.seconddev.hnweaver.common.constants.EsbAction;
import com.weaver.seconddev.hnweaver.common.domain.dto.EsbActionInfoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * EsbActionInfoServiceImpl 单元测试。
 *
 * @author 姚礼林
 * @date 2026/7/14
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EsbActionInfoService 信息查询测试")
class EsbActionInfoServiceImplTest {
    private static final String ACTION_GROUP_ID = "testAction";

    @Mock
    private ListableBeanFactory beanFactory;

    @InjectMocks
    private EsbActionInfoServiceImpl service;

    @Test
    @DisplayName("应当根据 groupId 获取 Action 描述和类路径")
    void shouldGetActionInfoByGroupId() {
        when(beanFactory.containsBean(ACTION_GROUP_ID)).thenReturn(true);
        doReturn(TestAction.class).when(beanFactory).getType(ACTION_GROUP_ID);

        Optional<EsbActionInfoDTO> result = service.getActionInfo(ACTION_GROUP_ID);

        assertTrue(result.isPresent());
        assertEquals("<p>测试 Action 描述</p>", result.get().getDesc());
        assertEquals(TestAction.class.getName(), result.get().getClassPath());
        assertEquals(ACTION_GROUP_ID, result.get().getGroupId());
        assertTrue(Boolean.TRUE.equals(result.get().getSupportsParamParsing()));
    }

    @Test
    @DisplayName("标准 ESB Action 应当可获取信息但不支持参数解析")
    void shouldGetInfoForStandardEsbActionWithoutParamParsing() {
        when(beanFactory.containsBean("standardAction")).thenReturn(true);
        doReturn(StandardAction.class).when(beanFactory).getType("standardAction");

        Optional<EsbActionInfoDTO> result = service.getActionInfo("standardAction");

        assertTrue(result.isPresent());
        assertEquals(StandardAction.class.getName(), result.get().getClassPath());
        assertTrue(Boolean.FALSE.equals(result.get().getSupportsParamParsing()));
    }

    @Test
    @DisplayName("应当扫描全部 ESB Action 信息")
    void shouldGetAllActionInfos() {
        List<EsbActionInfoDTO> result = service.getActionInfos();

        assertTrue(result.stream().anyMatch(actionInfo -> ACTION_GROUP_ID.equals(actionInfo.getGroupId())
                && TestAction.class.getName().equals(actionInfo.getClassPath())
                && "TestAction".equals(actionInfo.getClassName())));
    }

    @EsbAction(value = ACTION_GROUP_ID, desc = "<p>测试 Action 描述</p>")
    public static class TestAction extends AbstractEsbAction<TestInput, TestOutput> {
        @Override
        protected WeaResult<TestOutput> doExecute(TestInput params) {
            return WeaResult.success(new TestOutput());
        }

        @Override
        protected TestInput convertToParamObj(Map<String, Object> params) {
            return new TestInput();
        }
    }

    public static class TestInput {
    }

    public static class TestOutput {
    }

    public static class StandardAction implements EsbServerlessRpcRemoteInterface {
        @Override
        public WeaResult<Map<String, Object>> execute(Map<String, Object> params) {
            return WeaResult.success(Map.of());
        }
    }
}
