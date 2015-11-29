package mu.lab.thulib.thucab.httputils;

import mu.lab.thulib.thucab.PreferenceUtilities;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;

/**
 * Login State Observer
 * Created by coderhuhy on 15/11/12.
 */
public abstract class LoginStateObserver implements AbstractLoginObserver {

    public void onLoginSuccess(StudentDetails details, StudentAccount account) {
        PreferenceUtilities.saveStudenId(account.getStudentId());
        PreferenceUtilities.savePassword(account.getPassword());
        PreferenceUtilities.saveUsername(details.getName());
        PreferenceUtilities.saveDepartment(details.getDepartment());
    }

    public void onStudentIdFailure(int resId) {
    }

    public void onPasswordFailure(int resId) {
    }

    public void onNetworkFailure(int resId) {
    }

}
