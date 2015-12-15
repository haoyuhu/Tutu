package com.huhaoyu.tutu.backend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.huhaoyu.tutu.BuildConfig;

import java.util.Calendar;

import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Tutu alarm receiver
 * Created by coderhuhy on 15/12/12.
 */
public class TutuAlarmReceiver extends WakefulBroadcastReceiver {

    private static final String LogTag = TutuAlarmReceiver.class.getCanonicalName();

    public void onReceive(Context context, Intent intent) {
        String pattern = "yyyy-MM-dd HH:mm";
        Calendar calendar = Calendar.getInstance();
        String dateTime = DateTimeUtilities.formatReservationDate(calendar, pattern);
        Log.i(LogTag, "Tutu alarm receiver awake on " + dateTime);

        RegularAlarmManager manager = RegularAlarmManager.getInstance();
        if (manager.shouldExcuteTasks()) {
            if (BuildConfig.DEBUG) {
                TutuNotificationManager.getInstance().testNotification();
            }
            manager.updateTimeStamp();
            ComponentName name = new ComponentName(context.getPackageName(), TutuAlarmService.class.getName());
            startWakefulService(context, intent.setComponent(name));
        } else {
            completeWakefulIntent(intent);
        }
    }
}
