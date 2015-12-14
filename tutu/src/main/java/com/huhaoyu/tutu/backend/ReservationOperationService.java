package com.huhaoyu.tutu.backend;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.huhaoyu.tutu.ui.ReservationListActivity;
import com.huhaoyu.tutu.utils.TutuConstants;

import java.util.List;

import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.PreferenceUtilities;
import mu.lab.thulib.thucab.ResvRecordStore;
import mu.lab.thulib.thucab.UserAccountManager;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.resvutils.CabCmdExecutorImpl;
import mu.lab.thulib.thucab.resvutils.CabCommand;
import mu.lab.thulib.thucab.resvutils.CabCommandCreator;
import mu.lab.thulib.thucab.resvutils.CabCommandExecutor;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * Reservation operation service
 * Created by coderhuhy on 15/12/13.
 */
public class ReservationOperationService extends IntentService implements Observer<List<ReservationRecord>> {

    private static final String LogTag = ReservationOperationService.class.getCanonicalName();

    private CabCommandExecutor executor;
    private StudentAccount account;
    private String deletionId;
    private String postponeId;

    public ReservationOperationService() {
        super(LogTag);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!TextUtils.isEmpty(intent.getStringExtra(
                TutuNotificationManager.NotificationOperation.Modification.getKey()))) {
            Intent i = new Intent(getBaseContext(), ReservationListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(i);
            ReservationOperationReceiver.completeWakefulIntent(intent);
            return;
        }
        executor = CabCmdExecutorImpl.getInstance();
        deletionId = intent.getStringExtra(TutuNotificationManager.NotificationOperation.Deletion.getKey());
        postponeId = intent.getStringExtra(TutuNotificationManager.NotificationOperation.Postpone.getKey());
        UserAccountManager manager = UserAccountManager.getInstance();
        if ((!TextUtils.isEmpty(deletionId) || !TextUtils.isEmpty(postponeId)) && manager.hasAccount()) {
            try {
                account = manager.getAccount();
                ResvRecordStore.getResvRecordsFromRealm(account)
                        .subscribeOn(Schedulers.io())
                        .subscribe(this);
            } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
                Log.e(LogTag, error.toString(), error);
            }
        }
        ReservationOperationReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LogTag, "reservation operation service on create...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LogTag, "reservation operation service on destroy...");
    }

    @Override
    public void onCompleted() {
        Log.i(LogTag, "get records from realm completed...");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(LogTag, e.getMessage(), e);
    }

    @Override
    public void onNext(List<ReservationRecord> records) {
        if (!TextUtils.isEmpty(deletionId) && account != null) {
            for (ReservationRecord record : records) {
                if (record.getReservationId().equals(deletionId)) {
                    CabCommand command = CabCommandCreator.createDeletionCommand(deletionId);
                    executor.execute(account, command);
                    break;
                }
            }
        } else if (!TextUtils.isEmpty(postponeId) && account != null) {
            for (ReservationRecord record : records) {
                if (record.getReservationId().equals(postponeId)) {
                    try {
                        CabCommand command = CabCommandCreator.createPostponeCommand(record,
                                TutuConstants.Constants.DEFAULT_POSTPONE_INTERVAL_IN_MINUTE);
                        executor.execute(account, command);
                    } catch (DateTimeUtilities.DateTimeException e) {
                        Log.e(LogTag, e.getDetails(), e);
                    }
                }
            }
        }
    }
}
