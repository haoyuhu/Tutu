package mu.lab.thulib.thucab.httputils;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import mu.lab.thulib.R;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;
import rx.Observer;

/**
 * Login subscriber
 * Created by coderhuhy on 15/11/29.
 */
public class LoginSubscriber implements Observer<CabObjectResponse>{

    private static final String LogTag = LoginSubscriber.class.getCanonicalName();

    protected List<AbstractLoginObserver> observers = new ArrayList<>();
    private StudentAccount account;

    public LoginSubscriber(StudentAccount account, List<AbstractLoginObserver> observers) {
        this.observers.addAll(observers);
        this.account = account;
    }

    public LoginSubscriber(StudentAccount account) {
        this.account = account;
    }

    public void add(AbstractLoginObserver observer) {
        observers.add(observer);
    }

    @Override
    public void onCompleted() {
        Log.i(LogTag, "login success...");
    }

    @Override
    public void onError(Throwable e) {
        for (AbstractLoginObserver observer : observers){
            observer.onNetworkFailure(R.string.thucab_other_failure);
        }
    }

    @Override
    public void onNext(CabObjectResponse response) {
        Object object = response.getData();
        if (response.isRequestSuccess() && object != null) {
            StudentDetails details = new Gson().fromJson(object.toString(), StudentDetails.class);
            for (AbstractLoginObserver observer : observers) {
                observer.onLoginSuccess(details, this.account);
            }
        } else {
            switch (response.getState()) {
                case IdFailure:
                    for (AbstractLoginObserver observer : observers) {
                        observer.onStudentIdFailure(response.getStateDetails());
                    }
                    break;
                case PasswordFailure:
                    for (AbstractLoginObserver observer : observers) {
                        observer.onPasswordFailure(response.getStateDetails());
                    }
                    break;
                case ActivateFailure:
                    for (AbstractLoginObserver observer : observers) {
                        observer.onActivateFailure(response.getStateDetails());
                    }
                    break;
                case JsonFailure:
                case OtherFailure:
                default:
                    for (AbstractLoginObserver observer : observers) {
                        observer.onNetworkFailure(response.getStateDetails());
                    }
            }
        }
    }

}
