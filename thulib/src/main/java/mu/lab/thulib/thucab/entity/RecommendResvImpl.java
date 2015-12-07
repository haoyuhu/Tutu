package mu.lab.thulib.thucab.entity;

import android.support.annotation.NonNull;

import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Recommend reservation record implement
 * Created by coderhuhy on 15/11/18.
 */
public class RecommendResvImpl implements Comparable<RecommendResv>, RecommendResv {

    private static final double PRIORITY_INTERVAL_WEIGHT = 0.5;

    private String roomName;
    private String devId;
    private RoomLabKind kind;
    private ReservationState.TimeRange range;
    private long priority;

    public RecommendResvImpl(RecommendResvBuilder builder) {
        this.roomName = builder.getRoomName();
        this.devId = builder.getDevId();
        this.kind = builder.getKind();
        this.range = builder.getRange();
    }

    @Override
    public String getRoomName() {
        return this.roomName;
    }

    @Override
    public String getDevId() {
        return this.devId;
    }

    @Override
    public String getStart() {
        return this.range.getStart();
    }

    @Override
    public String getEnd() {
        return this.range.getEnd();
    }

    @Override
    public String getDevKind() {
        return this.kind.getRoom();
    }

    @Override
    public String getLabId() {
        return this.kind.getLab();
    }

    @Override
    public int getFloorStringId() {
        return this.kind.getFloorStringId();
    }

    @Override
    public long getPriority() {
        return this.priority;
    }

    public void calculatePriority(ReservationState.TimeRange r) throws DateTimeUtilities.DateTimeException {
        this.priority = DateTimeUtilities.calculateAbsInterval(r.getStart(), range.getStart())
                + DateTimeUtilities.calculateAbsInterval(r.getEnd(), range.getEnd())
                - (long) (range.getIntervalInMillis() * PRIORITY_INTERVAL_WEIGHT);
    }

    public void setMaxPriority() {
        this.priority = MAX_PRIORITY;
    }

    public RecommendResv memorandum() {
        return this;
    }

    @Override
    public int compareTo(@NonNull RecommendResv another) {
        return (int) (this.priority - another.getPriority());
    }
}
