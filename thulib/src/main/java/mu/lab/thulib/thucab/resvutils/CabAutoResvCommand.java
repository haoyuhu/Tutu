package mu.lab.thulib.thucab.resvutils;

import java.util.List;

import mu.lab.thulib.thucab.CabUtilities;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.AutoReservationItem;
import mu.lab.thulib.thucab.entity.ReservationState;

/**
 * Cab auto reservation command
 * Created by coderhuhy on 15/11/18.
 */
public class CabAutoResvCommand extends CabAbstractCommand {

    private AutoReservationItem item;

    public CabAutoResvCommand(AutoReservationItem item) {
        super(null);
        this.item = item;
    }

    @Override
    public void executeCommand() throws Exception {
        if (!item.shouldMakeReservation()) {
            return;
        }
        int increment = item.getIncrement();
        boolean success = false;
        List<ReservationState> list = CabUtilities.queryRoomState(DateTimeUtilities.DayRound.values()[increment]);
        for (int i = 0; i < list.size() && !success; ++i) {
            ReservationState s = list.get(i);
            List<ReservationState.TimeRange> ranges = s.getAvailableTimeRanges();
            for (ReservationState.TimeRange r : ranges) {
                String start = r.getStart();
                String end = r.getEnd();
                if (DateTimeUtilities.calculateInterval(start, item.getStart()) <= 0
                    && DateTimeUtilities.calculateInterval(item.getEnd(), end) <= 0) {
                    CabCommand command =
                        CabCommandCreator.createReservationCommand(s, item);
                    command.executeCommand();
                    success = true;
                    break;
                }
            }
        }
    }
}
