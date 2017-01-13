package com.zxy.recovery.core;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zxy.recovery.R;
import com.zxy.recovery.tools.RecoverySharedPrefsUtil;
import com.zxy.recovery.tools.RecoveryUtil;
import com.zxy.recovery.tools.Reflect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zhengxiaoyong on 16/8/26.
 */
public final class RecoveryActivity extends AppCompatActivity {

    public static final String RECOVERY_MODE_ACTIVE = "recovery_mode_active";

    private static final String DEFAULT_CRASH_FILE_DIR_NAME = "recovery_crash";

    private boolean isDebugMode = false;

    private boolean isDebugModeActive = false;

    private RecoveryStore.ExceptionData mExceptionData;

    private Toolbar mToolbar;

    private String mStackTrace;

    private String mCause;

    private Button mRecoverBtn;

    private Button mRestartBtn;

    private Button mRestartClearBtn;

    private View mMainLayout;

    private View mDebugLayout;

    private TextView mExceptionTypeTv;

    private TextView mClassNameTv;

    private TextView mMethodNameTv;

    private TextView mLineNumberTv;

    private TextView mStackTraceTv;

    private TextView mCauseTv;

    private TextView mCrashTipsTv;

    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);
        setupToolbar();
        initView();
        initData();
        setupEvent();
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setAlpha(0.95f);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle(RecoveryUtil.getAppName(this));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDebugModeActive = false;
                showMainView();
                setDisplayHomeAsUpEnabled(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isDebugMode)
            return false;
        if (isDebugModeActive) {
            getMenuInflater().inflate(R.menu.menu_recovery_sub, menu);
            return true;
        }
        getMenuInflater().inflate(R.menu.menu_recovery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_debug) {
            isDebugModeActive = true;
            showDebugView();
            setDisplayHomeAsUpEnabled(true);
        } else if (id == R.id.action_save) {
            boolean isSuccess = saveCrashData();
            Toast.makeText(this, isSuccess ? "Save success!" : "Save failed!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mMainLayout = findViewById(R.id.recovery_main_layout);
        mDebugLayout = findViewById(R.id.recovery_debug_layout);
        mRecoverBtn = (Button) findViewById(R.id.btn_recover);
        mRestartBtn = (Button) findViewById(R.id.btn_restart);
        mRestartClearBtn = (Button) findViewById(R.id.btn_restart_clear);
        mExceptionTypeTv = (TextView) findViewById(R.id.tv_type);
        mClassNameTv = (TextView) findViewById(R.id.tv_class_name);
        mMethodNameTv = (TextView) findViewById(R.id.tv_method_name);
        mLineNumberTv = (TextView) findViewById(R.id.tv_line_number);
        mStackTraceTv = (TextView) findViewById(R.id.tv_stack_trace);
        mCauseTv = (TextView) findViewById(R.id.tv_cause);
        mCrashTipsTv = (TextView) findViewById(R.id.tv_crash_tips);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScrollView.setPadding(0, RecoveryUtil.dp2px(getApplication(), 56), 0, 0);
        } else {
            mScrollView.setPadding(0, RecoveryUtil.dp2px(getApplication(), 72), 0, 0);
        }
    }

    private void initData() {
        isDebugMode = isDebugMode();
        if (isDebugMode)
            invalidateOptionsMenu();
        mExceptionData = getExceptionData();
        mCause = getCause();
        mStackTrace = getStackTrace();
    }

    private void setupEvent() {
        mRecoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean restart = RecoverySharedPrefsUtil.shouldRestartApp();
                if (restart) {
                    RecoverySharedPrefsUtil.clear();
                    restart();
                    return;
                }
                if (isRecoverStack()) {
                    recoverActivityStack();
                } else {
                    recoverTopActivity();
                }
            }
        });

        mRestartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean restart = RecoverySharedPrefsUtil.shouldRestartApp();
                if (restart)
                    RecoverySharedPrefsUtil.clear();
                restart();
            }
        });

        mRestartClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(RecoveryActivity.this)
                        .setTitle(getResources().getString(R.string.recovery_dialog_tips))
                        .setMessage(getResources().getString(R.string.recovery_dialog_tips_msg))
                        .setPositiveButton(getResources().getString(R.string.recovery_dialog_sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialog != null)
                                    dialog.dismiss();
                                RecoveryUtil.clearApplicationData();
                                restart();
                            }
                        }).setNegativeButton(getResources().getString(R.string.recovery_dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialog != null)
                                    dialog.dismiss();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        mCrashTipsTv.setText(String.format(getResources().getString(R.string.recovery_crash_tips_msg), RecoveryUtil.getAppName(this)));

        if (mExceptionData != null) {
            String type = mExceptionData.type == null ? "" : mExceptionData.type;
            String name = mExceptionData.className == null ? "" : mExceptionData.className;
            mExceptionTypeTv.setText(String.format(getResources().getString(R.string.recovery_exception_type), type.substring(type.lastIndexOf('.') + 1)));
            mClassNameTv.setText(String.format(getResources().getString(R.string.recovery_class_name), name.substring(name.lastIndexOf('.') + 1)));
            mMethodNameTv.setText(String.format(getResources().getString(R.string.recovery_method_name), mExceptionData.methodName));
            mLineNumberTv.setText(String.format(getResources().getString(R.string.recovery_line_number), mExceptionData.lineNumber));
        }
        mCauseTv.setText(String.valueOf(mCause));
        mStackTraceTv.setText(String.valueOf(mStackTrace));
    }

    private boolean isDebugMode() {
        return getIntent().getBooleanExtra(RecoveryStore.IS_DEBUG, false);
    }

    private RecoveryStore.ExceptionData getExceptionData() {
        return getIntent().getParcelableExtra(RecoveryStore.EXCEPTION_DATA);
    }

    private String getCause() {
        return getIntent().getStringExtra(RecoveryStore.EXCEPTION_CAUSE);
    }

    private String getStackTrace() {
        return getIntent().getStringExtra(RecoveryStore.STACK_TRACE);
    }

    private void restart() {
        Intent launchIntent = getApplication().getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(launchIntent);
            overridePendingTransition(0, 0);
        }
        finish();
    }

    private void recoverTopActivity() {
        Intent intent = getRecoveryIntent();
        if (intent != null && RecoveryUtil.isIntentAvailable(this, intent)) {
            intent.setExtrasClassLoader(getClassLoader());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(RECOVERY_MODE_ACTIVE, true);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            return;
        }
        restart();
    }

    private boolean isRecoverStack() {
        boolean hasRecoverStack = getIntent().hasExtra(RecoveryStore.RECOVERY_STACK);
        return !hasRecoverStack || getIntent().getBooleanExtra(RecoveryStore.RECOVERY_STACK, true);
    }

    private void recoverActivityStack() {
        ArrayList<Intent> intents = getRecoveryIntents();
        if (intents != null && !intents.isEmpty()) {
            ArrayList<Intent> availableIntents = new ArrayList<>();
            for (Intent tmp : intents) {
                if (tmp != null && RecoveryUtil.isIntentAvailable(this, tmp)) {
                    tmp.setExtrasClassLoader(getClassLoader());
                    availableIntents.add(tmp);
                }
            }
            if (!availableIntents.isEmpty()) {
                availableIntents.get(0).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                availableIntents.get(availableIntents.size() - 1).putExtra(RECOVERY_MODE_ACTIVE, true);
                startActivities(availableIntents.toArray(new Intent[availableIntents.size()]));
                overridePendingTransition(0, 0);
                finish();
                return;
            }
        }
        restart();
    }

    private Intent getRecoveryIntent() {
        boolean hasRecoverIntent = getIntent().hasExtra(RecoveryStore.RECOVERY_INTENT);
        if (!hasRecoverIntent)
            return null;
        return getIntent().getParcelableExtra(RecoveryStore.RECOVERY_INTENT);
    }

    private ArrayList<Intent> getRecoveryIntents() {
        boolean hasRecoveryIntents = getIntent().hasExtra(RecoveryStore.RECOVERY_INTENTS);
        if (!hasRecoveryIntents)
            return null;
        return getIntent().getParcelableArrayListExtra(RecoveryStore.RECOVERY_INTENTS);
    }

    private boolean saveCrashData() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String date = format.format(new Date(System.currentTimeMillis()));
        File dir = new File(getExternalFilesDir(null) + File.separator + DEFAULT_CRASH_FILE_DIR_NAME);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dir, String.valueOf(date) + ".txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write("\nException:\n" + (mExceptionData == null ? null : mExceptionData.toString()) + "\n\n");
            writer.write("Cause:\n" + mCause + "\n\n");
            writer.write("StackTrace:\n" + mStackTrace + "\n\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    private void killProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private void setDisplayHomeAsUpEnabled(boolean enabled) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
        final ImageButton navButton = (ImageButton) Reflect.on(Toolbar.class).field("mNavButtonView").get(mToolbar);
        if (navButton != null) {
            if (enabled) {
                navButton.setVisibility(View.VISIBLE);
            } else {
                navButton.setVisibility(View.GONE);
            }
        }
        invalidateOptionsMenu();
    }

    private void showDebugView() {
        mMainLayout.setVisibility(View.GONE);
        mDebugLayout.setVisibility(View.VISIBLE);
    }

    private void showMainView() {
        mMainLayout.setVisibility(View.VISIBLE);
        mDebugLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && isDebugModeActive) {
            isDebugModeActive = false;
            showMainView();
            setDisplayHomeAsUpEnabled(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        killProcess();
    }

}
