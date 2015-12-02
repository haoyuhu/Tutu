package mu.lab.thulib.thucab.entity;

import java.util.Calendar;

import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Reservation record
 * Created by coderhuhy on 15/11/15.
 */
public interface ReservationRecord {

    String getReservationId();

    String getStudentId();

    String getName();

    String getRoomName();

    String getState();

    Calendar getStartDateTime() throws DateTimeUtilities.DateTimeException;

    Calendar getEndDateTime() throws DateTimeUtilities.DateTimeException;

    Calendar getDate() throws DateTimeUtilities.DateTimeException;

}
