package com.zxy.recovery.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zxy.recovery.core.ActivityStackCompat;
import com.zxy.recovery.core.Recovery;

/**
 * Created by zhengxiaoyong on 16/8/30.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                int count = ActivityStackCompat.getActivityCount(Recovery.getInstance().getContext());
                Log.e("zxy", "realCount: " + count);
                String baseActivity = ActivityStackCompat.getBaseActivityName(Recovery.getInstance().getContext());
                Log.e("zxy", "realBaseActivityName: " + baseActivity);
            }
        });

    }
}
