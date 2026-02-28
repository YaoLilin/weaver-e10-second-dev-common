package com.weaver.seconddev.hnweaver.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.weaver.seconddev.hnweaver.common.constants.FieldComponentKeys;
import com.weaver.seconddev.hnweaver.common.service.FieldConvertor;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 文件字段转换抽象类
 * @date 2025/9/18
 **/
public abstract class AbstractFileFieldConvertor implements FieldConvertor {

    @Override
    public List<String> getComponentKey() {
        return CollUtil.toList(FieldComponentKeys.FILE);
    }
}
