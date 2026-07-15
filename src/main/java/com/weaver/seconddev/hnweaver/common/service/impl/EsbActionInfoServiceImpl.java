package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.esb.api.rpc.EsbServerlessRpcRemoteInterface;
import com.weaver.seconddev.hnweaver.common.AbstractEsbAction;
import com.weaver.seconddev.hnweaver.common.constants.EsbAction;
import com.weaver.seconddev.hnweaver.common.domain.dto.EsbActionInfoDTO;
import com.weaver.seconddev.hnweaver.common.service.EsbActionInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.beans.Introspector;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ESB Action 信息服务实现。
 *
 * @author 姚礼林
 * @date 2026/7/14
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EsbActionInfoServiceImpl implements EsbActionInfoService {
    private static final String ACTION_BASE_PACKAGE = "com.weaver.seconddev";

    private final ListableBeanFactory beanFactory;

    @Override
    public List<EsbActionInfoDTO> getActionInfos() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(EsbServerlessRpcRemoteInterface.class));

        return scanner.findCandidateComponents(ACTION_BASE_PACKAGE).stream()
                .map(BeanDefinition::getBeanClassName)
                .filter(Objects::nonNull)
                .map(this::loadActionClass)
                .filter(Objects::nonNull)
                .filter(this::isConcreteActionClass)
                .map(actionClass -> buildActionInfo(actionClass, resolveGroupId(actionClass)))
                .sorted(Comparator.comparing(EsbActionInfoDTO::getClassName))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EsbActionInfoDTO> getActionInfo(String groupId) {
        if (CharSequenceUtil.isBlank(groupId) || !beanFactory.containsBean(groupId)) {
            log.warn("未找到 groupId 为 {} 的 Action Bean", groupId);
            return Optional.empty();
        }

        Class<?> beanType = beanFactory.getType(groupId);
        if (beanType == null || !EsbServerlessRpcRemoteInterface.class.isAssignableFrom(beanType)) {
            log.warn("groupId {} 对应的 Bean 不是 EsbServerlessRpcRemoteInterface 实现类", groupId);
            return Optional.empty();
        }

        Class<?> userClass = ClassUtils.getUserClass(beanType);
        return Optional.of(buildActionInfo(userClass, groupId));
    }

    private Class<?> loadActionClass(String className) {
        try {
            return ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
        } catch (ClassNotFoundException e) {
            log.warn("加载 ESB Action 类 {} 失败", className, e);
            return null;
        }
    }

    private boolean isConcreteActionClass(Class<?> actionClass) {
        int modifiers = actionClass.getModifiers();
        return !actionClass.isInterface() && !Modifier.isAbstract(modifiers)
                && EsbServerlessRpcRemoteInterface.class.isAssignableFrom(actionClass);
    }

    private EsbActionInfoDTO buildActionInfo(Class<?> actionClass, String groupId) {
        EsbAction annotation = AnnotationUtils.findAnnotation(actionClass, EsbAction.class);
        EsbActionInfoDTO actionInfo = new EsbActionInfoDTO();
        actionInfo.setDesc(annotation == null ? "" : annotation.desc());
        actionInfo.setClassPath(actionClass.getName());
        actionInfo.setClassName(actionClass.getSimpleName());
        actionInfo.setGroupId(groupId);
        actionInfo.setSupportsParamParsing(AbstractEsbAction.class.isAssignableFrom(actionClass));
        return actionInfo;
    }

    private String resolveGroupId(Class<?> actionClass) {
        EsbAction esbAction = AnnotationUtils.findAnnotation(actionClass, EsbAction.class);
        if (esbAction != null && CharSequenceUtil.isNotBlank(esbAction.value())) {
            return esbAction.value();
        }

        Service service = AnnotationUtils.findAnnotation(actionClass, Service.class);
        if (service != null && CharSequenceUtil.isNotBlank(service.value())) {
            return service.value();
        }

        Component component = AnnotationUtils.findAnnotation(actionClass, Component.class);
        if (component != null && CharSequenceUtil.isNotBlank(component.value())) {
            return component.value();
        }
        return Introspector.decapitalize(actionClass.getSimpleName());
    }
}
