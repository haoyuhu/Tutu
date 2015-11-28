package mu.lab.thulib.thucab.entity;

/**
 * Recommend reservation record builder
 * Created by coderhuhy on 15/11/18.
 */
public class RecommendResvBuilder {

    private String roomName;
    private String devId;
    private RoomLabKind kind;
    private ReservationState.TimeRange range;

    public RecommendResvBuilder setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public RecommendResvBuilder setDevId(String devId) {
        this.devId = devId;
        return this;
    }

    public RecommendResvBuilder setKind(RoomLabKind kind) {
        this.kind = kind;
        return this;
    }

    public RecommendResvBuilder setRange(ReservationState.TimeRange range) {
        this.range = range;
        return this;
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

    public RecommendResvImpl build() {
        return new RecommendResvImpl(this);
    }

}
