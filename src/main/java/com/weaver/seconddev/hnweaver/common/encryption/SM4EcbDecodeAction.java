package com.weaver.seconddev.hnweaver.common.encryption;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.AbstractEsbAction;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.Data;

/**
 * SM4 ECB 模式解密 Action<br>
 * <p>
 * 基于 SM4 算法的 ECB 模式对称解密实现，支持普通文本和 Hex 两种密钥格式，<br>
 * 密文为 Hex 编码字符串，解密后返回原始明文。<br>
 * </p>
 *
 * 输入参数：
 * <ul>
 *   <li>content - Hex 编码的密文</li>
 *   <li>key - SM4 解密密钥</li>
 *   <li>keyType - 密钥类型，TEXT（普通文本）或 HEX（十六进制），默认 TEXT</li>
 * </ul>
 *
 * @author 姚礼林
 * @date 2026/7/3
 */
@Service("SM4EcbDecodeAction")
@Log4j
public class SM4EcbDecodeAction extends AbstractEsbAction<SM4EcbDecodeAction.Param, Map<String, Object>> {

    @Override
    protected WeaResult<Map<String, Object>> doExecute(Param params) {
        String content = params.getContent();
        String key = params.getKey();

        if (key == null || key.isEmpty()) {
            return WeaResult.fail("密钥不能为空");
        }
        if (content == null || content.isEmpty()) {
            return WeaResult.fail("解密内容不能为空");
        }

        // 构建 SM4 密钥字节数组（16 字节）
        String keyType = params.getKeyType() != null ? params.getKeyType() : "TEXT";
        byte[] keyBytes;
        if ("HEX".equalsIgnoreCase(keyType)) {
            try {
                keyBytes = HexUtil.decodeHex(key);
            } catch (Exception e) {
                log.error("Hex 格式密钥解析失败", e);
                return WeaResult.fail("Hex 格式密钥解析失败：" + e.getMessage());
            }
            if (keyBytes.length != 16) {
                return WeaResult.fail("SM4 密钥长度必须为 16 字节");
            }
        } else {
            keyBytes = Arrays.copyOf(key.getBytes(StandardCharsets.UTF_8), 16);
        }

        // 解密
        SymmetricCrypto sm4 = SmUtil.sm4(keyBytes);
        String result;
        try {
            result = sm4.decryptStr(HexUtil.decodeHex(content));
        } catch (Exception e) {
            log.error("解密失败", e);
            return WeaResult.fail("解密失败：" + e.getMessage());
        }

        Map<String, Object> resultData = new HashMap<>(1);
        resultData.put("result", result);
        return WeaResult.success(resultData);
    }

    @Override
    protected Param convertToParamObj(Map<String, Object> params) {
        return convertToParamObj(params, Param.class);
    }

    /**
     * SM4 ECB 解密参数
     */
    @Data
    protected static class Param {
        /**
         * Hex 编码的密文
         */
        private String content;
        /**
         * SM4 解密密钥
         */
        private String key;
        /**
         * 密钥类型：TEXT（普通文本，默认）、HEX（十六进制）
         */
        private String keyType;
    }
}
