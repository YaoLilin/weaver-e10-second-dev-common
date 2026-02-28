package com.weaver.seconddev.hnweaver.common.exception;

/**
 * @author 姚礼林
 * @desc sql执行异常
 * @date 2025/8/16
 **/
public class SqlExecuteException extends RuntimeException {
    public SqlExecuteException(String message) {
        super(message);
    }
}
