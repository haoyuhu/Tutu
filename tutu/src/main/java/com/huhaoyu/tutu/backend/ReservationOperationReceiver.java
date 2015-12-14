package com.huhaoyu.tutu.backend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Reservation operation receiver
 * Created by coderhuhy on 15/12/13.
 */
public class ReservationOperationReceiver extends WakefulBroadcastReceiver {

    private static final String LogTag = ReservationOperationReceiver.class.getCanonicalName();

    public void onReceive(Context context, Intent intent) {
        String pattern = "yyyy-MM-dd HH:mm";
        Calendar calendar = Calendar.getInstance();
        String dateTime = DateTimeUtilities.formatReservationDate(calendar, pattern);
        Log.i(LogTag, "Tutu reservation operation receiver awake on " + dateTime);
        ComponentName name = new ComponentName(context.getPackageName(),
                ReservationOperationService.class.getName());
        startWakefulService(context, intent.setComponent(name));
        TutuNotificationManager.getInstance().removeNotification();
    }
}
