package com.zxy.recovery.core;

import android.app.Activity;
import android.content.Context;

import com.zxy.recovery.callback.RecoveryCallback;

/**
 * Created by zhengxiaoyong on 16/8/26.
 */
public class Recovery {

    private volatile static Recovery sInstance;

    private static final Object LOCK = new Object();

    private Recovery() {
    }

    public static Recovery getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new Recovery();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {

    }

    public Recovery debug(boolean isDebug) {
        return this;
    }

    public Recovery recoverStack(boolean isRecoverStack) {
        return this;
    }

    public Recovery recoverInBackground(boolean isRecoverInBackground) {
        return this;
    }

    public Recovery mainPage(Class<? extends Activity> clazz) {
        return this;
    }

    public Recovery callback(RecoveryCallback callback) {
        return this;
    }

    public Recovery silent(boolean enabled, SilentMode mode) {
        return this;
    }

    @SafeVarargs
    public final Recovery skip(Class<? extends Activity>... activities) {
        return this;
    }

    public Recovery recoverEnabled(boolean enabled) {
        return this;
    }

    public enum SilentMode {
        RESTART(1),
        RECOVER_ACTIVITY_STACK(2),
        RECOVER_TOP_ACTIVITY(3),
        RESTART_AND_CLEAR(4);

        int value;

        SilentMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static SilentMode getMode(int value) {
            switch (value) {
                case 1:
                    return RESTART;
                case 2:
                    return RECOVER_ACTIVITY_STACK;
                case 3:
                    return RECOVER_TOP_ACTIVITY;
                case 4:
                    return RESTART_AND_CLEAR;
                default:
                    return RECOVER_ACTIVITY_STACK;
            }
        }
    }
}
