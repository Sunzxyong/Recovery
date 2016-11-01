package com.zxy.recovery.core;

import java.io.Serializable;

/**
 * Created by zhengxiaoyong on 16/8/30.
 */
public class CrashData implements Serializable {

    public long crashTime;

    public int crashCount;

    public boolean shouldRestart;

    private CrashData() {
    }

    public static CrashData newInstance() {
        return new CrashData();
    }

    public CrashData time(long time) {
        this.crashTime = time;
        return this;
    }

    public CrashData count(int count) {
        this.crashCount = count;
        return this;
    }

    public CrashData restart(boolean restart) {
        this.shouldRestart = restart;
        return this;
    }

    @Override
    public String toString() {
        return "CrashData{" +
                "crashCount=" + crashCount +
                ", crashTime=" + crashTime +
                ", shouldRestart=" + shouldRestart +
                '}';
    }
}
