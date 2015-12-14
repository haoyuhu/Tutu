package com.huhaoyu.tutu.backend;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.huhaoyu.tutu.utils.TutuConstants;

/**
 * Regular alarm manager
 * Created by coderhuhy on 15/12/12.
 */
public class RegularAlarmManager {

    private static final String LogTag = RegularAlarmManager.class.getCanonicalName();

    private RegularAlarmManager() {

    }

    private static class AlarmManagerHolder {
        static RegularAlarmManager instance = new RegularAlarmManager();
    }

    public static RegularAlarmManager getInstance() {
        return AlarmManagerHolder.instance;
    }

    public void init(Context context) {
        Intent intent = new Intent(context, TutuAlarmReceiver.class);
        intent.setAction(TutuAlarmService.class.getCanonicalName());

        // construct pending intent
        final int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // send to alarm manager
        final long start = 0;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, start,
                TutuConstants.Constants.ALARM_INTERVAL_IN_MILLIS, pendingIntent);
    }

}
