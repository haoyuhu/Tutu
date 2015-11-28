package mu.lab.thulib.thucab.resvutils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import mu.lab.thulib.thucab.CabUtilities;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.RecommendResv;
import mu.lab.thulib.thucab.entity.RecommendResvBuilder;
import mu.lab.thulib.thucab.entity.RecommendResvImpl;
import mu.lab.thulib.thucab.entity.ReservationState;

/**
 * Cab smart reservation command
 * Created by coderhuhy on 15/11/17.
 */
public class CabSmartResvCommand extends CabAbstractCommand {

    private static final String LogTag = CabSmartResvCommand.class.getSimpleName();

    private DateTimeUtilities.DayRound round;
    private ReservationState.TimeRange range;
    private int minInterval;
    private SmartReservationObserver smartObserver;

    public CabSmartResvCommand(DateTimeUtilities.DayRound round, ReservationState.TimeRange range,
                               int minInterval, @NonNull SmartReservationObserver observer) throws CabCommandException {
        super(observer);
        this.smartObserver = observer;
        this.round = round;
        this.range = range;
        this.minInterval = minInterval;
    }

    @Override
    public void executeCommand() throws Exception {
        boolean success = false;
        List<RecommendResvImpl> rest = new ArrayList<>();
        List<ReservationState> list = CabUtilities.queryRoomState(round);
        for (int i = 0; i < list.size() && !success; ++i) {
            ReservationState s = list.get(i);
            List<ReservationState.TimeRange> ranges = s.getAvailableTimeRanges(minInterval);
            for (ReservationState.TimeRange r : ranges) {
                String start = r.getStart();
                String end = r.getEnd();
                if (DateTimeUtilities.calculateInterval(start, range.getStart()) <= 0
                    && DateTimeUtilities.calculateInterval(range.getEnd(), end) <= 0) {
                    Calendar date = DateTimeUtilities.dayRoundToCalendar(round);
                    CabCommand command =
                        CabCommandCreator.createReservationCommand(s, date, range, smartObserver);
                    command.executeCommand();
                    success = true;
                    break;
                } else {
                    RecommendResvBuilder builder = new RecommendResvBuilder();
                    RecommendResvImpl resv = builder
                        .setDevId(s.getDevId())
                        .setRoomName(s.getRoomName())
                        .setKind(s.getKind())
                        .setRange(r).build();
                    try {
                        resv.calculatePriority(range);
                    } catch (DateTimeUtilities.DateTimeException error) {
                        Log.e(LogTag, error.getDetails(), error);
                        resv.setMaxPriority();
                    }
                    rest.add(resv);
                }
            }
        }
        if (!success) {
            Collections.sort(rest);
            List<RecommendResv> ret = new ArrayList<>();
            for (RecommendResvImpl resv : rest) {
                ret.add(resv.memorandum());
            }
            smartObserver.onNoMatchedRoom(ret);
        }
    }

    public abstract static class SmartReservationObserver implements ExecutorResultObserver {

        public abstract void onNoMatchedRoom(List<RecommendResv> list);

    }
}
