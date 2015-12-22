package com.huhaoyu.tutu.backend;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.huhaoyu.tutu.utils.TutuConstants;

import java.util.Calendar;

import mu.lab.thulib.thucab.CabConstants;

/**
 * Regular alarm manager
 * Created by coderhuhy on 15/12/12.
 */
public class RegularAlarmManager {

    private static final String LogTag = RegularAlarmManager.class.getCanonicalName();

    private long timeStamp = 0;
    private long notifyTimeStamp = 0;
    private long autoResvTimeStamp = 0;
    private final HourMinute muteStart;
    private final HourMinute muteEnd;


    private RegularAlarmManager() {
        muteStart = new HourMinute(TutuConstants.Constants.DEFAULT_REGULAR_TASK_MUTE_START_HOUR,
                TutuConstants.Constants.DEFAULT_REGULAR_TASK_MUTE_START_MINUTE);
        muteEnd = new HourMinute(TutuConstants.Constants.DEFAULT_REGULAR_TASK_MUTE_END_HOUR,
                TutuConstants.Constants.DEFAULT_REGULAR_TASK_MUTE_END_MINUTE);
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
                PendingIntent.FLAG_CANCEL_CURRENT);

        // send to alarm manager
        final long start = 0;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, start,
                TutuConstants.Constants.ALARM_INTERVAL_IN_MILLIS, pendingIntent);
    }

    public boolean shouldExecuteTasks() {
        long current = System.currentTimeMillis();
        return current - timeStamp >= TutuConstants.Constants.ALARM_INTERVAL_VALIDATE_IN_MILLIS && !inMutePeriod();
    }

    public boolean shouldNotify() {
        long current = System.currentTimeMillis();
        return current - notifyTimeStamp >= TutuConstants.Constants.ALARM_INTERVAL_VALIDATE_IN_MILLIS;
    }

    public boolean shouldAutoReservation() {
        long current = System.currentTimeMillis();
        return current - autoResvTimeStamp >= TutuConstants.Constants.DEFAULT_AUTO_RESERVATION_TASK_INTERVAL_IN_MILLIS;
    }

    public void updateTimeStamp() {
        this.timeStamp = System.currentTimeMillis();
    }

    public void updateNotifyTimeStamp() {
        this.notifyTimeStamp = System.currentTimeMillis();
    }

    public void updateAutoResvTimeStamp() {
        this.autoResvTimeStamp = System.currentTimeMillis();
    }


    protected boolean inMutePeriod() {
        Calendar calendar = Calendar.getInstance();
        int currHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currMinute = calendar.get(Calendar.MINUTE);

        return muteStart.compare(currHour, currMinute) && !muteEnd.compare(currHour, currMinute);
    }

    protected class HourMinute {
        int hours;
        int minutes;

        public HourMinute(int hours, int minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }

        boolean compare(int hours, int minutes) {
            int other = hours * CabConstants.DateTimeConstants.MINUTE_OF_HOUR + minutes;
            int inst = this.hours * CabConstants.DateTimeConstants.MINUTE_OF_HOUR + this.minutes;
            return inst <= other;
        }
    }

}
