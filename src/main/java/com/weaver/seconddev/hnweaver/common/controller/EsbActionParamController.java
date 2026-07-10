package com.weaver.seconddev.hnweaver.common.controller;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.common.authority.annotation.WeaPermission;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.AbstractEsbAction;
import com.weaver.seconddev.hnweaver.common.domain.dto.ActionParamDTO;
import com.weaver.seconddev.hnweaver.common.util.WorkflowActionParamParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 动作流 Action 参数查询接口<br>
 * <p>根据动作流配置的分组标识获取 AbstractEsbAction 子类的输入或输出参数，动作流 Action 类需要继承 AbstractEsbAction ，并且
 * 范型参数都为非基本类型和集合的对象才可以获取到 Action 参数。</p>
 * @date 2026/7/10
 **/
@RestController
@RequestMapping("/api/secondev/esb/action/params")
@WeaPermission(publicPermission = true)
@RequiredArgsConstructor
@Slf4j
public class EsbActionParamController {
    private final ListableBeanFactory beanFactory;

    /**
     * 获取 Action 输入参数
     *
     * @param groupId Action 的 Spring Bean 名称
     * @return 输入参数列表
     */
    @GetMapping("/input")
    public WeaResult<List<ActionParamDTO>> getInputParams(@RequestParam("groupId") String groupId) {
        Class<? extends AbstractEsbAction<?, ?>> actionClass = getActionClass(groupId);
        if (actionClass == null) {
            return WeaResult.fail("未找到对应的 AbstractEsbAction：" + groupId);
        }
        return WeaResult.success(WorkflowActionParamParser.parseInputParams(actionClass));
    }

    /**
     * 获取 Action 输出参数
     *
     * @param groupId Action 的 Spring Bean 名称
     * @return 输出参数列表
     */
    @GetMapping("/output")
    public WeaResult<List<ActionParamDTO>> getOutputParams(@RequestParam("groupId") String groupId) {
        Class<? extends AbstractEsbAction<?, ?>> actionClass = getActionClass(groupId);
        if (actionClass == null) {
            return WeaResult.fail("未找到对应的 AbstractEsbAction：" + groupId);
        }
        return WeaResult.success(WorkflowActionParamParser.parseOutputParams(actionClass));
    }

    @SuppressWarnings("unchecked")
    private Class<? extends AbstractEsbAction<?, ?>> getActionClass(String groupId) {
        if (CharSequenceUtil.isBlank(groupId) || !beanFactory.containsBean(groupId)) {
            log.warn("未找到 groupId 为 {} 的 Action Bean", groupId);
            return null;
        }

        Class<?> beanType = beanFactory.getType(groupId);
        if (beanType == null || !AbstractEsbAction.class.isAssignableFrom(beanType)) {
            log.warn("groupId {} 对应的 Bean 不是 AbstractEsbAction 子类", groupId);
            return null;
        }
        return (Class<? extends AbstractEsbAction<?, ?>>) beanType;
    }
}
