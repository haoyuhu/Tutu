package com.huhaoyu.tutu.entity;

import com.huhaoyu.tutu.R;

import java.util.Calendar;

import mu.lab.thulib.thucab.entity.AutoReservationItem;
import mu.lab.thulib.thucab.entity.ReservationState;

/**
 * Auto reservation item decorator
 * Created by coderhuhy on 15/12/10.
 */
public class AutoResvItemDecorator {

    private static final float DISABLE_ALPHA = 0.5f;
    private static final float ENABLE_ALPHA = 1.0f;

    private static final String DEFAULT_START = "8:00";
    private static final String DEFAULT_END = "12:00";

    private int dayOfWeek;
    private String tStart;
    private String tEnd;
    private boolean enable;

    private AutoResvItemDecorator() {

    }

    public static AutoResvItemDecorator from(AutoReservationItem item) {
        AutoResvItemDecorator decorator = new AutoResvItemDecorator();
        decorator.dayOfWeek = item.getDate().get(Calendar.DAY_OF_WEEK);
        decorator.tStart = item.getStart();
        decorator.tEnd = item.getEnd();
        decorator.enable = true;
        return decorator;
    }

    public static AutoResvItemDecorator newInstance(int dayOfWeek) {
        AutoResvItemDecorator decorator = new AutoResvItemDecorator();
        decorator.dayOfWeek = dayOfWeek;
        decorator.tStart = DEFAULT_START;
        decorator.tEnd = DEFAULT_END;
        decorator.enable = false;
        return decorator;
    }

    public void copy(AutoReservationItem item) {
        this.dayOfWeek = item.getDate().get(Calendar.DAY_OF_WEEK);
        this.tStart = item.getStart();
        this.tEnd = item.getEnd();
        this.enable = true;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public float getAlpha() {
        return enable ? ENABLE_ALPHA : DISABLE_ALPHA;
    }

    public int getImageId() {
        return enable ? R.mipmap.ic_done_white : R.mipmap.ic_clear_white;
    }

    public int getColorId() {
        return enable ? R.color.md_green_800 : R.color.md_red_800;
    }

    public String getStart() {
        return tStart;
    }

    public String getEnd() {
        return tEnd;
    }

    public void setStart(String start) {
        tStart = start;
    }

    public void settEnd(String end) {
        tEnd = end;
    }

    public boolean sameDay(AutoReservationItem item) {
        return this.dayOfWeek == item.getDate().get(Calendar.DAY_OF_WEEK);
    }

    public void clear() {
        this.tStart = DEFAULT_START;
        this.tEnd = DEFAULT_END;
        this.enable = false;
    }

    public AutoReservationItem toItem() {
        ReservationState.TimeRange range = new ReservationState.TimeRange(tStart, tEnd);
        return new AutoReservationItem(range, dayOfWeek);
    }

}
