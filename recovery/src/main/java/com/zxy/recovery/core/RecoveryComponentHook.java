package com.zxy.recovery.core;

import com.zxy.recovery.tools.RecoveryLog;
import com.zxy.recovery.tools.Reflect;

import java.lang.reflect.Proxy;

/**
 * Created by zhengxiaoyong on 16/8/30.
 */
final class RecoveryComponentHook {

    static boolean hookActivityManagerProxy() {
        //Singleton<IActivityManager>
        Object gDefault = Reflect.on("android.app.ActivityManagerNative").field("gDefault").get(null);
        if (gDefault == null)
            return false;
        Object currentActivityManagerProxy = Reflect.on("android.util.Singleton").field("mInstance").get(gDefault);
        if (currentActivityManagerProxy == null)
            return false;
        try {
            ActivityManagerDelegate delegate = new ActivityManagerDelegate(currentActivityManagerProxy);
            if (currentActivityManagerProxy.getClass().isInstance(delegate))
                return true;
            Class<?>[] interfaces = Class.forName("android.app.ActivityManagerNative").getInterfaces();
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, delegate);
            Reflect.on("android.util.Singleton").field("mInstance").set(gDefault, proxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            RecoveryLog.e(e.toString());
        }
        return true;
    }

}
