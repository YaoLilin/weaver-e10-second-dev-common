package com.weaver.seconddev.hnweaver.common.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.Nullable;

/**
 * @author 姚礼林
 * @desc 参数类型枚举
 * @date 2026/1/10
 **/
public enum ParamType {
    /**
     * 字符串
     */
    STRING(0, "字符串"),

    /**
     * 数字
     */
    NUMBER(1, "数字"),

    /**
     * 布尔
     */
    BOOLEAN(2, "布尔"),

    /**
     * 对象
     */
    OBJECT(3, "对象"),

    /**
     * 数组
     */
    ARRAY(4, "数组");

    private final int value;
    private final String displayName;

    ParamType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据值获取枚举
     */
    @JsonCreator
    @Nullable
    public static ParamType fromValue(int value) {
        for (ParamType type : ParamType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
