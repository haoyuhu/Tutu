package mu.lab.thulib.thucab.httputils;

import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;

/**
 * Abstract login observer
 * Created by coderhuhy on 15/11/29.
 */
public interface AbstractLoginObserver {

    void onLoginSuccess(StudentDetails details, StudentAccount account);

    void onStudentIdFailure(int resId);

    void onPasswordFailure(int resId);

    void onNetworkFailure(int resId);

}
