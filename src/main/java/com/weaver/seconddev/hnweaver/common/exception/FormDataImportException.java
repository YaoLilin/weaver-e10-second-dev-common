package com.weaver.seconddev.hnweaver.common.exception;

/**
 * @author 姚礼林
 * @desc 表单数据导入异常
 * @date 2025/8/1
 **/
public class FormDataImportException extends RuntimeException {
    public FormDataImportException(String message) {
        super(message);
    }

    public FormDataImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
