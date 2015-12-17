package com.huhaoyu.tutu.backend;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.ui.ReservationListActivity;
import com.huhaoyu.tutu.utils.PreferencesUtils;
import com.huhaoyu.tutu.utils.TutuConstants;

import java.util.Calendar;
import java.util.List;

import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.ResvRecordStore;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.StudentAccount;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * Tutu notification manager
 * Created by coderhuhy on 15/12/12.
 */
public class TutuNotificationManager implements Observer<List<ReservationRecord>> {

    private static final String LogTag = TutuNotificationManager.class.getCanonicalName();
    private static final int NOTIFICATION_LIGHT = Notification.DEFAULT_LIGHTS;
    private static final int NOTIFICATION_VIBRATE = Notification.DEFAULT_VIBRATE;
    private static final int NOTIFICATION_SOUND = Notification.DEFAULT_SOUND;

    private boolean light = true;
    private boolean vibrate = true;
    private boolean sound = true;

    public enum PushType {
        OpenApp("go_app"), Custom("go_custom"), Unknown("unknown");
        String key;

        PushType(String key) {
            this.key = key;
        }

        static PushType from(String type) {
            PushType[] types = PushType.values();
            for (PushType t : types) {
                if (t.key.equals(type)) {
                    return t;
                }
            }
            return Unknown;
        }
    }

    public enum CustomType {
        Update("update"), Unknown("unknown");
        String key;

        CustomType(String key) {
            this.key = key;
        }

        static CustomType from(String type) {
            CustomType[] types = CustomType.values();
            for (CustomType t : types) {
                if (t.key.equals(type)) {
                    return t;
                }
            }
            return Unknown;
        }
    }

    public enum NotificationOperation {
        Deletion("notification_deletion"),
        Modification("notification_modification"),
        Postpone("notification_postpone"),
        OpenActivity("notification_open");
        String key;

