package mu.lab.thulib.thucab.entity;

import java.util.ArrayList;
import java.util.List;

import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Cab filter
 * Created by coderhuhy on 15/11/26.
 */
public class CabFilter {

    public static final int DefaultMinInterval = 1;
    List<DateTimeUtilities.TimePeriod> periods = new ArrayList<>();
    int intervalInHour = 0;

    public CabFilter(List<DateTimeUtilities.TimePeriod> periods, int interval) {
        this.change(periods, interval);
    }

    public List<DateTimeUtilities.TimePeriod> getPeriods() {
        return periods;
    }

    public int getIntervalInHour() {
        return intervalInHour;
    }

    public void refresh(List<DateTimeUtilities.TimePeriod> list, int interval) {
        this.clear();
        this.change(list, interval);
    }

    protected void clear() {
        periods.clear();
        intervalInHour = DefaultMinInterval;
    }

    protected void change(List<DateTimeUtilities.TimePeriod> list, int interval) {
        if (list == null || list.isEmpty()) {
            for (DateTimeUtilities.TimePeriod period : DateTimeUtilities.TimePeriod.values()) {
                this.periods.add(period);
            }
        } else {
            this.periods.addAll(list);
        }
        this.intervalInHour = interval;
    }

}
