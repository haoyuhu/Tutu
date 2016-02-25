package com.huhaoyu.tutu.entity;

import android.content.Context;

import com.huhaoyu.tutu.R;

import java.util.Calendar;

import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.ReservationRecord;

/**
 * Reservation record Decorator
 * Created by coderhuhy on 15/11/30.
 */
public class ReservationRecordDecorator {

    protected ReservationRecord record;
    protected DateTimeUtilities.DayRound round;
    protected DateTimeUtilities.TimePeriod period;
    protected String start;
    protected String end;
    protected boolean hasStarted;

    private int[] colors = {
            R.color.tutu_orange_transparent,
            R.color.tutu_pink_transparent,
            R.color.tutu_blue_transparent,
            R.color.tutu_green_transparent
    };

    private ReservationRecordDecorator() {

    }

    public ReservationRecord getRecord() {
        return this.record;
    }

    public String getDayRound(Context context) {
        return context.getString(round.getResId());
    }

    public String getPeriod(Context context) {
        return context.getString(period.getResId());
    }

    public int getPeriodBackgroundColor() {
        return colors[period.ordinal()];
    }

    public int getPeriodImageRes() {
        return period.getImageId();
    }

    public String getRoomName() {
        return record.getRoomName();
    }

    public String getState(Context context) {
        return hasStarted ?
                context.getString(R.string.tutu_info_started) : context.getString(R.string.tutu_info_not_start);
    }

    public String getStart2End() {
        String connection = "~";
        return getStart2End(connection);
    }

    public String getStart2End(String connection) {
        return getStart() + connection + getEnd();
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }

    public boolean isHasStarted() {
        return this.hasStarted;
    }

    public static ReservationRecordDecorator from(ReservationRecord record)
            throws DateTimeUtilities.DateTimeException {
        final String pattern = "HH:mm";
        ReservationRecordDecorator decorator = new ReservationRecordDecorator();
        decorator.record = record;
        Calendar dtStart = record.getStartDateTime();
        Calendar dtEnd = record.getEndDateTime();
        Calendar current = Calendar.getInstance();
        decorator.hasStarted = DateTimeUtilities.calculateInterval(dtStart, current) <= 0;
        decorator.start = DateTimeUtilities.formatReservationDate(dtStart, pattern);
        decorator.end = DateTimeUtilities.formatReservationDate(dtEnd, pattern);
        decorator.round = DateTimeUtilities.DayRound.from(dtStart);
        decorator.period = DateTimeUtilities.TimePeriod.from(dtStart);
        return decorator;
    }

}
