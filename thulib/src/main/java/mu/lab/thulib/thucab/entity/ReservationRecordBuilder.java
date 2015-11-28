package mu.lab.thulib.thucab.entity;

/**
 * Reservation record builder
 * Created by coderhuhy on 15/11/15.
 */
public class ReservationRecordBuilder {

    private String reservationId;
    private String studentId;
    private String name;
    private String roomName;
    private String state;
    private String start;
    private String end;

    public ReservationRecordBuilder from(RealmReservationRecord record) {
        this.reservationId = record.getReservationId();
        this.studentId = record.getStudentId();
        this.name = record.getName();
        this.roomName = record.getRoomName();
        this.state = record.getState();
        this.start = record.getStart();
        this.end = record.getEnd();
        return this;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getState() {
        return state;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public ReservationRecordBuilder setReservationId(String reservationId) {
        this.reservationId = reservationId;
        return this;
    }

    public ReservationRecordBuilder setStudentId(String studentId) {
        this.studentId = studentId;
        return this;
    }

    public ReservationRecordBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ReservationRecordBuilder setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public ReservationRecordBuilder setState(String state) {
        this.state = state;
        return this;
    }

    public ReservationRecordBuilder setStart(String start) {
        this.start = start;
        return this;
    }

    public ReservationRecordBuilder setEnd(String end) {
        this.end = end;
        return this;
    }

    public ReservationRecord build() {
        return new ReservationRecordImpl(this);
    }

    public RealmReservationRecord buildRealmEntity() {
        return new RealmReservationRecord(this);
    }

}
