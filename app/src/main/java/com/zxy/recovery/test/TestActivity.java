package com.zxy.recovery.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        new Thread(new Runnable() {
//            int i = 0;
//
//            @Override
//            public void run() {
//                while (true) {
//                    if (i == 3) {
//                        Activity activity = null;
//                        activity.finish();
//                    }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    i++;
//                }
//            }
//        }).start();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btn) {
            Activity activity = null;
            activity.finish();
        } else if (view.getId() == R.id.btn2) {
            Intent intent = new Intent(TestActivity.this, TestActivity2.class);
            startActivity(intent);
        }
    }
}
