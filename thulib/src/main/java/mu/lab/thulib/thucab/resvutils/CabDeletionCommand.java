package mu.lab.thulib.thucab.resvutils;

import mu.lab.thulib.thucab.CabUtilities;

/**
 * Cab deletion command
 * Created by coderhuhy on 15/11/16.
 */
public class CabDeletionCommand extends CabAbstractCommand {

    private String reservationId;

    public CabDeletionCommand(String reservationId, ExecutorResultObserver observer) {
        super(observer);
        this.reservationId = reservationId;
    }

    @Override
    public void executeCommand() throws Exception {
        if (observer != null) {
            CabUtilities.deleteReservation(this.reservationId, this.observer);
        } else {
            CabUtilities.deleteReservation(this.reservationId);
        }
    }
}
