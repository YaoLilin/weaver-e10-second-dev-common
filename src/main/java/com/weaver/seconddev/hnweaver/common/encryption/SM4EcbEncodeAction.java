package com.weaver.seconddev.hnweaver.common.encryption;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.common.AbstractEsbAction;
import com.weaver.seconddev.hnweaver.common.constants.ActionParam;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * SM4 ECB 模式加密 Action<br>
 * <p>
 * 基于 SM4 算法的 ECB 模式对称加密实现，支持普通文本和 Hex 两种密钥格式，<br>
 * 加密结果支持 Hex 和 Base64 两种输出格式。<br>
 * </p>
 * <p>
 * 输入参数：
 * <ul>
 *   <li>content - 待加密内容</li>
 *   <li>key - SM4 加密密钥</li>
 *   <li>keyType - 密钥类型，TEXT（普通文本）或 HEX（十六进制），默认 TEXT</li>
 *   <li>outputFormat - 输出结果类型，HEX 或 BASE64，默认 HEX</li>
 * </ul>
 *
 * @author 姚礼林
 * @date 2026/7/1
 */
@Service("SM4EcbEncodeAction")
@Log4j
public class SM4EcbEncodeAction extends AbstractEsbAction<SM4EcbEncodeAction.Param, SM4EcbEncodeAction.Result> {

    @Override
    protected WeaResult<Result> doExecute(Param params) {
        String content = params.getContent();
        String key = params.getKey();

        if (key == null || key.isEmpty()) {
            return WeaResult.fail("密钥不能为空");
        }
        if (content == null || content.isEmpty()) {
            return WeaResult.fail("加密内容不能为空");
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

        // 加密并输出结果
        SymmetricCrypto sm4 = SmUtil.sm4(keyBytes);
        String outputFormat = params.getOutputFormat() != null ? params.getOutputFormat() : "HEX";
        String result = "BASE64".equalsIgnoreCase(outputFormat) ? sm4.encryptBase64(content) : sm4.encryptHex(content);

        Result resultData = new Result();
        resultData.setResult(result);
        return WeaResult.success(resultData);
    }

    @Override
    protected Param convertToParamObj(Map<String, Object> params) {
        return convertToParamObj(params, Param.class);
    }

    /**
     * SM4 ECB 加密参数
     */
    @Data
    protected static class Param {
        /**
         * 待加密内容
         */
        @ActionParam(required = true, desc = "待加密内容", displayName = "待加密内容")
        private String content;
        /**
         * SM4 加密密钥
         */
        @ActionParam(required = true, desc = "SM4 加密密钥", displayName = "SM4 加密密钥")
        private String key;
        /**
         * 密钥类型：TEXT（普通文本，默认）、HEX（十六进制）
         */
        @ActionParam(required = false, desc = "密钥类型：TEXT（普通文本，默认）、HEX（十六进制）", displayName = "密钥类型")
        private String keyType;
        /**
         * 输出结果类型：HEX（十六进制，默认）、BASE64
         */
        @ActionParam(required = false, desc = "输出结果类型：HEX（十六进制，默认）、BASE64", displayName = "输出结果类型")
        private String outputFormat;
    }

    @Data
    protected static class Result {
        /**
         * 加密结果
         */
        @ActionParam(required = true, desc = "加密结果", displayName = "加密结果")
        private String result;
    }
}
