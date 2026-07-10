package com.weaver.seconddev.hnweaver.common.encryption;

import com.weaver.common.base.entity.result.WeaResult;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SM4EcbDecodeAction 单元测试<br>
 * <p>
 * 测试 SM4 ECB 模式解密的参数校验、密钥格式支持以及与加密动作的加解密闭环。
 * </p>
 *
 * @author 姚礼林
 * @date 2026/7/3
 */
class SM4EcbDecodeActionTest {

    private final SM4EcbDecodeAction decodeAction = new SM4EcbDecodeAction();
    private final SM4EcbEncodeAction encodeAction = new SM4EcbEncodeAction();

    @Test
    void doExecute_withEmptyKey_shouldFail() {
        SM4EcbDecodeAction.Param param = new SM4EcbDecodeAction.Param();
        param.setContent("abcdef");
        param.setKey("");

        WeaResult<Map<String, Object>> result = decodeAction.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withNullKey_shouldFail() {
        SM4EcbDecodeAction.Param param = new SM4EcbDecodeAction.Param();
        param.setContent("abcdef");

        WeaResult<Map<String, Object>> result = decodeAction.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withEmptyContent_shouldFail() {
        SM4EcbDecodeAction.Param param = new SM4EcbDecodeAction.Param();
        param.setContent("");
        param.setKey("0123456789abcdef");

        WeaResult<Map<String, Object>> result = decodeAction.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withNullContent_shouldFail() {
        SM4EcbDecodeAction.Param param = new SM4EcbDecodeAction.Param();
        param.setKey("0123456789abcdef");

        WeaResult<Map<String, Object>> result = decodeAction.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withInvalidHexContent_shouldFail() {
        SM4EcbDecodeAction.Param param = new SM4EcbDecodeAction.Param();
        param.setContent("这不是hex");
        param.setKey("0123456789abcdef");

        WeaResult<Map<String, Object>> result = decodeAction.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withInvalidHexKey_shouldFail() {
        SM4EcbDecodeAction.Param param = new SM4EcbDecodeAction.Param();
        param.setContent("abcdef1234567890");
        param.setKey("invalid-hex-key");
        param.setKeyType("HEX");

        WeaResult<Map<String, Object>> result = decodeAction.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withTextKey_roundTrip() {
        String plaintext = "Hello SM4 Decrypt!";
        String key = "0123456789abcdef";

        // 先加密
        SM4EcbEncodeAction.Param encParam = new SM4EcbEncodeAction.Param();
        encParam.setContent(plaintext);
        encParam.setKey(key);
        WeaResult<SM4EcbEncodeAction.Result> encResult = encodeAction.doExecute(encParam);
        assertFalse(encResult.isFail());
        String ciphertext = encResult.getData().getResult();

        // 再解密
        SM4EcbDecodeAction.Param decParam = new SM4EcbDecodeAction.Param();
        decParam.setContent(ciphertext);
        decParam.setKey(key);
        WeaResult<Map<String, Object>> decResult = decodeAction.doExecute(decParam);
        assertFalse(decResult.isFail());
        String decrypted = (String) decResult.getData().get("result");

        assertEquals(plaintext, decrypted);
    }

    @Test
    void doExecute_withHexKey_roundTrip() {
        String plaintext = "SM4 ECB Hex Key Test";
        String hexKey = "0123456789abcdef0123456789abcdef";

        // 先加密（HEX 密钥模式）
        SM4EcbEncodeAction.Param encParam = new SM4EcbEncodeAction.Param();
        encParam.setContent(plaintext);
        encParam.setKey(hexKey);
        encParam.setKeyType("HEX");
        WeaResult<SM4EcbEncodeAction.Result> encResult = encodeAction.doExecute(encParam);
        assertFalse(encResult.isFail());
        String ciphertext = encResult.getData().getResult();

        // 再解密（HEX 密钥模式）
        SM4EcbDecodeAction.Param decParam = new SM4EcbDecodeAction.Param();
        decParam.setContent(ciphertext);
        decParam.setKey(hexKey);
        decParam.setKeyType("HEX");
        WeaResult<Map<String, Object>> decResult = decodeAction.doExecute(decParam);
        assertFalse(decResult.isFail());
        String decrypted = (String) decResult.getData().get("result");

        assertEquals(plaintext, decrypted);
    }

    @Test
    void doExecute_withE10Example() {
        // 模拟与 E10 SM4ECBDecode 函数的兼容性：TEXT 密钥 + 加密后用 HEX 传参
        String plaintext = "{\"appId\":\"lgyy_zk\"}";
        String key = "CpRBzb6ojoTHQ2Bz";

        // 加密
        SM4EcbEncodeAction.Param encParam = new SM4EcbEncodeAction.Param();
        encParam.setContent(plaintext);
        encParam.setKey(key);
        WeaResult<SM4EcbEncodeAction.Result> encResult = encodeAction.doExecute(encParam);
        assertFalse(encResult.isFail());
        String ciphertext = encResult.getData().getResult();

        // 解密
        SM4EcbDecodeAction.Param decParam = new SM4EcbDecodeAction.Param();
        decParam.setContent(ciphertext);
        decParam.setKey(key);
        WeaResult<Map<String, Object>> decResult = decodeAction.doExecute(decParam);
        assertFalse(decResult.isFail());
        String decrypted = (String) decResult.getData().get("result");

        assertEquals(plaintext, decrypted);
    }
}
