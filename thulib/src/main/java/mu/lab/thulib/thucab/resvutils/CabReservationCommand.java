package mu.lab.thulib.thucab.resvutils;

import java.util.Calendar;

import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.CabUtilities;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.AutoReservationItem;
import mu.lab.thulib.thucab.entity.ReservationState;

/**
 * Cab reservation command
 * Created by coderhuhy on 15/11/16.
 */
public class CabReservationCommand extends CabAbstractCommand {

    private ReservationState record;
    private Calendar date;
    // format as HH:mm
    private String start;
    // format as HH:mm
    private String end;

    public CabReservationCommand(ReservationState record, Calendar date,
                                 ReservationState.TimeRange range, ExecutorResultObserver observer)
        throws CabCommandException {
        super(observer);
        if (range.getIntervalInMillis() > CabConstants.ReservationConstants.MAX_RESERVATION_MILLIS
            + CabConstants.DateTimeConstants.ALLOWABLE_ERROR
            || range.getIntervalInMillis() < CabConstants.ReservationConstants.MIN_RESERVATION_MILLIS
            - CabConstants.DateTimeConstants.ALLOWABLE_ERROR) {
            throw new CabCommandException("cannot make reservation more than 4 hours or less than 0.5 hour...");
        }
        this.record = record;
        this.date = date;
        this.start = range.getStart();
        this.end = range.getEnd();
    }

    public CabReservationCommand(ReservationState record, AutoReservationItem item) throws CabCommandException {
        this(record, item.getDate(), item.getRange(), null);

    }

    public String getDevKind() {
        return record.getKind().getRoom();
    }

    public String getDev() {
        return record.getDevId();
    }

    public String getLabId() {
        return record.getKind().getLab();
    }

    public String getDate() {
        final String pattern = "yyyyMMdd";
        return DateTimeUtilities.formatReservationDate(date, pattern);
    }

    public String getStart() {
        return this.start.replaceAll(":", "");
    }

    public String getEnd() {
        return this.end.replaceAll(":", "");
    }

    @Override
    public void executeCommand() throws Exception {
        if (observer != null) {
            CabUtilities.makeReservation(this, observer);
        } else {
            CabUtilities.makeReservation(this);
        }
    }
}
