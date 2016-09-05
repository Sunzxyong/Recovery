package com.zxy.recovery.exception;

/**
 * Created by zhengxiaoyong on 16/8/28.
 */
public class ReflectException extends RuntimeException {
    public ReflectException() {
        super();
    }

    public ReflectException(String detailMessage) {
        super(detailMessage);
    }

    public ReflectException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ReflectException(Throwable throwable) {
        super(throwable);
    }
}
