package com.weaver.seconddev.hnweaver.common.encryption;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.AbstractEsbAction;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.HMac;
import lombok.Data;

/**
 * HMAC-SM3 加密 Action<br>
 * <p>
 * 基于 SM3 哈希算法的 HMAC 消息认证码实现，用于带密钥的消息完整性校验和来源认证。<br>
 * 输入参数：key（密钥）、content（待加密内容）<br>
 * 输出：64 位十六进制 HMAC-SM3 摘要字符串
 * </p>
 *
 * @author 姚礼林
 * @date 2025/11/25
 */
@Component
public class HmacSM3Action extends AbstractEsbAction<HmacSM3Action.EncryptParam, String> {

    @Override
    protected WeaResult<String> doExecute(EncryptParam params) {
        String key = params.getKey();
        String content = params.getContent();
        if (key == null || key.isEmpty()) {
            return WeaResult.fail("密钥不能为空");
        }
        if (content == null || content.isEmpty()) {
            return WeaResult.fail("加密内容不能为空");
        }
        HMac hmac = SmUtil.hmacSm3(key.getBytes(StandardCharsets.UTF_8));
        String result = hmac.digestHex(content, StandardCharsets.UTF_8);
        return WeaResult.success(result);
    }

    @Override
    protected EncryptParam convertToParamObj(Map<String, Object> params) {
        return convertToParamObj(params, EncryptParam.class);
    }

    /**
     * HMAC-SM3 加密参数
     */
    @Data
    static class EncryptParam {
        /**
         * 密钥
         */
        private String key;
        /**
         * 待加密内容
         */
        private String content;
    }
}
