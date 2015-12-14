package com.huhaoyu.tutu.utils;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Memory watcher
 * Created by coderhuhy on 15/12/14.
 */
public class MemoryWatcher {

    private static Application application;
    private static RefWatcher watcher;

    public static void initForDebug(Application app) {
        watcher = LeakCanary.install(app);
        application = app;
    }

    public static void initForRealise(Application app) {
        watcher = RefWatcher.DISABLED;
        application = app;
    }

    @Nullable
    public static RefWatcher getWatcher(Context context) {
        if (context.getApplicationContext() == application) {
            return watcher;
        }
        return null;
    }

}
