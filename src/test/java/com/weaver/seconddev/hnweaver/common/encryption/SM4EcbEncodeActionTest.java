package com.weaver.seconddev.hnweaver.common.encryption;

import com.weaver.common.base.entity.result.WeaResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SM4EcbEncodeAction 单元测试<br>
 * <p>
 * 测试 SM4 ECB 模式加密的各项功能：参数校验、普通文本/Hex 密钥格式、Hex/Base64 输出格式。
 * </p>
 *
 * @author 姚礼林
 * @date 2026/7/1
 */
class SM4EcbEncodeActionTest {

    private final SM4EcbEncodeAction action = new SM4EcbEncodeAction();

    @Test
    void doExecute_withEmptyKey_shouldFail() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("test");
        param.setKey("");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withNullKey_shouldFail() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("test");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withEmptyContent_shouldFail() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("");
        param.setKey("0123456789abcdef");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withNullContent_shouldFail() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setKey("0123456789abcdef");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withTextKey_defaultHexOutput() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("Hello SM4");
        param.setKey("0123456789abcdef");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertFalse(result.isFail());
        String resultStr = result.getData().getResult();
        assertNotNull(resultStr);
        assertFalse(resultStr.isEmpty());
        // HEX 输出应仅包含十六进制字符
        assertTrue(resultStr.matches("[0-9a-fA-F]+"));
    }

    @Test
    void doExecute_withTextKey_base64Output() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("Hello SM4");
        param.setKey("0123456789abcdef");
        param.setOutputFormat("BASE64");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertFalse(result.isFail());
        String resultStr = result.getData().getResult();
        assertNotNull(resultStr);
        assertFalse(resultStr.isEmpty());
    }

    @Test
    void doExecute_withHexKey_defaultHexOutput() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("Hello SM4");
        param.setKey("0123456789abcdef0123456789abcdef");
        param.setKeyType("HEX");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertFalse(result.isFail());
        String resultStr = result.getData().getResult();
        assertNotNull(resultStr);
        assertFalse(resultStr.isEmpty());
        // HEX 输出应仅包含十六进制字符
        assertTrue(resultStr.matches("[0-9a-fA-F]+"));
    }

    @Test
    void doExecute_withHexKey_base64Output() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("Hello SM4");
        param.setKey("0123456789abcdef0123456789abcdef");
        param.setKeyType("HEX");
        param.setOutputFormat("BASE64");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertFalse(result.isFail());
        String resultStr = result.getData().getResult();
        assertNotNull(resultStr);
        assertFalse(resultStr.isEmpty());
    }

    @Test
    void doExecute_withInvalidHexKey_shouldFail() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("Hello SM4");
        param.setKey("invalid-hex-key");
        param.setKeyType("HEX");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withHexKeyWrongLength_shouldFail() {
        // HEX 密钥解码后长度不足 16 字节应失败
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("test");
        param.setKey("01234567");
        param.setKeyType("HEX");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertTrue(result.isFail());
    }

    @Test
    void doExecute_withSameInput_shouldProduceConsistentResult() {
        SM4EcbEncodeAction.Param param1 = new SM4EcbEncodeAction.Param();
        param1.setContent("Hello SM4");
        param1.setKey("0123456789abcdef");

        SM4EcbEncodeAction.Param param2 = new SM4EcbEncodeAction.Param();
        param2.setContent("Hello SM4");
        param2.setKey("0123456789abcdef");

        WeaResult<SM4EcbEncodeAction.Result> result1 = action.doExecute(param1);
        WeaResult<SM4EcbEncodeAction.Result> result2 = action.doExecute(param2);
        assertEquals(result1.getData().getResult(), result2.getData().getResult());
    }

    @Test
    void doExecute_withShortTextKey_shouldPadTo16Bytes() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("test");
        param.setKey("short");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertFalse(result.isFail());
        assertNotNull(result.getData().getResult());
    }

    @Test
    void doExecute_withLongTextKey_shouldTruncateTo16Bytes() {
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent("test");
        param.setKey("this is a very long key that exceeds sixteen bytes");

        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertFalse(result.isFail());
        assertNotNull(result.getData().getResult());
    }

    @DisplayName("测试加密")
    @Test
    void doExecute_encrypt(){
        // 模拟服务器动作流调用流程：参数经 convertToParamObj 处理后加密
        String contentJson = "{\"appId\": \"lgyy_zk\",\"secret\": \"Kpuq6X*kaLn&yH!zk\",\"mobiles\": \"17736604794\",\"content\": \"c\",\"sign\": \"57bd218f6237a187a6a4d9d08e27e2c0e6d60ddecbcdaa9ccb072950cff547e5\",\"t\": \"1782985811855\"}";
        SM4EcbEncodeAction.Param param = new SM4EcbEncodeAction.Param();
        param.setContent(contentJson);
        param.setKey("14183bfd64f98692a4719111cbe88dad");
        param.setKeyType("HEX");
        param.setOutputFormat("HEX");
        WeaResult<SM4EcbEncodeAction.Result> result = action.doExecute(param);
        assertFalse(result.isFail());
        String resultStr = result.getData().getResult();
        assertNotNull(resultStr);
        System.out.println("加密结果：" + resultStr);
    }
}
