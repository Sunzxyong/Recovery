package com.zxy.recovery.callback;

/**
 * Created by zhengxiaoyong on 16/8/29.
 */
public interface RecoveryCallback {

    void stackTrace(String stackTrace);

    void cause(String cause);

    void exception(String throwExceptionType, String throwClassName, String throwMethodName, int throwLineNumber);

    void throwable(Throwable throwable);
}
