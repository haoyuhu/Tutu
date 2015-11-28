package mu.lab.thulib.thucab.entity;

import java.util.Calendar;

import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Reservation record impl
 * Created by coderhuhy on 15/11/15.
 */
public class ReservationRecordImpl implements ReservationRecord {

    private String reservationId;
    private String studentId;
    private String name;
    private String roomName;
    private String state;
    private String start;
    private String end;

    public ReservationRecordImpl(ReservationRecordBuilder builder) {
        this.reservationId = builder.getReservationId();
        this.studentId = builder.getStudentId();
        this.name = builder.getName();
        this.roomName = builder.getRoomName();
        this.state = builder.getState();
        this.start = builder.getStart();
        this.end = builder.getEnd();
    }

    @Override
    public String getReservationId() {
        return reservationId;
    }

    @Override
    public String getStudentId() {
        return studentId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRoomName() {
        return roomName;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public Calendar getStartDateTime() throws DateTimeUtilities.DateTimeException {
        return DateTimeUtilities.dateTimeToCalendar(this.start);
    }

    @Override
    public Calendar getEndDateTime() throws DateTimeUtilities.DateTimeException {
        return DateTimeUtilities.dateTimeToCalendar(this.end);
    }

    @Override
    public Calendar getDate() throws DateTimeUtilities.DateTimeException {
        return getStartDateTime();
    }
}
