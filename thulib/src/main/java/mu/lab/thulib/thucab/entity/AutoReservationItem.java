package mu.lab.thulib.thucab.entity;

import java.util.Calendar;

import mu.lab.thulib.thucab.CabConstants;

/**
 * Auto reservation item
 * Created by coderhuhy on 15/11/18.
 */
public class AutoReservationItem {

    // Day of week figure by Calendar, such as Calendar.MONDAY
    private int dayOfWeek;
    private ReservationState.TimeRange range;

    public AutoReservationItem() {
    }

    public AutoReservationItem(ReservationState.TimeRange range, int dayOfWeek) {
        this.range = range;
        this.dayOfWeek = dayOfWeek;
    }

    public Calendar getDate() {
        for (int increment = 0; increment != CabConstants.DateTimeConstants.DAY_OF_WEEK; ++increment) {
            Calendar calendar = Calendar.getInstance();
            calendar.roll(Calendar.DAY_OF_YEAR, increment);
            if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                return calendar;
            }
        }
        return null;
    }

    public boolean shouldMakeReservation() {
        // cannot book today's reading room to avoid unexpected situations.
        for (int increment = 1; increment != CabConstants.ReservationConstants.LIMIT_RESERVATION_DAYS; ++increment) {
            Calendar calendar = Calendar.getInstance();
            calendar.roll(Calendar.DAY_OF_YEAR, increment);
            if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                return true;
            }
        }
        return false;
    }

    public int getIncrement() {
        for (int increment = 0; increment != CabConstants.DateTimeConstants.DAY_OF_WEEK; ++increment) {
            Calendar calendar = Calendar.getInstance();
            calendar.roll(Calendar.DAY_OF_YEAR, increment);
            if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                return increment;
            }
        }
        return 0;
    }

    public String getStart() {
        return range.getStart();
    }

    public String getEnd() {
        return range.getEnd();
    }

    public ReservationState.TimeRange getRange() {
        return range;
    }

    public static AutoReservationItem from(AutoResvRealmItem item) {
        AutoReservationItem ret = new AutoReservationItem();
        ret.dayOfWeek = item.getDayOfWeek();
        ret.range = new ReservationState.TimeRange(item.getStart(), item.getEnd());
        return ret;
    }

    public AutoResvRealmItem toRealm(StudentAccount account) {
        AutoResvRealmItem item = new AutoResvRealmItem();
        item.setId(account.getStudentId() + "-" + this.dayOfWeek);
        item.setDayOfWeek(this.dayOfWeek);
        item.setStart(this.getStart());
        item.setEnd(this.getEnd());
        item.setUsername(account.getStudentId());
        return item;
    }

}
