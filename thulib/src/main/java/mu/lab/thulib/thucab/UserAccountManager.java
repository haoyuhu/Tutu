package mu.lab.thulib.thucab;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mu.lab.thulib.thucab.entity.AutoReservationItem;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * User account manager
 * Created by coderhuhy on 15/11/30.
 */
public class UserAccountManager implements Observer<List<AutoReservationItem>> {

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
    protected List<AutoReservationItem> settings;

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
        if (hasAccount()) {
            AutoResvStore.getAutoResvItems(account)
                    .subscribeOn(Schedulers.io())
                    .subscribe(this);
        } else {
            settings = new ArrayList<>();
        }
    }

    public boolean save(@NonNull List<AutoReservationItem> settings) {
        this.settings = settings;
        if (hasAccount()) {
            AutoResvStore.clear(account);
            AutoResvStore.saveAutoResvItemsToRealm(settings, account);
            return true;
        }
        return false;
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

    public List<AutoReservationItem> getSettings() throws PreferenceUtilities.StudentAccountNotFoundError {
        if (!hasAccount()) {
            throw new PreferenceUtilities.StudentAccountNotFoundError("account is incomplete, cannot get auto reservation settings...");
        }
        return this.settings;
    }

    public StudentAccount getAccount() throws PreferenceUtilities.StudentAccountNotFoundError {
        if (!hasAccount()) {
            throw new PreferenceUtilities.StudentAccountNotFoundError("account is incomplete, cannot get account...");
        }
        return this.account;
    }

    public StudentDetails getDetails() throws PreferenceUtilities.StudentAccountNotFoundError {
        if (!hasAccount()) {
            throw new PreferenceUtilities.StudentAccountNotFoundError("details is incomplete, cannot get details...");
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
        AutoResvStore.clear(account);
        account = null;
        details = null;
        settings = null;
    }

    @Override
    public void onCompleted() {
        if (settings == null) {
            settings = new ArrayList<>();
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e(LogTag, e.getMessage(), e);
    }

    @Override
    public void onNext(List<AutoReservationItem> items) {
        settings = items;
    }
}
