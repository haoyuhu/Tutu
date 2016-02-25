package mu.lab.thulib.thucab.entity;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mu.lab.thulib.R;
import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.util.Log;

/**
 * Reservation state entity
 * Created by coderhuhy on 15/11/12.
 */
public class ReservationState implements AbstractState {

    public static final String LogTag = ReservationState.class.getSimpleName();
    public static final int DEFAULT_MIN_INTERVAL = 1;

    private String roomName;
    private String devId;
    private RoomLabKind kind;
    private TimeRange range;
    private List<TimeRange> availableTimeRanges;

    public static class TimeRange {
        String start;
        String end;

        public TimeRange(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getFormatInterval(Context context) {
            int t = (int) (getIntervalInMillis() / CabConstants.DateTimeConstants.MILLIS_OF_SECOND
                    / CabConstants.DateTimeConstants.SECOND_OF_MINUTE);
            int minutes = t % CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
            int hours = t / CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
            return minutes != 0 ? String.format(context.getString(R.string.thucab_interval_for_reservation), hours, minutes)
                    : String.format(context.getString(R.string.thucab_interval_for_reservation_without_mins), hours);
        }

        public long getIntervalInMillis() {
            long interval = -1;
            try {
                interval = DateTimeUtilities.calculateAbsInterval(start, end);
            } catch (DateTimeUtilities.DateTimeException e) {
                Log.e(LogTag, e.getDetails(), e);
            }
            return interval;
        }

        /**
         * @return if the time start time in any period, return it.
         */
        public DateTimeUtilities.TimePeriod getTimePeriod() {
            if (this.start.equals(DateTimeUtilities.TimePeriod.AllDay.getStart())
                && this.end.equals(DateTimeUtilities.TimePeriod.AllDay.getEnd())) {
                return DateTimeUtilities.TimePeriod.AllDay;
            } else {
                for (DateTimeUtilities.TimePeriod period : DateTimeUtilities.TimePeriod.values()) {
                    try {
                        if (period.inPeriod(this.start)) {
                            return period;
                        }
                    } catch (DateTimeUtilities.DateTimeException e) {
                        Log.e(LogTag, e.getDetails(), e);
                    }
                }
                return DateTimeUtilities.TimePeriod.AllDay;
            }
        }

        /**
         * @return The string show in ui
         */
        public int getPeriodString() {
            return getTimePeriod().getResId();
        }

        /**
         * @return The image show in ui
         */
        public int getPeriodImage() {
            return getTimePeriod().getImageId();
        }

        public double getIntervalInHour() {
            return getIntervalInMillis() / (1000 * 60 * 60);
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }
    }

    public ReservationState(ReservationStateBuilder builder) {
        this.roomName = builder.getRoomName();
        this.devId = builder.getDevId();
        this.kind = builder.getKind();
        this.range = builder.getRange();
        this.availableTimeRanges = builder.getAvailableTimeRanges();
    }

    @Override
    public String getRoomName() {
        return roomName;
    }

    @Override
    public String getDevId() {
        return devId;
    }

    @Override
    public RoomLabKind getKind() {
        return kind;
    }

    @Override
    public int getFloorStringId() {
        return this.kind.getFloorStringId();
    }

    public List<TimeRange> getAvailableTimeRanges(int intervalInHour) {
        List<TimeRange> ret = new ArrayList<>();
        for (TimeRange timeRange : this.availableTimeRanges) {
            if ( timeRange.getIntervalInHour() >= intervalInHour) {
                ret.add(timeRange);
            }
        }
        return ret;
    }

    @Override
    public List<TimeRange> getAvailableTimeRanges() {
        return getAvailableTimeRanges(DEFAULT_MIN_INTERVAL);
    }

    @Override
    public TimeRange getRange() {
        return range;
    }

    public void setAvailableTimeRanges(List<TimeRange> availableTimeRanges) {
        this.availableTimeRanges = availableTimeRanges;
    }

    public boolean isHumanitiesLibSingleRoom() {
        return (this.kind == RoomLabKind.HumanitiesLibSecFloorSingle
            || this.kind == RoomLabKind.HumanitiesLibThirdFloorSingle)
                && !this.roomName.contains("办公室");
    }

    public boolean isRoomAvailable() {
        return !this.roomName.contains("取消") && !this.roomName.contains("故障");
    }

    public boolean hasAvailableRooms() {
        return !this.availableTimeRanges.isEmpty();
    }

}
