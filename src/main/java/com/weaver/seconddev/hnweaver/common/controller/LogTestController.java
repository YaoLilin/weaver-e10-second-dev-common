package com.weaver.seconddev.hnweaver.common.controller;

import com.weaver.common.authority.annotation.WeaPermission;
import com.weaver.common.base.entity.result.WeaResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 姚礼林
 * @desc 日志测试
 * @date 2025/12/30
 **/
@RestController
@RequestMapping({"/api/secondev/test/log"})
@WeaPermission(publicPermission = true)
@RequiredArgsConstructor
@Slf4j
public class LogTestController {

    @GetMapping
    public WeaResult<String> testLog() {
        log.debug("debug 日志");
        log.info("info 日志");
        log.error("error 日志");
        try {
            throw new Exception("异常");
        } catch (Exception e) {
            log.error("出现异常", e);
        }
        log.error("error 日志2");
        return WeaResult.success("成功");
    }
}
