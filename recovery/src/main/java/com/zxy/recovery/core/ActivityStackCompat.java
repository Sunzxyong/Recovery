package com.zxy.recovery.core;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * Created by zhengxiaoyong on 16/8/30.
 */
public class ActivityStackCompat {

    private static ActivityManager.AppTask getTopTaskAfterL(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return activityManager.getAppTasks().get(0);
        return null;
    }

    private static ActivityManager.RunningTaskInfo getTopTaskBeforeL(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = null;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                tasks = activityManager.getRunningTasks(1);
        } catch (Exception e) {
            return null;
        }
        if (tasks == null || tasks.size() == 0)
            return null;
        return tasks.get(0);
    }

    /**
     * @param context Context.
     * @return Number of activities in this task.
     */
    public static int getActivityCount(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.AppTask appTask = getTopTaskAfterL(context);
            if (appTask == null)
                return 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return appTask.getTaskInfo().numActivities;
            } else {
                //or mActivities
                return RecoveryStore.getInstance().getRunningActivityCount();
            }
        } else {
            ActivityManager.RunningTaskInfo taskInfo = getTopTaskBeforeL(context);
            if (taskInfo == null)
                return 0;
            return taskInfo.numActivities;
        }
    }

    public static String getBaseActivityName(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.AppTask appTask = getTopTaskAfterL(context);
            if (appTask == null)
                return null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return appTask.getTaskInfo().baseActivity.getClassName();
            } else {
                ComponentName componentName = RecoveryStore.getInstance().getBaseActivity();
                return componentName == null ? null : componentName.getClassName();
            }
        } else {
            ActivityManager.RunningTaskInfo taskInfo = getTopTaskBeforeL(context);
            if (taskInfo == null)
                return null;
            return taskInfo.baseActivity.getClassName();
        }
    }

}
