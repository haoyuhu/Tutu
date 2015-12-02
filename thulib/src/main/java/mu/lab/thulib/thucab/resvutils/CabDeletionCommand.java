package mu.lab.thulib.thucab.resvutils;

import mu.lab.thulib.thucab.CabUtilities;
import mu.lab.thulib.thucab.httputils.ResponseState;

/**
 * Cab deletion command
 * Created by coderhuhy on 15/11/16.
 */
public class CabDeletionCommand extends CabAbstractCommand {

    private String reservationId;

    public CabDeletionCommand(String reservationId, ExecutorResultObserver observer) {
        super(observer, CommandKind.Deletion);
        this.reservationId = reservationId;
    }

    @Override
    public ExecuteResult executeCommand() throws Exception {
        ResponseState result = CabUtilities.deleteReservation(this.reservationId);
        ExecuteResult.CommandResultState ret;
        switch (result) {
            case ReservationSuccess:
                ret = ExecuteResult.CommandResultState.Success;
                break;
            case DateFailure:
                ret = ExecuteResult.CommandResultState.Local;
                break;
            default:
                ret = ExecuteResult.CommandResultState.NetworkFailure;
        }
        return new ExecuteResult(cmdKind, observer, ret);
    }
}
