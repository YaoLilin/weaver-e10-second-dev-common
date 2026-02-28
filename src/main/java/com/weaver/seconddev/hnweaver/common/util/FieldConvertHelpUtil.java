package com.weaver.seconddev.hnweaver.common.util;

import com.weaver.common.form.dto.data.FormDataOptionDto;
import com.weaver.common.form.metadata.ModuleSource;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * @author 姚礼林
 * @desc 字段转换辅助工具
 * @date 2025/9/18
 **/
@UtilityClass
public class FieldConvertHelpUtil {

    public static @NotNull List<FormDataOptionDto> handleBrowserField(ModuleSource type, String value,
                                                                UnaryOperator<String> valueConvertFunc) {
        List<FormDataOptionDto> options = new ArrayList<>();
        for (String name : value.split(",")) {
            if (!name.isEmpty()) {
                FormDataOptionDto option = new FormDataOptionDto();
                option.setType(type);
                option.setOptionId(valueConvertFunc.apply(value));
                options.add(option);
            }
        }
        return options;
    }

}
