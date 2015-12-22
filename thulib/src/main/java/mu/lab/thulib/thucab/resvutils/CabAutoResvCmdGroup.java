package mu.lab.thulib.thucab.resvutils;

import android.util.Log;

import java.util.List;

import mu.lab.thulib.thucab.AutoResvStore;
import mu.lab.thulib.thucab.UserAccountManager;
import mu.lab.thulib.thucab.entity.AutoReservationItem;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.httputils.ResponseState;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * Cab auto reservation command group
 * Created by coderhuhy on 15/11/18.
 */
public class CabAutoResvCmdGroup extends CabAbstractCommand {

    private final static String LogTag = CabAutoResvCmdGroup.class.getSimpleName();

    public CabAutoResvCmdGroup() {
        super(null, CommandKind.AutoReservationGroup);
    }

    @Override
    public ExecuteResult executeCommand() throws Exception {
        boolean success = true;
        List<AutoReservationItem> list = UserAccountManager.getInstance().getSettings();
        for (AutoReservationItem item : list) {
            CabAutoResvCommand command = new CabAutoResvCommand(item);
            try {
                ExecuteResult.CommandResultState s = command.executeCommand().getResultState();
                if (s.equals(ExecuteResult.CommandResultState.Conflict)) {
                    success = false;
                }
            } catch (Exception e) {
                Log.e(LogTag, e.getMessage(), e);
            }
        }
        return new ExecuteResult(cmdKind, observer,
                success ? ExecuteResult.CommandResultState.Success : ExecuteResult.CommandResultState.Conflict);
    }

}
