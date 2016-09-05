package com.zxy.recovery.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.zxy.recovery.callback.RecoveryActivityLifecycleCallback;
import com.zxy.recovery.callback.RecoveryCallback;
import com.zxy.recovery.exception.RecoveryException;
import com.zxy.recovery.tools.RecoveryLog;
import com.zxy.recovery.tools.RecoveryUtil;

/**
 * Created by zhengxiaoyong on 16/8/26.
 */
public class Recovery {

    private volatile static Recovery sInstance;

    private static final Object LOCK = new Object();

    private Context mContext;

    private boolean isDebug = false;

    /**
     * The default to restore the stack.
     */
    private boolean isRecoverStack = true;

    /**
     * The default is not restore the background process.
     */
    private boolean isRecoverInBackground = false;

    private Class<? extends Activity> mMainPageClass;

    private RecoveryCallback mCallback;

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
        if (context == null)
            throw new RecoveryException("Context can not be null!");
        if (!(context instanceof Application))
            context = context.getApplicationContext();
        mContext = context;
        registerRecoveryHandler();
        registerRecoveryLifecycleCallback();
    }

    public Recovery debug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    public Recovery recoverStack(boolean isRecoverStack) {
        this.isRecoverStack = isRecoverStack;
        return this;
    }

    public Recovery recoverInBackground(boolean isRecoverInBackground) {
        this.isRecoverInBackground = isRecoverInBackground;
        return this;
    }

    public Recovery mainPage(Class<? extends Activity> clazz) {
        this.mMainPageClass = clazz;
        return this;
    }

    public Recovery callback(RecoveryCallback callback) {
        this.mCallback = callback;
        return this;
    }

    private void registerRecoveryHandler() {
        RecoveryHandler.newInstance(Thread.getDefaultUncaughtExceptionHandler()).setCallback(mCallback).register();
    }

    private void registerRecoveryLifecycleCallback() {
        ((Application) mContext).registerActivityLifecycleCallbacks(new RecoveryActivityLifecycleCallback());
    }

    private void registerRecoveryProxy() {
        //OS version in the 5.0 ~ 6.0 don`t register agent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        if (mMainPageClass == null)
            return;
        if (!RecoveryUtil.isMainProcess(RecoveryUtil.checkNotNull(mContext, "The context is not initialized")))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    boolean isSuccess = RecoveryComponentHook.hookActivityManagerProxy();
                    RecoveryLog.e("hook is success:" + isSuccess);
                    if (isSuccess)
                        break;
                }
            }
        }).start();
    }

    public Context getContext() {
        return RecoveryUtil.checkNotNull(mContext, "The context is not initialized");
    }

    public boolean isDebug() {
        return isDebug;
    }

    boolean isRecoverInBackground() {
        return isRecoverInBackground;
    }

    boolean isRecoverStack() {
        return isRecoverStack;
    }

    Class<? extends Activity> getMainPageClass() {
        return mMainPageClass;
    }
}
