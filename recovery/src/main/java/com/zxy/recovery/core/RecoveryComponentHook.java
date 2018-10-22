package com.zxy.recovery.core;

import android.os.Build;

import com.zxy.recovery.tools.RecoveryLog;
import com.zxy.recovery.tools.Reflect;

import java.lang.reflect.Proxy;

/**
 * Created by zhengxiaoyong on 16/8/30.
 */
final class RecoveryComponentHook {

    static boolean hookActivityManagerProxy() {
        Object gDefault;
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                //IActivityManagerSingleton
                gDefault = Reflect.on(android.app.ActivityManager.class).field("IActivityManagerSingleton").get(null);
            } else {
                //Singleton<IActivityManager>
                gDefault = Reflect.on("android.app.ActivityManagerNative").field("gDefault").get(null);
            }
            if (gDefault == null)
                return false;
            Object currentActivityManagerProxy = Reflect.on("android.util.Singleton").field("mInstance").get(gDefault);
            if (currentActivityManagerProxy == null)
                return false;
            Object proxy;
            if (Build.VERSION.SDK_INT >= 26) {
                ActivityManagerDelegate delegate = new ActivityManagerDelegate(currentActivityManagerProxy);
                if (currentActivityManagerProxy.getClass().isInstance(delegate))
                    return true;
                Class<?> amClass = Class.forName("android.app.IActivityManager");
                proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{amClass}, delegate);
            } else {
                ActivityManagerDelegate delegate = new ActivityManagerDelegate(currentActivityManagerProxy);
                if (currentActivityManagerProxy.getClass().isInstance(delegate))
                    return true;
                Class<?>[] interfaces = Class.forName("android.app.ActivityManagerNative").getInterfaces();
                proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, delegate);
            }
            Reflect.on("android.util.Singleton").field("mInstance").set(gDefault, proxy);
        } catch (Throwable e) {
            e.printStackTrace();
            RecoveryLog.e(e.toString());
        }
        return true;
    }

}
