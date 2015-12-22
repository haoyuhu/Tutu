package mu.lab.thulib.thucab.httputils;

import mu.lab.thulib.thucab.UserAccountManager;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;

/**
 * Login State Observer
 * Created by coderhuhy on 15/11/12.
 */
public abstract class LoginStateObserver implements AbstractLoginObserver {

    public void onLoginSuccess(StudentDetails details, StudentAccount account) {
        UserAccountManager manager = UserAccountManager.getInstance();
        manager.save(account);
        manager.save(details);
    }

    public void onStudentIdFailure(int resId) {
    }

    public void onPasswordFailure(int resId) {
    }

    public void onNetworkFailure(int resId) {
    }

    public void onActivateFailure(int resId) {
    }

}