        NotificationOperation(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private Context context;

    private TutuNotificationManager() {

    }

    public static TutuNotificationManager getInstance() {
        return NotificationManagerHolder.instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    private static class NotificationManagerHolder {
        static TutuNotificationManager instance = new TutuNotificationManager();
    }

    public void check(StudentAccount account) {
        if (PreferencesUtils.getAutoNotification()) {
            ResvRecordStore.getResvRecordsFromRealm(account)
                    .subscribeOn(Schedulers.io())
                    .subscribe(this);
        }
    }

    @Override
    public void onCompleted() {
        Log.i(LogTag, "notification on completed...");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(LogTag, e.getMessage(), e);
    }

    @Override
    public void onNext(List<ReservationRecord> records) {
        for (ReservationRecord record : records) {
            try {
                Calendar current = Calendar.getInstance();
                if (DateTimeUtilities.isTheSameDay(record.getDate(), current)) {
                    Calendar start = record.getStartDateTime();
                    long interval = DateTimeUtilities.calculateInterval(start, current);
                    if (interval >= 0 && interval <= TutuConstants.Constants.DEFAULT_NOTIFICATION_INTERVAL_IN_MILLIS) {
                        notifyReservation(record);
                        break;
                    }
                }
            } catch (DateTimeUtilities.DateTimeException e) {
                Log.e(LogTag, e.getDetails(), e);
            }
        }
    }

    public void removeNotification() {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(TutuConstants.Constants.TUTU_RESERVATION_NOTIFICATION_ID);
    }

    public void notifyReservation(ReservationRecord record) {
        Calendar sc, ec;
        try {
            sc = record.getStartDateTime();
            ec = record.getEndDateTime();
        } catch (DateTimeUtilities.DateTimeException e) {
            Log.e(LogTag, e.getDetails(), e);
            return;
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        final String patternTime = "HH:mm";
        final String start = DateTimeUtilities.formatReservationDate(sc, patternTime);
        final String end = DateTimeUtilities.formatReservationDate(ec, patternTime);
        String room = record.getRoomName();
        long millis = DateTimeUtilities.calculateInterval(sc, Calendar.getInstance());
        if (millis <= 0 || millis > TutuConstants.Constants.DEFAULT_NOTIFICATION_INTERVAL_IN_MILLIS) return;
        int minutes = (int) (millis / CabConstants.DateTimeConstants.MILLIS_OF_SECOND / CabConstants.DateTimeConstants.SECOND_OF_MINUTE);
        String title = String.format(context.getString(R.string.tutu_notification_title), room);
        String content = String.format(context.getString(R.string.tutu_notification_content), start, end, minutes);
        String id = record.getReservationId();

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(content);
        PendingIntent deletion = new OperationIntentCreator().createDeletion(id);
        PendingIntent modification = new OperationIntentCreator().createModification(id);
        PendingIntent postpone = new OperationIntentCreator().createPostpone(id);
        builder.addAction(0, context.getString(R.string.tutu_notification_deletion), deletion);
        builder.addAction(0, context.getString(R.string.tutu_notification_modification), modification);
        builder.addAction(0, context.getString(R.string.tutu_notification_delay), postpone);
        builder.setDefaults(getNotificationDefaults());
        builder.setAutoCancel(true);

        Notification notification = builder.build();
        manager.notify(TutuConstants.Constants.TUTU_RESERVATION_NOTIFICATION_ID, notification);
    }

    public void notifyMessage(boolean l, boolean v, boolean s, String ticker, String content, String type, String custom) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(ticker);
        builder.setContentText(content);
        builder.setDefaults(getNotificationDefaults(l, v, s));
        builder.setAutoCancel(true);

        PushType push = PushType.from(type);
        CustomType cst = CustomType.from(custom);
        switch (push) {
            case OpenApp: {
                Intent i = new Intent(context, ReservationListActivity.class);
                PendingIntent pi = PendingIntent.getActivity(context, NotificationOperation.OpenActivity.ordinal(),
                        i, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pi);
                break;
            }
            case Custom: {
                break;
            }
            default:
                break;
        }

        Notification notification = builder.build();
        manager.notify(TutuConstants.Constants.TUTU_MESSAGE_NOTIFICATION_ID, notification);
    }

    public void testNotification(boolean auto, boolean notify) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        String pattern = "yyyy-MM-dd HH:mm";
        String dt = DateTimeUtilities.formatReservationDate(Calendar.getInstance(), pattern);
        String autoStr = "auto: " + String.valueOf(auto);
        String notifyStr = "notify: " + String.valueOf(notify);
        String title = "test title";
        String content = dt + " " + autoStr + " " + notifyStr;
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setDefaults(getNotificationDefaults());
        builder.setAutoCancel(true);

        Notification notification = builder.build();
        manager.notify(TutuConstants.Constants.TUTU_TEST_NOTIFICATION_ID, notification);
    }

    protected int getNotificationDefaults() {
        return getNotificationDefaults(this.light, this.vibrate, this.sound);
    }

    protected int getNotificationDefaults(boolean light, boolean vibrate, boolean sound) {
        int defaults = 0;
        if (light) {
            defaults = defaults | NOTIFICATION_LIGHT;
        }
        if (vibrate) {
            defaults = defaults | NOTIFICATION_VIBRATE;
        }
        if (sound) {
            defaults = defaults | NOTIFICATION_SOUND;
        }
        return defaults;
    }

    public void setLight(boolean on) {
        light = on;
    }

    public void setVibrate(boolean on) {
        vibrate = on;
    }

    public void setSound(boolean on) {
        sound = on;
    }

    protected class OperationIntentCreator {

        PendingIntent createDeletion(String id) {
            NotificationOperation operation = NotificationOperation.Deletion;
            int requestCode = operation.ordinal();
            Intent intent = new Intent(context, ReservationOperationReceiver.class);
            intent.putExtra(operation.getKey(), id);
            intent.setAction(ReservationOperationService.class.getCanonicalName());
            return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        PendingIntent createModification(String id) {
            NotificationOperation operation = NotificationOperation.Modification;
            int requestCode = operation.ordinal();
            Intent intent = new Intent(context, ReservationOperationReceiver.class);
            intent.putExtra(operation.getKey(), id);
            return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        PendingIntent createPostpone(String id) {
            NotificationOperation operation = NotificationOperation.Postpone;
            int requestCode = operation.ordinal();
            Intent intent = new Intent(context, ReservationOperationReceiver.class);
            intent.putExtra(operation.getKey(), id);
            intent.setAction(ReservationOperationService.class.getCanonicalName());
            return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

    }
}
