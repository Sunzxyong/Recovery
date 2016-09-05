package com.zxy.recovery.exception;

/**
 * Created by zhengxiaoyong on 16/8/28.
 */
public class RecoveryException extends RuntimeException {
    public RecoveryException(String message) {
        super(message);
    }

    public RecoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
