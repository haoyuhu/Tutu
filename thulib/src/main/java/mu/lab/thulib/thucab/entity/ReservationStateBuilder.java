package mu.lab.thulib.thucab.entity;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.util.Log;

/**
 * Reservation state builder
 * Created by coderhuhy on 15/11/12.
 */
public class ReservationStateBuilder {

    private static final String LogTag = ReservationStateBuilder.class.getSimpleName();

    private String roomName;
    private String devId;
    private RoomLabKind kind;
    private ReservationState.TimeRange range;
    private List<ReservationState.TimeRange> availableTimeRanges;

    public ReservationStateBuilder fromJsonReservationState(JsonReservationState state, boolean isToday)
        throws RoomLabKind.NoRoomLabKindException {
        this.roomName = state.getRoomName();
        this.devId = state.getDevId();
        this.kind = RoomLabKind.fromRoomLab(state.getKindId(), state.getLabId());
        this.range = new ReservationState.TimeRange(state.getOpenStart(), state.getOpenEnd());
        this.availableTimeRanges = new ArrayList<>();
        String start = state.getOpenStart();
        String current = DateTimeUtilities.getCurrentTimePoint();
        if (start == null) {
            return this;
        }
        try {
            if (isToday && DateTimeUtilities.calculateInterval(start, current) < 0) {
                start = current;
            }
        } catch (DateTimeUtilities.DateTimeException e) {
            Log.e(LogTag, e.getDetails(), e);
            start = current;
        }
        List<JsonReservationState.JsonStateItem> list = state.getStates();
        Collections.sort(list, new Comparator<JsonReservationState.JsonStateItem>() {
            @Override
            public int compare(JsonReservationState.JsonStateItem lhs, JsonReservationState.JsonStateItem rhs) {
                try {
                    String start = DateTimeUtilities.getTimePart(lhs.getStart());
                    String another = DateTimeUtilities.getTimePart(rhs.getStart());
                    return (int) (DateTimeUtilities.calculateInterval(start, another) / 1000);
                } catch (DateTimeUtilities.DateTimeException e) {
                    android.util.Log.e(LogTag, e.getDetails(), e);
                    return 0;
                }
            }
        });
        for (JsonReservationState.JsonStateItem item : list) {
            try {
                String time = DateTimeUtilities.getTimePart(item.getStart());
                if (DateTimeUtilities.calculateInterval(start, time) < 0) {
                    ReservationState.TimeRange timeRange = new ReservationState.TimeRange(start, time);
                    this.availableTimeRanges.add(timeRange);
                }
                start = DateTimeUtilities.getTimePart(item.getEnd());
            } catch (DateTimeUtilities.DateTimeException e) {
                Log.e(LogTag, e.getDetails(), e);
            }
        }
        try {
            if (!TextUtils.isEmpty(state.getOpenEnd())) {
                String time = state.getOpenEnd();
                if (DateTimeUtilities.calculateInterval(start, time) < 0) {
                    ReservationState.TimeRange timeRange = new ReservationState.TimeRange(start, time);
                    this.availableTimeRanges.add(timeRange);
                }
            }
        } catch (DateTimeUtilities.DateTimeException e) {
            Log.e(LogTag, e.getDetails(), e);
        }
        return this;
    }

    public ReservationState build() {
        return new ReservationState(this);
    }

    public String getRoomName() {
        return roomName;
    }

    public String getDevId() {
        return devId;
    }

    public RoomLabKind getKind() {
        return kind;
    }

    public ReservationState.TimeRange getRange() {
        return range;
    }

    public List<ReservationState.TimeRange> getAvailableTimeRanges() {
        return availableTimeRanges;
    }
}
