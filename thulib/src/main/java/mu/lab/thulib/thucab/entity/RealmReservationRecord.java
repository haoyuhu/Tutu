package mu.lab.thulib.thucab.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Reservation record impl
 * Created by coderhuhy on 15/11/15.
 */
public class RealmReservationRecord extends RealmObject {

    @PrimaryKey
    private String reservationId;
    private String studentId;
    private String name;
    private String roomName;
    private String state;
    private String start;
    private String end;

    public RealmReservationRecord() {
    }

    public RealmReservationRecord(ReservationRecordBuilder builder) {
        this.reservationId = builder.getReservationId();
        this.studentId = builder.getStudentId();
        this.name = builder.getName();
        this.roomName = builder.getRoomName();
        this.state = builder.getState();
        this.start = builder.getStart();
        this.end = builder.getEnd();
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

}
