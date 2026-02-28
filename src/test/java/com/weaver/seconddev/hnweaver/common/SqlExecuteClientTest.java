package com.weaver.seconddev.hnweaver.common;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 姚礼林
 * @desc SqlExecuteClient 测试类
 * @date 2025/12/15
 **/
class SqlExecuteClientTest {

    @Test
    void getFieldValueIgnoreCase() {
        String json = "{\"target_id\":\"1193943523387531266\",\"id\":\"1193943523387531269\",\"table_name\":\"uf_wisedu_schedule_record\"}";
        Map<String ,Object> record = JSONObject.toJavaObject(JSONObject.parseObject(json), Map.class);
        String id = SqlExecuteClient.getFieldValueIgnoreCase(record, "ID");
        assertNotNull(id);
        assertEquals("1193943523387531269", id);
        String targetId = SqlExecuteClient.getFieldValueIgnoreCase(record, "TARGET_ID");
        assertNotNull(targetId);
        assertEquals("1193943523387531266", targetId);
    }

    @Test
    void getFieldValueIgnoreCase_withLowercase() {
        String json = "{\"target_id\":\"1193943523387531266\",\"id\":\"1193943523387531269\",\"table_name\":\"uf_wisedu_schedule_record\"}";
        Map<String ,Object> record = JSONObject.toJavaObject(JSONObject.parseObject(json), Map.class);
        String id = SqlExecuteClient.getFieldValueIgnoreCase(record, "id");
        assertNotNull(id);
        assertEquals("1193943523387531269", id);
        String targetId = SqlExecuteClient.getFieldValueIgnoreCase(record, "target_id");
        assertNotNull(targetId);
        assertEquals("1193943523387531266", targetId);
    }
}
