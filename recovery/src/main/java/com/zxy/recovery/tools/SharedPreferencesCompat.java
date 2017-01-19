package com.zxy.recovery.tools;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by zhengxiaoyong on 16/8/27.
 */
public class SharedPreferencesCompat {

    private SharedPreferencesCompat() {
        throw new SharedPreferencesException("Stub!");
    }

    private static SharedPreferences getSharedPrefs(Context context, String sharedPrefsName) {
        checkNotNull(context, "Context can not be null!");
        checkNotEmpty(sharedPrefsName, "SharedPreferences name can not be empty!");
        return context.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);
    }

    public static void put(Context context, String sharedPrefsName, String key, String value) {
        checkNotEmpty(key, "SharedPreferences key can not be empty!");
        checkNotNull(value, "SharedPreferences value can not be null!");
        SharedPreferences.Editor editor = getSharedPrefs(context, sharedPrefsName).edit();
        editor.putString(key, value);
        SharedPreferencesEditorCompat.apply(editor);
    }

    public static String get(Context context, String sharedPrefsName, String key, String defValue) {
        checkNotEmpty(key, "SharedPreferences key can not be empty!");
        return getSharedPrefs(context, sharedPrefsName).getString(key, defValue);
    }

    public static void clear(Context context, String sharedPrefsName) {
        SharedPreferences.Editor editor = getSharedPrefs(context, sharedPrefsName).edit();
        editor.clear();
        SharedPreferencesEditorCompat.apply(editor);
    }

    public static void remove(Context context, String sharedPrefsName, String key) {
        checkNotEmpty(key, "SharedPreferences key can not be empty!");
        SharedPreferences.Editor editor = getSharedPrefs(context, sharedPrefsName).edit();
        editor.remove(key);
        SharedPreferencesEditorCompat.apply(editor);
    }

    public static Map<String, ?> getAll(Context context, String sharedPrefsName) {
        checkNotNull(context, "Context can not be null!");
        return getSharedPrefs(context, sharedPrefsName).getAll();
    }

    public static boolean contains(Context context, String sharedPrefsName, String key) {
        checkNotEmpty(key, "SharedPreferences key can not be empty!");
        return getSharedPrefs(context, sharedPrefsName).contains(key);
    }

    public static Set<String> getStringSet(Context context, String sharedPrefsName, String key, Set<String> defValues) {
        checkNotEmpty(key, "SharedPreferences key can not be empty!");
        return getSharedPrefs(context, sharedPrefsName).getStringSet(key, defValues);
    }

    public static Builder newBuilder(Context context, String sharedPrefsName) {
        return new Builder(context, sharedPrefsName);
    }

    public static final class Builder {

        private SharedPreferences.Editor mEditor;

        private Builder(Context context, String sharedPrefsName) {
            mEditor = getSharedPrefs(context, sharedPrefsName).edit();
        }

        public Builder put(String key, String value) {
            checkNotEmpty(key, "SharedPreferences key can not be empty!");
            checkNotNull(value, "SharedPreferences value can not be null!");
            mEditor.putString(key, value);
            return this;
        }

        public void apply() {
            if (mEditor != null)
                SharedPreferencesEditorCompat.apply(mEditor);
        }

        public void commit() {
            if (mEditor != null)
                SharedPreferencesEditorCompat.commit(mEditor);
        }

    }

    private static <T> void checkNotNull(T t, String message) {
        if (t == null)
            throw new SharedPreferencesException(String.valueOf(message));
    }

    private static void checkNotEmpty(String t, String message) {
        if (TextUtils.isEmpty(t))
            throw new SharedPreferencesException(String.valueOf(message));
    }

    private static final class SharedPreferencesException extends RuntimeException {
        public SharedPreferencesException(String message, Throwable cause) {
            super(message, cause);
        }

        public SharedPreferencesException(String message) {
            super(message);
        }
    }

    private static final class SharedPreferencesEditorCompat {

        private static final ExecutorService SINGLE_THREAD_POOL;

        static {
            SINGLE_THREAD_POOL = Executors.newFixedThreadPool(1, new SharedPreferencesThreadFactory());
        }

        static void apply(final SharedPreferences.Editor editor) {
            try {
                editor.apply();
            } catch (Throwable e) {
                SINGLE_THREAD_POOL.submit(new Runnable() {
                    @Override
                    public void run() {
                        editor.commit();
                    }
                });
            }
        }

        static void commit(SharedPreferences.Editor editor) {
            editor.commit();
        }
    }

    private static final class SharedPreferencesThreadFactory implements ThreadFactory {

        private static final String THREAD_NAME = "SharedPreferencesThread";

        @Override
        public Thread newThread(@NonNull final Runnable r) {
            Runnable wrapper = new Runnable() {
                @Override
                public void run() {
                    try {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    r.run();
                }
            };
            Thread thread = new Thread(wrapper, THREAD_NAME);
            if (thread.isDaemon())
                thread.setDaemon(false);
            return thread;
        }
    }
}