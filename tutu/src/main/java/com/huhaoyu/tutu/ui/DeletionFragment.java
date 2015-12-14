package com.huhaoyu.tutu.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.ReservationRecordDecorator;
import com.huhaoyu.tutu.utils.RefresherManager;
import com.huhaoyu.tutu.utils.SnackbarManager;
import com.huhaoyu.tutu.utils.TutuConstants;
import com.huhaoyu.tutu.widget.ReservationObserver;
import com.rey.material.widget.ProgressView;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.PreferenceUtilities;
import mu.lab.thulib.thucab.UserAccountManager;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.resvutils.CabCmdExecutorImpl;
import mu.lab.thulib.thucab.resvutils.CabCommand;
import mu.lab.thulib.thucab.resvutils.CabCommandCreator;
import mu.lab.thulib.thucab.resvutils.CabCommandExecutor;
import mu.lab.thulib.thucab.resvutils.ExecutorResultObserver;
import mu.lab.thulib.thucab.resvutils.ReservationLoginCallback;

/**
 * Deletion fragment
 * Created by coderhuhy on 15/12/4.
 */
public class DeletionFragment extends LeakCanaryBottomFragment
        implements View.OnClickListener, ReservationLoginCallback {

    private static final String LogTag = DeletionFragment.class.getCanonicalName();

    @Bind(R.id.quit_button)
    Button quitButton;
    @Bind(R.id.confirm_button)
    Button confirmButton;
    @Bind(R.id.title_tv)
    TextView titleTv;
    @Bind(R.id.refresh_progress)
    ProgressView refreshProgress;

    private ReservationRecordDecorator record;
    private ReservationObserver observer;
    private RefresherManager refresherManager;
    private Handler handler;
    private UserAccountManager accountManager;
    private CabCommandExecutor executor;

    private ExecutorResultObserver callback = new ExecutorResultObserver() {
        @Override
        public void onConflict() {
            refresherManager.stop();
            if (confirmButton != null) {
                confirmButton.setEnabled(true);
            }
            SnackbarManager snackbar = new SnackbarManager(titleTv);
            snackbar.setContent(R.string.tutu_reservation_conflict).show();
        }

        @Override
        public void onNetworkFailure() {
            refresherManager.stop();
            if (confirmButton != null) {
                confirmButton.setEnabled(true);
            }
            SnackbarManager snackbar = new SnackbarManager(titleTv);
            snackbar.setContent(R.string.tutu_reservation_network_failure).show();
            observer.onNetworkError();
        }

        @Override
        public void onSuccess() {
            refresherManager.stop();
            if (confirmButton != null) {
                confirmButton.setEnabled(true);
                confirmButton.setText(getContext().getString(R.string.tutu_reservation_complete));
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                    observer.onReservationSuccess();
                }
            }, TutuConstants.Constants.DELAY_DURATION);
        }
    };

    public static DeletionFragment newInstance(ReservationRecordDecorator record, ReservationObserver observer) {
        DeletionFragment fragment = new DeletionFragment();
        fragment.record = record;
        fragment.observer = observer;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deletion, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String room = record.getRoomName();
        String title = getContext().getString(R.string.tutu_deletion) + " " + room;

        titleTv.setText(title);
        quitButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);

        refresherManager = RefresherManager.newInstance(refreshProgress);
        accountManager = UserAccountManager.getInstance();
        executor = CabCmdExecutorImpl.getInstance();
        executor.registerCallback(this);
        handler = new Handler();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        executor.unregisterCallback(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quit_button:
                dismiss();
                break;
            case R.id.confirm_button:
                sendDeletion();
                break;
        }
    }

    private void sendDeletion() {
        refresherManager.start();
        confirmButton.setEnabled(false);
        try {
            CabCommand command = CabCommandCreator
                    .createDeletionCommand(record.getRecord().getReservationId(), callback);
            if (accountManager.hasAccount()) {
                StudentAccount account = accountManager.getAccount();
                executor.execute(account, command);
            } else {
                SnackbarManager snackbar = new SnackbarManager(titleTv);
                snackbar.setContent(R.string.tutu_reservation_account_failure).show();
                confirmButton.setEnabled(false);
                observer.onAccountError();
                dismiss();
            }
        } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
            Log.e(LogTag, error.getMessage(), error);
            SnackbarManager snackbar = new SnackbarManager(titleTv);
            snackbar.setContent(R.string.tutu_reservation_account_failure).show();
            confirmButton.setEnabled(false);
            observer.onAccountError();
            dismiss();
        }
    }

    @Override
    public void onActivationError() {
        refresherManager.stop();
        SnackbarManager snackbar = new SnackbarManager(titleTv);
        snackbar.setContent(R.string.tutu_reservation_account_failure).show();
        if (confirmButton != null) {
            confirmButton.setEnabled(false);
        }
        observer.onAccountError();
        dismiss();
    }

    @Override
    public void onAccountError() {
        refresherManager.stop();
        SnackbarManager snackbar = new SnackbarManager(titleTv);
        snackbar.setContent(R.string.tutu_reservation_account_failure).show();
        if (confirmButton != null) {
            confirmButton.setEnabled(false);
        }
        observer.onAccountError();
        dismiss();
    }

    @Override
    public void onNetworkError() {
        callback.onNetworkFailure();
    }

    @Override
    public void onLocalError() {
        refresherManager.stop();
    }

}
