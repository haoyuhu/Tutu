package mu.lab.thulib.thucab.entity;

import java.util.Calendar;

import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Reservation record
 * Created by coderhuhy on 15/11/15.
 */
public interface ReservationRecord {

    public String getReservationId();

    public String getStudentId();

    public String getName();

    public String getRoomName();

    public String getState();

    public Calendar getStartDateTime() throws DateTimeUtilities.DateTimeException;

    public Calendar getEndDateTime() throws DateTimeUtilities.DateTimeException;

    public Calendar getDate() throws DateTimeUtilities.DateTimeException;

}
