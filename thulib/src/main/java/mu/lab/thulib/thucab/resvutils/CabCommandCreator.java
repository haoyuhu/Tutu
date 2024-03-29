package mu.lab.thulib.thucab.resvutils;

import java.util.Calendar;

import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.AbstractState;
import mu.lab.thulib.thucab.entity.AutoReservationItem;
import mu.lab.thulib.thucab.entity.RecommendResv;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.ReservationState;
import mu.lab.thulib.thucab.entity.StudentAccount;

/**
 * Cab command creator
 * Created by coderhuhy on 15/11/17.
 */
public class CabCommandCreator {

    public static CabCommand createReservationCommand(
        AbstractState record, Calendar date, ReservationState.TimeRange range,
        ExecutorResultObserver observer) throws CabCommand.CabCommandException {
        return new CabReservationCommand(record, date, range, observer);
    }

    public static CabCommand createReservationCommand(
        ReservationState record, Calendar date, ReservationState.TimeRange range) throws CabCommand.CabCommandException {
        return new CabReservationCommand(record, date, range, null);
    }

    public static CabCommand createReservationCommand(ReservationState record, AutoReservationItem item)
        throws CabCommand.CabCommandException {
        return new CabReservationCommand(record, item);
    }

    public static CabCommand createRecommendationCommand(RecommendResv item, Calendar date, ReservationState.TimeRange range,
                                                         ExecutorResultObserver observer) throws CabCommand.CabCommandException {
        return new CabRecommendationCommand(item, date, range, observer);
    }

    public static CabCommand createModificationCommand(ReservationRecord record, ReservationState.TimeRange range,
                                                       ExecutorResultObserver observer) throws CabCommand.CabCommandException {
        return new CabModificationCommand(record, range, observer);
    }

    public static CabCommand createModificationCommand(
        ReservationRecord record, ReservationState.TimeRange range) throws CabCommand.CabCommandException {
        return new CabModificationCommand(record, range, null);
    }

    public static CabCommand createDeletionCommand(String reservationId, ExecutorResultObserver observer) {
        return new CabDeletionCommand(reservationId, observer);
    }

    public static CabCommand createDeletionCommand(String reservationId) {
        return new CabDeletionCommand(reservationId, null);
    }

    public static CabCommand createSmartResvCommand(DateTimeUtilities.DayRound round, ReservationState.TimeRange range,
                                                    int minInterval, CabSmartResvCommand.SmartReservationObserver observer)
        throws CabCommand.CabCommandException {
        return new CabSmartResvCommand(round, range, minInterval, observer);
    }

    public static CabCommand createAutoResvCmdGroup() {
        return new CabAutoResvCmdGroup();
    }

    public static CabCommand createPostponeCommand(ReservationRecord record, int minutes, ExecutorResultObserver observer)
            throws DateTimeUtilities.DateTimeException {
        return new CabDelayCommand(record, minutes, observer);
    }

}
