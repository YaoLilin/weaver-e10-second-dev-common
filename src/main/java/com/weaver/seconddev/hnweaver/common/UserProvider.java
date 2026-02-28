package com.weaver.seconddev.hnweaver.common;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.seconddev.hnweaver.common.config.CommonConfigProperties;
import com.weaver.teams.domain.user.SimpleEmployee;
import com.weaver.teams.security.context.UserContext;
import com.weaver.workflow.common.cfg.org.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 可获取当前用户或系统管理员用户。有时候业务需要用到用户，使用 {@code UserContext.getCurrentUser()}
 * 获取当前用户可能取不到（比如远程调用 Action 时），则需要获取一个用户，可以使用此类进行获取，它可以判断当前用户是否为空，
 * 如果为空则获取系统管理员用户，为了能够获取系统管理员用户，需要在配置文件配置系统管理员用户id。
 * @date 2025/10/25
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class UserProvider {
    private final CommonConfigProperties properties;
    private final UserService userService;

    /**
     * 获取当前用户，如果当前用户为空，则使用系统管理员用户，需在配置文件中配置系统管理员用户id，
     * 配置见 {@link CommonConfigProperties#getSysadminUserId()}
     * @return 当前用户或系统管理员用户，如果获取不到则返回空
     */
    public Optional<SimpleEmployee> getCurrentUserOrSysadminUser() {
        SimpleEmployee currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            log.info("当前用户为空，使用系统管理员用户");
            return getSysadminUser();
        }
        return Optional.of(currentUser);
    }

    /**
     * 获取系统管理员用户，需在配置文件中配置系统管理员用户id，配置见 {@link CommonConfigProperties#getSysadminUserId()}
     * @return 系统管理员用户，如果获取不到则返回空
     */
    public Optional<SimpleEmployee> getSysadminUser() {
        String userId = properties.getSysadminUserId();
        if (CharSequenceUtil.isBlank(userId)) {
            log.warn("系统管理员用户id未配置");
            return Optional.empty();
        }
        SimpleEmployee user = userService.getSimpleEmployeeById(Long.parseLong(userId));
        if (user == null) {
            log.error("系统管理员用户不存在，用户id：{}", userId);
            return Optional.empty();
        }
        return Optional.of(user);
    }
}
