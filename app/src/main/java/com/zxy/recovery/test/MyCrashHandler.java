package com.zxy.recovery.test;

import android.util.Log;

/**
 * Created by zhengxiaoyong on 2017/1/16.
 */
public class MyCrashHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    public MyCrashHandler() {
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e("zxy", "myCrashHandler...");
        mUncaughtExceptionHandler.uncaughtException(t, e);
    }


    public static void register() {
        Thread.setDefaultUncaughtExceptionHandler(new MyCrashHandler());
    }
}
