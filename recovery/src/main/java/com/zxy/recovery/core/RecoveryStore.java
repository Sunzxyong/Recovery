package com.zxy.recovery.core;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhengxiaoyong on 16/8/28.
 */
public final class RecoveryStore {

    static final String RECOVERY_INTENT = "recovery_intent";

    static final String RECOVERY_INTENTS = "recovery_intents";

    static final String RECOVERY_STACK = "recovery_stack";

    static final String IS_DEBUG = "recovery_is_debug";

    static final String STACK_TRACE = "recovery_stack_trace";

    static final String EXCEPTION_DATA = "recovery_exception_data";

    static final String EXCEPTION_CAUSE = "recovery_exception_cause";

    private volatile static RecoveryStore sInstance;

    private static final Object LOCK = new Object();

    private List<WeakReference<Activity>> mRunningActivities;

    private Intent mIntent;

    private RecoveryStore() {
        mRunningActivities = new CopyOnWriteArrayList<>();
    }

    public static RecoveryStore getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new RecoveryStore();
                }
            }
        }
        return sInstance;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public synchronized void setIntent(Intent intent) {
        mIntent = intent;
    }

    public boolean verifyActivity(Activity activity) {
        return activity != null && !Recovery.getInstance().getSkipActivities().contains(activity.getClass()) && !RecoveryActivity.class.isInstance(activity);
    }

    public List<WeakReference<Activity>> getRunningActivities() {
        return mRunningActivities;
    }

    public void putActivity(Activity activity) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        mRunningActivities.add(weakReference);
    }

    public boolean contains(Activity activity) {
        if (activity == null)
            return false;
        int size = mRunningActivities.size();
        for (int i = 0; i < size; i++) {
            WeakReference<Activity> refer = mRunningActivities.get(i);
            if (refer == null)
                continue;
            Activity tmp = refer.get();
            if (tmp == null)
                continue;
            if (activity == tmp)
                return true;
        }
        return false;
    }

    public void removeActivity(Activity activity) {
        for (WeakReference<Activity> activityWeakReference : mRunningActivities) {
            if (activityWeakReference == null)
                continue;
            Activity tmpActivity = activityWeakReference.get();
            if (tmpActivity == null)
                continue;
            if (tmpActivity == activity) {
                mRunningActivities.remove(activityWeakReference);
                break;
            }
        }
    }

    int getRunningActivityCount() {
        AtomicInteger count = new AtomicInteger(0);
        for (WeakReference<Activity> activityWeakReference : mRunningActivities) {
            if (activityWeakReference == null)
                continue;
            Activity activity = activityWeakReference.get();
            if (activity == null)
                continue;
            count.set(count.incrementAndGet());
        }
        return count.get();
    }

    ComponentName getBaseActivity() {
        for (WeakReference<Activity> activityWeakReference : mRunningActivities) {
            if (activityWeakReference == null)
                continue;
            Activity tmpActivity = activityWeakReference.get();
            if (tmpActivity == null)
                continue;
            return tmpActivity.getComponentName();
        }
        return null;
    }

    ArrayList<Intent> getIntents() {
        ArrayList<Intent> intentList = new ArrayList<>();
        for (WeakReference<Activity> activityWeakReference : mRunningActivities) {
            if (activityWeakReference == null)
                continue;
            Activity tmpActivity = activityWeakReference.get();
            if (tmpActivity == null)
                continue;
            intentList.add((Intent) tmpActivity.getIntent().clone());
        }
        return intentList;
    }

    static final class ExceptionData implements Parcelable {
        String type;
        String className;
        String methodName;
        int lineNumber;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.type);
            dest.writeString(this.className);
            dest.writeString(this.methodName);
            dest.writeInt(this.lineNumber);
        }

        ExceptionData() {
        }

        public static ExceptionData newInstance() {
            return new ExceptionData();
        }

        public ExceptionData type(String type) {
            this.type = type;
            return this;
        }

        public ExceptionData className(String className) {
            this.className = className;
            return this;
        }

        public ExceptionData methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public ExceptionData lineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        protected ExceptionData(Parcel in) {
            this.type = in.readString();
            this.className = in.readString();
            this.methodName = in.readString();
            this.lineNumber = in.readInt();
        }

        public static final Creator<ExceptionData> CREATOR = new Creator<ExceptionData>() {
            @Override
            public ExceptionData createFromParcel(Parcel source) {
                return new ExceptionData(source);
            }

            @Override
            public ExceptionData[] newArray(int size) {
                return new ExceptionData[size];
            }
        };

        @Override
        public String toString() {
            return "ExceptionData{" +
                    "className='" + className + '\'' +
                    ", type='" + type + '\'' +
                    ", methodName='" + methodName + '\'' +
                    ", lineNumber=" + lineNumber +
                    '}';
        }
    }
}
