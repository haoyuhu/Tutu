package com.huhaoyu.tutu.backend;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.huhaoyu.tutu.R;
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
import mu.lab.thulib.thucab.resvutils.ExecutorResultObserver;
import mu.lab.thulib.thucab.resvutils.ReservationLoginCallback;
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
    private ExecutorResultObserver observer = new ExecutorResultObserver() {
        @Override
        public void onConflict() {
            onOperationError();
        }

        @Override
        public void onNetworkFailure() {
            onOperationError();
        }

        @Override
        public void onSuccess() {
            onOperationSuccess();
        }
    };
    private ReservationLoginCallback callback = new ReservationLoginCallback() {
        @Override
        public void onActivationError() {
            onOperationError();
        }

        @Override
        public void onAccountError() {
            onOperationError();
        }

        @Override
        public void onNetworkError() {
            onOperationError();
        }

        @Override
        public void onLocalError() {
            onOperationError();
        }
    };

    protected void onOperationError() {
        String title = getBaseContext().getString(R.string.tutu_notification_result_failure_title);
        String content = getBaseContext().getString(R.string.tutu_notification_result_failure_content);
        TutuNotificationManager manager = TutuNotificationManager.getInstance();
        manager.notifyResult(title, content, true);
    }

    protected void onOperationSuccess() {
        String title = getBaseContext().getString(R.string.tutu_notification_result_success_title);
        String content = getBaseContext().getString(R.string.tutu_notification_result_success_content);
        TutuNotificationManager manager = TutuNotificationManager.getInstance();
        manager.notifyResult(title, content, false);
    }

    public ReservationOperationService() {
        super(LogTag);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!TextUtils.isEmpty(intent.getStringExtra(
                TutuNotificationManager.NotificationOperation.Modification.getKey()))) {
            Intent i = new Intent(getBaseContext(), ReservationListActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                executor.registerCallback(callback);
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
        executor.unregisterCallback(callback);
    }

    @Override
    public void onNext(List<ReservationRecord> records) {
        if (!TextUtils.isEmpty(deletionId) && account != null) {
            for (ReservationRecord record : records) {
                if (record.getReservationId().equals(deletionId)) {
                    CabCommand command = CabCommandCreator.createDeletionCommand(deletionId, observer);
                    executor.execute(account, command);
                    break;
                }
            }
        } else if (!TextUtils.isEmpty(postponeId) && account != null) {
            for (ReservationRecord record : records) {
                if (record.getReservationId().equals(postponeId)) {
                    try {
                        CabCommand command = CabCommandCreator.createPostponeCommand(record,
                                TutuConstants.Constants.DEFAULT_POSTPONE_INTERVAL_IN_MINUTE, observer);
                        executor.execute(account, command);
                    } catch (DateTimeUtilities.DateTimeException e) {
                        Log.e(LogTag, e.getDetails(), e);
                    }
                }
            }
        }

        if (account != null) {
            executor.refresh(account);
        }
        executor.unregisterCallback(callback);
    }
}
