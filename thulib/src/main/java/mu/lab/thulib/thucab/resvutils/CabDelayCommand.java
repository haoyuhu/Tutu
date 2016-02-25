package mu.lab.thulib.thucab.resvutils;

import java.util.Calendar;

import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.ReservationState;

/**
 * Cab delay command
 * Created by coderhuhy on 15/12/12.
 */
public class CabDelayCommand extends CabAbstractCommand {

    private static final String LogTag = CabDelayCommand.class.getCanonicalName();

    private ReservationRecord record;
    private String start;
    private String end;

    public CabDelayCommand(ReservationRecord record, int delayInMinutes, ExecutorResultObserver observer)
            throws DateTimeUtilities.DateTimeException {
        super(observer, CommandKind.Delay);
        final String pattern = "HH:mm";
        this.record = record;
        String s = DateTimeUtilities.formatReservationDate(record.getStartDateTime(), pattern);
        String e = DateTimeUtilities.formatReservationDate(record.getEndDateTime(), pattern);

        String[] hm = s.split(":");
        int h = Integer.parseInt(hm[0]);
        int m = Integer.parseInt(hm[1]);
        m += delayInMinutes;
        while (m >= CabConstants.DateTimeConstants.MINUTE_OF_HOUR) {
            m -= CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
            ++h;
        }
        this.start = "" + h + ":" + m;
        this.end = e;
    }

    @Override
    public ExecuteResult executeCommand() throws Exception {
        CabCommand command = CabCommandCreator.createModificationCommand(
                record, new ReservationState.TimeRange(start, end), observer);
        return command.executeCommand();
    }
}
