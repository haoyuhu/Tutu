package com.huhaoyu.tutu.utils;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.ui.ReservationListActivity;

import java.util.Calendar;
import java.util.List;

import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.PreferenceUtilities;
import mu.lab.thulib.thucab.ResvRecordStore;
import mu.lab.thulib.thucab.UserAccountManager;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;
import rx.Observer;

/**
 * Drawer manager implement
 * Created by coderhuhy on 15/11/27.
 */
public class DrawerManagerImpl extends DrawerManager
        implements Observer<List<ReservationRecord>>, View.OnClickListener {

    private static final String LogTag = DrawerManagerImpl.class.getCanonicalName();

    //drawer header
    TextView usernameTv;
    TextView studentIdTv;
    TextView departmentTv;
    LinearLayout drawerHeaderTags;
    View clickableView;
    //drawer menu
    MenuItem smartReservation;
    MenuItem autoReservation;
    MenuItem reservationInfo;
    MenuItem logout;
    MenuItem switchAccount;

    ReservationListActivity context;
    UserAccountManager manager;

    public DrawerManagerImpl(StudentAccount account, NavigationView navigation, ReservationListActivity context) {
        super(account, navigation);
        this.context = context;
        this.manager = UserAccountManager.getInstance();

        Menu menu = navigation.getMenu();
        smartReservation = menu.findItem(R.id.drawer_smart_reservation);
        autoReservation = menu.findItem(R.id.drawer_auto_reservation);
        reservationInfo = menu.findItem(R.id.drawer_reservation_info);
        logout = menu.findItem(R.id.drawer_logout);
        switchAccount = menu.findItem(R.id.drawer_switch_account);

        View header = navigation.inflateHeaderView(R.layout.drawer_header);
        usernameTv = (TextView) header.findViewById(R.id.username_tv);
        studentIdTv = (TextView) header.findViewById(R.id.student_id_tv);
        departmentTv = (TextView) header.findViewById(R.id.department_tv);
        clickableView = header.findViewById(R.id.clickable_view);
        drawerHeaderTags = (LinearLayout) header.findViewById(R.id.drawer_header_tags_container);

        clickableView.setOnClickListener(this);
    }

    protected void showRecord() {
        ResvRecordStore.getResvRecords(account, false, this);
    }

    protected void showStudentInfo() throws PreferenceUtilities.StudentAccountNotFoundError {
        String studentId = account.getStudentId();
        StudentDetails details = manager.getDetails();
        String name = details.getName();
        String department = details.getDepartment();

        usernameTv.setText(name);
        studentIdTv.setText(studentId);
        departmentTv.setVisibility(View.VISIBLE);
        departmentTv.setText(department);
    }

    @Override
    public void onLogin(StudentAccount account) {
        this.account = account;
        try {
            showStudentInfo();
            showRecord();

            smartReservation.setEnabled(true);
            autoReservation.setEnabled(true);
            reservationInfo.setEnabled(true);
            logout.setEnabled(true);
            switchAccount.setEnabled(true);
        } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
            Log.e(LogTag, error.toString(), error);
            onClearUp();
        }
    }

    @Override
    public void onInit() {
        if (account != null) {
            onLogin(account);
        } else {
            onClearUp();
        }
    }

    @Override
    public void onClearUp() {
        usernameTv.setText(context.getString(R.string.tutu_drawer_tsinghua_tu));
        studentIdTv.setText(context.getString(R.string.tutu_drawer_click_to_login));
        departmentTv.setVisibility(View.GONE);
        drawerHeaderTags.removeAllViews();

        smartReservation.setEnabled(false);
        autoReservation.setEnabled(false);
        reservationInfo.setEnabled(false);
        logout.setEnabled(false);
        switchAccount.setEnabled(false);

        context.refreshFab();
    }

    @Override
    public void onResume() {
        showRecord();
    }

    @Override
    public void onCompleted() {
        Log.i(LogTag, "get records completed...");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(LogTag, e.getMessage(), e);
    }

    @Override
    public void onNext(List<ReservationRecord> records) {
        int total = records.size();
        int today = 0;
        for (ReservationRecord record : records) {
            try {
                Calendar cal = record.getDate();
                Calendar curr = Calendar.getInstance();
                if (cal.get(Calendar.YEAR) == curr.get(Calendar.YEAR)
                        && cal.get(Calendar.MONTH) == curr.get(Calendar.MONTH)
                        && cal.get(Calendar.DAY_OF_MONTH) == curr.get(Calendar.DAY_OF_MONTH)) {
                    today = 1;
                }
            } catch (DateTimeUtilities.DateTimeException e) {
                Log.e(LogTag, e.getDetails(), e);
            }
        }
        TextView totalTv = (TextView) LayoutInflater.from(context).inflate(R.layout.drawer_header_tag, drawerHeaderTags, false);
        TextView todayTv = (TextView) LayoutInflater.from(context).inflate(R.layout.drawer_header_tag, drawerHeaderTags, false);
        totalTv.setText(String.format(context.getString(R.string.tutu_drawer_total_reservation_count), total));
        todayTv.setText(String.format(context.getString(R.string.tutu_drawer_today_reservation_count), today));
        drawerHeaderTags.removeAllViews();
        drawerHeaderTags.addView(totalTv);
        drawerHeaderTags.addView(todayTv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clickable_view:
                context.openLoginFragment();
        }
    }
}
