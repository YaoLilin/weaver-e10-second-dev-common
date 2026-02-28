package com.weaver.seconddev.hnweaver.common.constants;

/**
 * @author 姚礼林
 * @desc eb表单明细更新模式
 * @date 2025/9/16
 **/
public enum DetailUpdateType {
    /**
     * 追加
     */
    INSERT,
    /**
     * 更新，以数据id为条件，为此模式时必需添加数据id
     */
    UPDATE,
    /**
     * 插入或更新，不符合条件时则插入，否则更新
     */
    INSERT_OR_UPDATE,
    /**
     * 覆盖
     */
    COVER,
    /**
     * 删除
     */
    DELETE,
    /**
     * 删除所有
     */
    DELETE_ALL
}
