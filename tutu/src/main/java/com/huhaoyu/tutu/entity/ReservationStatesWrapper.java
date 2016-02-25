package com.huhaoyu.tutu.entity;

import android.content.Context;

import com.huhaoyu.tutu.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.ReservationState;

/**
 * Reservation states wrapper
 * Created by coderhuhy on 15/11/24.
 */
public class ReservationStatesWrapper implements ReservationSummary {

    protected static final int HEADER_COUNT = 1;

    protected Calendar refreshDateTime;
    protected final DateTimeUtilities.DayRound round;
    protected List<ReservationStateDecorator> list;
    protected List<ReservationStateDecorator.CountInterval> secfloor;
    protected List<ReservationStateDecorator.CountInterval> thrfloor;
    protected int[] counts = {0, 0};

    public ReservationStatesWrapper(DateTimeUtilities.DayRound round) {
        final int PERIOD_COUNT = 4;
        this.round = round;
        list = new ArrayList<>();
        secfloor = new ArrayList<>();
        thrfloor = new ArrayList<>();
        for (int i = 0; i != PERIOD_COUNT; ++i) {
            secfloor.add(new ReservationStateDecorator.CountInterval());
            thrfloor.add(new ReservationStateDecorator.CountInterval());
        }
        refreshDateTime = Calendar.getInstance();
    }

    protected void clear() {
        list.clear();
        secfloor.clear();
        thrfloor.clear();
        counts[0] = counts[1] = 0;
    }

    public void refresh(List<ReservationState> states) {
        this.clear();
        DateTimeUtilities.TimePeriod[] periods = DateTimeUtilities.TimePeriod.values();
        for (int i = 0; i != periods.length; ++i) {
            secfloor.add(new ReservationStateDecorator.CountInterval());
            thrfloor.add(new ReservationStateDecorator.CountInterval());
        }
        for (ReservationState state : states) {
            if (!state.hasAvailableRooms()) {
                continue;
            }
            ReservationStateDecorator decorator = ReservationStateDecorator.from(state);
            list.add(decorator);

            for (int i = 0; i != periods.length; ++i) {
                int tCount = decorator.getPeriodCount(periods[i]);
                long tInterval = decorator.getPeriodInterval(periods[i]);
                switch (decorator.getFloor()) {
                    case Second:
                        secfloor.get(i).add(tCount);
                        secfloor.get(i).merge(tInterval);
                        break;
                    case Third:
                        thrfloor.get(i).add(tCount);
                        thrfloor.get(i).merge(tInterval);
                        break;
                }
            }
        }
        for (int i = 0; i != periods.length; ++i) {
            counts[0] += secfloor.get(i).count;
            counts[1] += thrfloor.get(i).count;
        }
        refreshDateTime = Calendar.getInstance();
    }

    // TODO: 15/11/25 filter interval and time period

    public ReservationStateDecorator get(int position) {
        int realPos = position - HEADER_COUNT;
        if (realPos >= 0 && realPos < realSize()) {
            return list.get(realPos);
        }
        return null;
    }

    public int size() {
        return this.realSize() + HEADER_COUNT;
    }

    public int realSize() {
        return list.size();
    }

    @Override
    public String getRefreshDateTime(String pattern) {
        return DateTimeUtilities.formatReservationDate(this.refreshDateTime, pattern);
    }

    @Override
    public String getDate(Context context) {
        String pattern = context.getString(R.string.tutu_date_pattern);
        return getDate(pattern);
    }

    @Override
    public String getDate(String pattern) {
        return DateTimeUtilities.formatReservationDate(round, pattern);
    }

    @Override
    public int getTotalAvailableTimeCount(HsLibFloor floor) {
        return counts[floor.ordinal()];
    }

    @Override
    public int getAvailableTimeCountInPeriod(HsLibFloor floor, DateTimeUtilities.TimePeriod period) {
        switch (floor) {
            case Second:
                return secfloor.get(period.ordinal()).count;
            case Third:
                return thrfloor.get(period.ordinal()).count;
        }
        return 0;
    }

    @Override
    public long getAvailableTimeIntervalInPeriod(HsLibFloor floor, DateTimeUtilities.TimePeriod period) {
        switch (floor) {
            case Second:
                return secfloor.get(period.ordinal()).interval;
            case Third:
                return thrfloor.get(period.ordinal()).interval;
        }
        return 0l;
    }

    @Override
    public String getTotalAvailableTimeCount(HsLibFloor floor, String pattern) {
        int count = getTotalAvailableTimeCount(floor);
        return String.format(pattern, count);
    }

    @Override
    public String getAvailableTimeCountInPeriod(HsLibFloor floor, DateTimeUtilities.TimePeriod period, String pattern) {
        int count = getAvailableTimeCountInPeriod(floor, period);
        return String.format(pattern, count);
    }

    @Override
    public String getAvailableTimeIntervalInPeriod(HsLibFloor floor, DateTimeUtilities.TimePeriod period,
                                                   String pattern, String patternWithoutMins) {
        long interval = getAvailableTimeIntervalInPeriod(floor, period);
        int t = (int) (interval / CabConstants.DateTimeConstants.MILLIS_OF_SECOND
                / CabConstants.DateTimeConstants.SECOND_OF_MINUTE);
        int minutes = t % CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
        int hours = t / CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
        return minutes != 0 ? String.format(pattern, hours, minutes) : String.format(patternWithoutMins, hours);
    }

    @Override
    public List<String> getRecentReservation() {
        return null;
    }

    @Override
    public boolean isValid() {
        return !(counts[0] == 0 && counts[1] == 0);
    }
}
