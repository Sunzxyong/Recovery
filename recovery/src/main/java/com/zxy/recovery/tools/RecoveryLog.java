package com.zxy.recovery.tools;

import android.util.Log;

import com.zxy.recovery.core.Recovery;

/**
 * Created by zhengxiaoyong on 16/8/26.
 */
public class RecoveryLog {

    private static final String TAG = "Recovery";

    public static void e(String message) {
        if (Recovery.getInstance().isDebug())
            Log.e(TAG, message);
    }
}
