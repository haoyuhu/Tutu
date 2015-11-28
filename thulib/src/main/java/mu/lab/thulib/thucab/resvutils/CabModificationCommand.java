package mu.lab.thulib.thucab.resvutils;

import java.util.Calendar;

import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.CabUtilities;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.ReservationState;

/**
 * Cab modification command
 * Created by coderhuhy on 15/11/15.
 */
public class CabModificationCommand extends CabAbstractCommand {

    private final static String LogTag = CabModificationCommand.class.getSimpleName();

    private ReservationRecord record;
    private String start;
    private String end;

    public CabModificationCommand(ReservationRecord record, ReservationState.TimeRange range,
                                  ExecutorResultObserver observer) throws CabCommandException {
        super(observer);
        if (range.getIntervalInMillis() > CabConstants.ReservationConstants.MAX_RESERVATION_MILLIS
            + CabConstants.DateTimeConstants.ALLOWABLE_ERROR
            || range.getIntervalInMillis() < CabConstants.ReservationConstants.MIN_RESERVATION_MILLIS
            - CabConstants.DateTimeConstants.ALLOWABLE_ERROR) {
            throw new CabCommandException("cannot make reservation more than 4 hours or less than 0.5 hour...");
        }
        this.record = record;
        this.start = range.getStart();
        this.end  = range.getEnd();
    }

    public String getReservationId() {
        return this.record.getReservationId();
    }

    public String getStart() throws DateTimeUtilities.DateTimeException {
        final String pattern = "yyyy-MM-dd HH:mm";
        Calendar origin = record.getStartDateTime();
        Calendar current = DateTimeUtilities.timeToCalendar(this.start);
        origin.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
        origin.set(Calendar.MINUTE, current.get(Calendar.MINUTE));
        return DateTimeUtilities.formatReservationDate(origin, pattern);
    }

    public String getEnd() throws DateTimeUtilities.DateTimeException {
        final String pattern = "yyyy-MM-dd HH:mm";
        Calendar origin = record.getEndDateTime();
        Calendar current = DateTimeUtilities.timeToCalendar(this.end);
        origin.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
        origin.set(Calendar.MINUTE, current.get(Calendar.MINUTE));
        return DateTimeUtilities.formatReservationDate(origin, pattern);
    }

    @Override
    public void executeCommand() throws Exception {
        if (this.observer != null) {
            CabUtilities.modifyReservation(this, this.observer);
        } else {
            CabUtilities.modifyReservation(this);
        }

    }

}
