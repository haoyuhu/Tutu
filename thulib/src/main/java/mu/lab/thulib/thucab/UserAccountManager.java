package mu.lab.thulib.thucab;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;

/**
 * User account manager
 * Created by coderhuhy on 15/11/30.
 */
public class UserAccountManager {

    private static final String LogTag = UserAccountManager.class.getCanonicalName();

    private static class UserAccountManagerHolder {
        static UserAccountManager instance = new UserAccountManager();
    }

    private UserAccountManager() {
    }

    public static UserAccountManager getInstance() {
        return UserAccountManagerHolder.instance;
    }

    protected StudentAccount account;
    protected StudentDetails details;

    public void init(Context context) {
        PreferenceUtilities.init(context);
        try {
            this.account = PreferenceUtilities.getStudentAccount();
        } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
            Log.e(LogTag, error.toString(), error);
        }
        try {
            this.details = PreferenceUtilities.getStudentDetails();
        } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
            Log.e(LogTag, error.toString(), error);
        }
    }

    public boolean save(@NonNull StudentAccount account) {
        this.account = account;
        return PreferenceUtilities.saveStudenId(account.getStudentId())
                && PreferenceUtilities.savePassword(account.getPassword());
    }

    public boolean save(@NonNull StudentDetails details) {
        this.details = details;
        return PreferenceUtilities.saveUsername(details.getName())
                && PreferenceUtilities.saveDepartment(details.getDepartment())
                && PreferenceUtilities.savePhone(details.getPhone())
                && PreferenceUtilities.saveEmail(details.getEmail());
    }

    public StudentAccount getAccount() throws PreferenceUtilities.StudentAccountNotFoundError {
        if (this.account == null) {
            throw new PreferenceUtilities.StudentAccountNotFoundError("account is incomplete...");
        }
        return this.account;
    }

    public StudentDetails getDetails() throws PreferenceUtilities.StudentAccountNotFoundError {
        if (this.details == null) {
            throw new PreferenceUtilities.StudentAccountNotFoundError("details is incomplete...");
        }
        return this.details;
    }

    public boolean hasAccount() {
        return account != null;
    }

    public boolean hasDetails() {
        return details != null;
    }

    public void clear() {
        PreferenceUtilities.clear();
        account = null;
        details = null;
    }

}
