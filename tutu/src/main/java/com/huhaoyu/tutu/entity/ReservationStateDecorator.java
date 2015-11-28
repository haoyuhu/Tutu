package com.huhaoyu.tutu.entity;

import android.content.Context;

import com.huhaoyu.tutu.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.AbstractState;
import mu.lab.thulib.thucab.entity.ReservationState;
import mu.lab.thulib.thucab.entity.RoomLabKind;

/**
 * Reservation state decorator
 * Created by coderhuhy on 15/11/24.
 */
public class ReservationStateDecorator implements AbstractState {

    protected static class CountInterval {
        int count = 0;
        long interval = 0;

        public void add(int num) {
            count += num;
        }

        public void merge(long interval) {
            this.interval = Math.max(this.interval, interval);
        }

        void count(long interval) {
            ++count;
            this.interval = Math.max(this.interval, interval);
        }
    }

    protected HsLibFloor floor;
    protected AbstractState state;
    protected List<DateTimeUtilities.TimePeriod> periodTags;
    protected long interval = 0;
    protected CountInterval morning = new CountInterval();
    protected CountInterval afternoon = new CountInterval();
    protected CountInterval evening = new CountInterval();
    protected CountInterval allday = new CountInterval();

    public static ReservationStateDecorator from(AbstractState state) {
        ReservationStateDecorator decorator = new ReservationStateDecorator();
        decorator.state = state;
        switch (state.getKind()) {
            case HumanitiesLibSecFloorSingle:
                decorator.floor = HsLibFloor.Second;
                break;
            case HumanitiesLibThirdFloorSingle:
                decorator.floor = HsLibFloor.Third;
                break;
            default:
                decorator.floor = HsLibFloor.Second;
        }
        Set<DateTimeUtilities.TimePeriod> set = new HashSet<>();
        for (ReservationState.TimeRange range : state.getAvailableTimeRanges()) {
            long i = range.getIntervalInMillis();
            DateTimeUtilities.TimePeriod period = range.getTimePeriod();
            if (!set.contains(period)) {
                set.add(period);
            }
            switch (period) {
                case Morning:
                    decorator.morning.count(i);
                    break;
                case Afternoon:
                    decorator.afternoon.count(i);
                    break;
                case Night:
                    decorator.evening.count(i);
                    break;
                case AllDay:
                    decorator.allday.count(i);
                    break;
            }
        }
        List<Long> t = Arrays.asList(decorator.morning.interval, decorator.afternoon.interval,
                decorator.evening.interval, decorator.allday.interval);
        Collections.sort(t);
        decorator.interval = t.get(t.size() - 1);
        final int MAX_SIZE = 3;
        decorator.periodTags = new ArrayList<>();
        if (set.size() >= MAX_SIZE) {
            decorator.periodTags.add(DateTimeUtilities.TimePeriod.AllDay);
        } else {
            decorator.periodTags.addAll(set);
        }
        return decorator;
    }

    public List<String> getTimePeriodTags(Context context) {
        List<String> ret = new ArrayList<>();
        for (DateTimeUtilities.TimePeriod period : periodTags) {
            ret.add(context.getString(period.getResId()));
        }
        return ret;
    }

    /**
     * @return return max interval as max
     */
    public String getMaxInterval(Context context) {
        int t = (int) (interval / CabConstants.DateTimeConstants.MILLIS_OF_SECOND / CabConstants.DateTimeConstants.SECOND_OF_MINUTE);
        int minutes = t % CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
        int hours = t / CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
        return minutes != 0 ? String.format(context.getString(R.string.tutu_max_interval_for_reservation), hours, minutes)
                : String.format(context.getString(R.string.tutu_max_interval_for_reservation_without_mins), hours);
    }

    HsLibFloor getFloor() {
        return this.floor;
    }

    int getPeriodCount(DateTimeUtilities.TimePeriod period) {
        switch (period) {
            case Morning:
                return morning.count;
            case Afternoon:
                return afternoon.count;
            case Night:
                return evening.count;
            case AllDay:
                return allday.count;
        }
        return 0;
    }

    int getTotalCount() {
        return morning.count + afternoon.count + evening.count + allday.count;
    }

    long getPeriodInterval(DateTimeUtilities.TimePeriod period) {
        switch (period) {
            case Morning:
                return morning.interval;
            case Afternoon:
                return afternoon.interval;
            case Night:
                return evening.interval;
            case AllDay:
                return allday.interval;
        }
        return 0l;
    }

    public long getTotalInterval() {
        return this.interval;
    }

    @Override
    public String getRoomName() {
        return state.getRoomName();
    }

    @Override
    public String getDevId() {
        return state.getDevId();
    }

    @Override
    public RoomLabKind getKind() {
        return state.getKind();
    }

    @Override
    public int getFloorStringId() {
        return state.getFloorStringId();
    }

    @Override
    public List<ReservationState.TimeRange> getAvailableTimeRanges() {
        return state.getAvailableTimeRanges();
    }

    @Override
    public ReservationState.TimeRange getRange() {
        return state.getRange();
    }
}
