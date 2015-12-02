package com.huhaoyu.tutu.entity;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.huhaoyu.tutu.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.StudentDetails;

/**
 * Reservation info wrapper
 * Created by coderhuhy on 15/11/30.
 */
public class ReservationInfoWrapper implements PersonalInfoHeader {

    private static final String LogTag = ReservationInfoWrapper.class.getCanonicalName();
    private static final int HEADER_COUNT = 1;

    protected List<ReservationRecordDecorator> list = new ArrayList<>();
    protected StudentDetails details;
    protected Calendar refreshTime;

    public ReservationInfoWrapper() {
        refreshTime = Calendar.getInstance();
    }

    public void refresh(List<ReservationRecord> records, StudentDetails details) {
        this.clear();
        for (ReservationRecord record : records) {
            try {
                ReservationRecordDecorator decorator = ReservationRecordDecorator.from(record);
                this.list.add(decorator);
            } catch (DateTimeUtilities.DateTimeException e) {
                Log.e(LogTag, e.getDetails(), e);
            }
        }
        this.details = details;
        this.refreshTime = Calendar.getInstance();
    }

    public void clear() {
        list.clear();
        details = null;
    }

    public ReservationRecordDecorator get(int position) {
        int realPos = position - HEADER_COUNT;
        if (realPos >= 0 && realPos < list.size()) {
            return list.get(realPos);
        }
        return null;
    }

    public int size() {
        return realSize() + HEADER_COUNT;
    }

    public int realSize() {
        return list.size();
    }

    @Override
    public String getName(Context context) {
        if (details == null || TextUtils.isEmpty(details.getName())) {
            return context.getString(R.string.tutu_info_tutu);
        }
        return details.getName();
    }

    @Override
    public String getStudentId(Context context) {
        if (details == null || TextUtils.isEmpty(details.getStudentId())) {
            return context.getString(R.string.tutu_info_default_id);
        }
        return details.getStudentId();
    }

    @Override
    public String getDepartment(Context context) {
        if (details == null || TextUtils.isEmpty(details.getDepartment())) {
            return context.getString(R.string.tutu_info_default_department);
        }
        return details.getDepartment();
    }

    @Override
    public String getPhone(Context context) {
        if (details == null || details.getPhone() == null) {
            return context.getString(R.string.tutu_info_default_phone);
        }
        if ("".equals(details.getPhone())) {
            return context.getString(R.string.tutu_info_no_phone);
        }
        return details.getPhone();
    }

    @Override
    public String getEmail(Context context) {
        if (details == null || details.getEmail() == null) {
            return context.getString(R.string.tutu_info_default_demail);
        }
        if ("".equals(details.getEmail())) {
            return context.getString(R.string.tutu_info_no_email);
        }
        return details.getEmail();
    }

    @Override
    public String getRefreshDateTime(String pattern) {
        return DateTimeUtilities.formatReservationDate(this.refreshTime, pattern);
    }

    @Override
    public boolean hasReservation() {
        return !list.isEmpty();
    }
}
