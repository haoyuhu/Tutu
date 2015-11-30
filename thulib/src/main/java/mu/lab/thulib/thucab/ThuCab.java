package mu.lab.thulib.thucab;

import android.content.Context;

import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.resvutils.CabCmdExecutorImpl;

/**
 * Thu cab
 * Created by coderhuhy on 15/11/19.
 */
public class ThuCab {

    public static void init(Context context) {
        UserAccountManager.getInstance().init(context);
    }

    /**
     * Clear Preference, Database and current commands
     *
     * @param account Student account
     */
    public static void clear(StudentAccount account) {
        CabCmdExecutorImpl.getInstance().clear();
        ResvRecordStore.clear(account);
        AutoResvStore.clear(account);
        UserAccountManager.getInstance().clear();
    }

}
