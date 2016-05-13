package com.renren.mobile.x2.utils.usage;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Debug;

public class CpuUsage {
    private static final String TAG = "CpuUsage";

    public CpuUsage() {
    }

    long update() {
        return Debug.threadCpuTimeNanos();
    }
}
