package com.huhaoyu.tutu.backend;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import mu.lab.thulib.thucab.PreferenceUtilities;
import mu.lab.thulib.thucab.UserAccountManager;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.resvutils.CabCmdExecutorImpl;
import mu.lab.thulib.thucab.resvutils.CabCommandExecutor;

/**
 * Tutu alarm service
 * Created by coderhuhy on 15/12/12.
 */
public class TutuAlarmService extends IntentService {

    private static final String LogTag = TutuAlarmService.class.getCanonicalName();

    public TutuAlarmService() {
        super(LogTag);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LogTag, "Alarm service on handling intent...");
        CabCommandExecutor executor = CabCmdExecutorImpl.getInstance();
        TutuNotificationManager notificationManager = TutuNotificationManager.getInstance();
        UserAccountManager accountManager = UserAccountManager.getInstance();
        if (accountManager.hasAccount()) {
            try {
                StudentAccount account = accountManager.getAccount();
                executor.refresh(account);
                notificationManager.check(account);
            } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
                Log.e(LogTag, error.toString(), error);
            }
        }
        TutuAlarmReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LogTag, "Alarm service on create...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LogTag, "Alarm service on destroy...");
    }
}
