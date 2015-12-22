package mu.lab.thulib.thucab.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Recommend reservation record implement
 * Created by coderhuhy on 15/11/18.
 */
public class RecommendResvImpl extends RecommendResv implements Comparable<RecommendResv>, Parcelable {

    private static final String LogTag = RecommendResvImpl.class.getCanonicalName();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(roomName);
        dest.writeString(devId);
        dest.writeString(kind.getRoom());
        dest.writeString(kind.getLab());
        dest.writeString(range.getStart());
        dest.writeString(range.getEnd());
        dest.writeLong(priority);
    }

    public static final Creator<RecommendResv> CREATOR = new Creator<RecommendResv>() {
        @Override
        public RecommendResv createFromParcel(Parcel source) {
            String room = source.readString();
            String id = source.readString();
            RoomLabKind k;
            try {
                k = RoomLabKind.fromRoomLab(source.readString(), source.readString());
            } catch (RoomLabKind.NoRoomLabKindException e) {
                Log.e(LogTag, e.getDetails(), e);
                k = RoomLabKind.HumanitiesLibSecFloorSingle;
            }
            ReservationState.TimeRange r = new ReservationState.TimeRange(source.readString(), source.readString());
            long p = source.readLong();
            RecommendResvBuilder builder = new RecommendResvBuilder();
            RecommendResvImpl resv = builder.setRoomName(room).setDevId(id).setKind(k).setRange(r).build();
            resv.priority = p;
            return resv;
        }

        @Override
        public RecommendResv[] newArray(int size) {
            return new RecommendResv[0];
        }
    };
}
