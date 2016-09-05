package com.zxy.recovery.core;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.zxy.recovery.tools.RecoveryLog;
import com.zxy.recovery.tools.RecoveryUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zhengxiaoyong on 16/8/30.
 */
class ActivityManagerDelegate implements InvocationHandler {

    private Object mBaseActivityManagerProxy;

    ActivityManagerDelegate(Object o) {
        this.mBaseActivityManagerProxy = o;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        switch (methodName) {
            case "finishActivity":
//            case "finishActivityAffinity":
                Class<? extends Activity> clazz = Recovery.getInstance().getMainPageClass();
                if (clazz == null)
                    break;
                int count = ActivityStackCompat.getActivityCount(Recovery.getInstance().getContext());
                String baseActivityName = ActivityStackCompat.getBaseActivityName(Recovery.getInstance().getContext());
                if (TextUtils.isEmpty(baseActivityName))
                    break;
                RecoveryLog.e("currentActivityCount: " + count);
                RecoveryLog.e("baseActivityName: " + baseActivityName);
                if (count == 1 && !clazz.getName().equals(baseActivityName)) {
                    Intent intent = new Intent(Recovery.getInstance().getContext(), clazz);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    if (RecoveryUtil.isIntentAvailable(Recovery.getInstance().getContext(), intent))
                        Recovery.getInstance().getContext().startActivity(intent);
                }
                break;
            default:
                break;

        }

        return method.invoke(mBaseActivityManagerProxy, args);
    }

}
