package mu.lab.thulib.thucab.resvutils;

import java.util.Calendar;

import mu.lab.thulib.thucab.entity.RecommendResv;
import mu.lab.thulib.thucab.entity.ReservationState;

/**
 * Cab recommendation command
 * Created by coderhuhy on 15/12/7.
 */
public class CabRecommendationCommand extends CabReservationCommand {

    private RecommendResv item;

    public CabRecommendationCommand(RecommendResv item, Calendar date, ReservationState.TimeRange range,
                                    ExecutorResultObserver observer)
            throws CabCommandException {
        super(null, date, range, observer);
        this.item = item;
    }

    @Override
    public String getDevKind() {
        return item.getDevKind();
    }

    @Override
    public String getDev() {
        return item.getDevId();
    }

    @Override
    public String getLabId() {
        return item.getLabId();
    }

}
