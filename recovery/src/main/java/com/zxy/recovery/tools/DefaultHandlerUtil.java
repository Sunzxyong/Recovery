package com.zxy.recovery.tools;


import android.os.Build;

import com.zxy.recovery.exception.RecoveryException;

/**
 * Created by zgxiaoyong on 16/8/28.
 */
public class DefaultHandlerUtil {

    private DefaultHandlerUtil() {
        throw new RecoveryException("Stub!");
    }

    private static Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
        try {
            Class<?> clazz;
            if (Build.VERSION.SDK_INT >= 26) {
                clazz = Class.forName("com.android.internal.os.RuntimeInit$KillApplicationHandler");
            } else {
                clazz = Class.forName("com.android.internal.os.RuntimeInit$UncaughtHandler");
            }

            Object object = clazz.getDeclaredConstructor().newInstance();
            return (Thread.UncaughtExceptionHandler) object;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isSystemDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        if (handler == null)
            return false;
        Thread.UncaughtExceptionHandler defHandler = getDefaultUncaughtExceptionHandler();
        return defHandler != null && defHandler.getClass().isInstance(handler);
    }

}
