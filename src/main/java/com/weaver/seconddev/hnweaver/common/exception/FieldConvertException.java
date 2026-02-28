package com.weaver.seconddev.hnweaver.common.exception;

/**
 * @author 姚礼林
 * @desc 表单字段转换异常
 * @date 2025/12/30
 **/
public class FieldConvertException extends RuntimeException {
    public FieldConvertException(String message) {
        super(message);
    }

  public FieldConvertException(String message, Throwable cause) {
    super(message, cause);
  }
}
