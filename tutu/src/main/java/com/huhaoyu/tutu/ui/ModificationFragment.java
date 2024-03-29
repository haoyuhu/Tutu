package com.huhaoyu.tutu.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.PreferenceUtilities;
import mu.lab.thulib.thucab.UserAccountManager;
import mu.lab.thulib.thucab.entity.ReservationState;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.resvutils.CabCmdExecutorImpl;
import mu.lab.thulib.thucab.resvutils.CabCommand;
import mu.lab.thulib.thucab.resvutils.CabCommandCreator;
import mu.lab.thulib.thucab.resvutils.CabCommandExecutor;
import mu.lab.thulib.thucab.resvutils.ExecutorResultObserver;
import mu.lab.thulib.thucab.resvutils.ReservationLoginCallback;

/**
 * Modification fragment
 * Created by coderhuhy on 15/12/3.
 */
public class ModificationFragment extends LeakCanaryBottomFragment
        implements View.OnClickListener, ReservationLoginCallback {

    private static final String LogTag = ModificationFragment.class.getCanonicalName();

    @Bind(R.id.start_time_tv)
    TextView startTimeTv;
    @Bind(R.id.end_time_tv)
    TextView endTimeTv;
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

    public static ModificationFragment newInstance(ReservationRecordDecorator record, ReservationObserver observer) {
        ModificationFragment fragment = new ModificationFragment();
        fragment.record = record;
        fragment.observer = observer;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String room = record.getRoomName();
        String title = getContext().getString(R.string.tutu_modification) + " " + room;
        String start = record.getStart();
        String end = record.getEnd();

        titleTv.setText(title);
        startTimeTv.setText(start);
        endTimeTv.setText(end);

        quitButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        startTimeTv.setOnClickListener(this);
        endTimeTv.setOnClickListener(this);

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
                sendReservation();
                break;
            case R.id.start_time_tv:
                getTime(true);
                break;
            case R.id.end_time_tv:
                getTime(false);
                break;
        }
    }

    private void sendReservation() {
        String start = startTimeTv.getText().toString();
        String end = endTimeTv.getText().toString();
        if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)) {
            refresherManager.start();
            confirmButton.setEnabled(false);
            try {
                CabCommand command = CabCommandCreator.createModificationCommand(
                        record.getRecord(), new ReservationState.TimeRange(start, end), callback);
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
            } catch (CabCommand.CabCommandException e) {
                Log.e(LogTag, e.getDetails(), e);
                SnackbarManager snackbar = new SnackbarManager(titleTv);
                snackbar.setContent(R.string.tutu_reservation_unknown_failure).show();
                confirmButton.setEnabled(false);
            } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
                Log.e(LogTag, error.getMessage(), error);
                SnackbarManager snackbar = new SnackbarManager(titleTv);
                snackbar.setContent(R.string.tutu_reservation_account_failure).show();
                confirmButton.setEnabled(false);
                observer.onAccountError();
                dismiss();
            }
        }
    }

    private void getTime(final boolean startOrEnd) {
        Timepoint[] points = null;
        try {
            points = startOrEnd ? getOptionalStart() : getOptionalEnd();
        } catch (DateTimeUtilities.DateTimeException e) {
            Log.e(LogTag, e.getDetails(), e);
        }
        int sh = 8, sm = 0;
        if (points != null && points.length != 0) {
            int index = startOrEnd ? 0 : points.length - 1;
            sh = points[index].getHour();
            sm = points[index].getMinute();
        }
        TimePickerDialog dialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                final String pattern = "HH:mm";
                if (startOrEnd) {
                    Calendar startCal = Calendar.getInstance();
                    startCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startCal.set(Calendar.MINUTE, minute);
                    String start = DateTimeUtilities.formatReservationDate(startCal, pattern);
                    startTimeTv.setText(start);

                    String end = CabConstants.ReservationConstants.END_TIME;
                    try {
                        end = DateTimeUtilities.getMaxOptionalEnd(start, end);
                    } catch (DateTimeUtilities.DateTimeException e) {
                        Log.e(LogTag, e.getDetails(), e);
                    }
                    endTimeTv.setText(end);
                } else {
                    Calendar endCal = Calendar.getInstance();
                    endCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endCal.set(Calendar.MINUTE, minute);
                    String end = DateTimeUtilities.formatReservationDate(endCal, pattern);
                    endTimeTv.setText(end);
                }
            }
        }, sh, sm, false);
        if (points != null) {
            dialog.setSelectableTimes(points);
        }
        dialog.show(getActivity().getFragmentManager(), LogTag);
    }

    private Timepoint[] getOptionalStart() throws DateTimeUtilities.DateTimeException {
        List<Timepoint> list = new ArrayList<>();
        Timepoint start = getTimePoint(CabConstants.ReservationConstants.START_TIME);
        Timepoint end = getTimePoint(CabConstants.ReservationConstants.END_TIME);
        int hour = start.getHour();
        int minute = start.getMinute();
        int endh = end.getHour();
        int endm = end.getMinute() - CabConstants.ReservationConstants.MIN_RESERVATION_MINUTES;
        if (endm < 0) {
            --endh;
            endm += CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
        }
        while (toMinute(hour, minute) <= toMinute(endh, endm)) {
            list.add(new Timepoint(hour, minute));
            minute += CabConstants.ReservationConstants.MINUTE_OF_RESERVATION_INTERVAL;
            if (minute >= CabConstants.DateTimeConstants.MINUTE_OF_HOUR) {
                ++hour;
                minute -= CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
            }
        }
        return list.toArray(new Timepoint[list.size()]);
    }

    private Timepoint[] getOptionalEnd() throws DateTimeUtilities.DateTimeException {
        List<Timepoint> list = new ArrayList<>();
        String start = startTimeTv.getText().toString();
        String end = endTimeTv.getText().toString();
        Timepoint s = getTimePoint(start);
        Timepoint e = getTimePoint(end);
        int hour = s.getHour();
        int minute = s.getMinute() + CabConstants.ReservationConstants.MIN_RESERVATION_MINUTES;
        int endh = e.getHour();
        int endm = e.getMinute();
        if (minute >= CabConstants.DateTimeConstants.MINUTE_OF_HOUR) {
            ++hour;
            minute -= CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
        }
        while (toMinute(hour, minute) <= toMinute(endh, endm)) {
            list.add(new Timepoint(hour, minute));
            minute += CabConstants.ReservationConstants.MINUTE_OF_RESERVATION_INTERVAL;
            if (minute >= CabConstants.DateTimeConstants.MINUTE_OF_HOUR) {
                ++hour;
                minute -= CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
            }
        }
        return list.toArray(new Timepoint[list.size()]);
    }

    private Timepoint getTimePoint(String time) {
        String[] hm = time.split(":");
        return new Timepoint(Integer.parseInt(hm[0]), Integer.parseInt(hm[1]));
    }

    private int toMinute(int hours, int minutes) {
        return hours * CabConstants.DateTimeConstants.MINUTE_OF_HOUR + minutes;
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
