package com.huhaoyu.tutu.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.huhaoyu.tutu.TUTuApplication;

import java.util.List;

/**
 * Utilities
 * Created by coderhuhy on 15/12/8.
 */
public class Utilities {

    public static boolean isAppOnForeground() {
        Application application = TUTuApplication.getInstance();
        ActivityManager activityManager = (ActivityManager)application.getSystemService(
                Context.ACTIVITY_SERVICE);
        String packageName = application.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

}
