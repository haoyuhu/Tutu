package mu.lab.thulib.thucab.resvutils;

import android.util.Log;

import java.util.List;

import mu.lab.thulib.thucab.AutoResvStore;
import mu.lab.thulib.thucab.entity.AutoReservationItem;
import mu.lab.thulib.thucab.entity.StudentAccount;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * Cab auto reservation command group
 * Created by coderhuhy on 15/11/18.
 */
public class CabAutoResvCmdGroup extends CabAbstractCommand implements Observer<List<AutoReservationItem>> {

    private final static String LogTag = CabAutoResvCmdGroup.class.getSimpleName();

    private StudentAccount account;

    public CabAutoResvCmdGroup(StudentAccount account) {
        super(null);
        this.account = account;
    }

    @Override
    public void executeCommand() throws Exception {
        AutoResvStore.getAutoResvItems(account).subscribeOn(Schedulers.io()).subscribe(this);
    }

    @Override
    public void onCompleted() {
        Log.i(LogTag, "get auto reservation list from realm successfully...");
    }

    @Override
    public void onError(Throwable e) {
        Log.i(LogTag, "fail to get auto reservation list from realm...");
    }

    @Override
    public void onNext(List<AutoReservationItem> list) {
        for (AutoReservationItem item : list) {
            CabAutoResvCommand command = new CabAutoResvCommand(item);
            try {
                command.executeCommand();
            } catch (Exception e) {
                Log.e(LogTag, e.getMessage(), e);
            }
        }
    }

}
