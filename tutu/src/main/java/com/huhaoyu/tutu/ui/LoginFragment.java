package com.huhaoyu.tutu.ui;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.utils.TutuConstants;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.ProgressView;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.CabUtilities;
import mu.lab.thulib.thucab.ThuCab;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;
import mu.lab.thulib.thucab.httputils.AbstractLoginObserver;
import mu.lab.thulib.thucab.httputils.LoginStateObserver;
import mu.lab.thulib.thucab.httputils.LoginSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Login fragment
 * Created by coderhuhy on 15/11/29.
 */
public class LoginFragment extends DialogFragment implements View.OnClickListener, AbstractLoginObserver {

    private static final String LogTag = LoginFragment.class.getCanonicalName();

    @Bind(R.id.student_id_et)
    MaterialEditText studentIdEt;
    @Bind(R.id.password_et)
    MaterialEditText passwordEt;
    @Bind(R.id.activate_button)
    Button activateButton;
    @Bind(R.id.login_button)
    Button loginButton;
    @Bind(R.id.refresh_progress)
    ProgressView refreshProgress;

    String username;
    String password;
    StudentAccount account;
    boolean isLogin;
    LoginStateObserver observer;
    ReservationListActivity context;
    DialogFragment instance;
    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_login, container);
        ButterKnife.bind(this, view);

        studentIdEt.setAutoValidate(false);
        passwordEt.setAutoValidate(false);

        if (isLogin) {
            username = account.getStudentId();
            password = account.getPassword();
            studentIdEt.setText(username);
            passwordEt.setText(password);
            studentIdEt.setEnabled(false);
            passwordEt.setEnabled(false);
            loginButton.setText(context.getString(R.string.tutu_logout));
        } else {
            studentIdEt.setEnabled(true);
            passwordEt.setEnabled(true);
            loginButton.setText(context.getString(R.string.tutu_login));
        }

        activateButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        return view;
    }

    public static DialogFragment newInstance(LoginStateObserver observer,
                                             ReservationListActivity context, StudentAccount account) {
        LoginFragment fragment = new LoginFragment();
        fragment.observer = observer;
        fragment.context = context;
        fragment.account = account;
        fragment.instance = fragment;
        fragment.isLogin = account != null;
        return fragment;
    }

    public static DialogFragment show(FragmentManager manager, ReservationListActivity context,
                                      LoginStateObserver observer) {
        return show(manager, context, observer, null);
    }

    public static DialogFragment show(FragmentManager manager, ReservationListActivity context,
                                      LoginStateObserver observer, StudentAccount account) {
        DialogFragment fragment = newInstance(observer, context, account);
        fragment.show(manager, LogTag);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    protected void clear() {
        ThuCab.clear(account);
        username = "";
        password = "";
        studentIdEt.setText("");
        passwordEt.setText("");
        studentIdEt.setEnabled(true);
        passwordEt.setEnabled(true);
        loginButton.setText(context.getString(R.string.tutu_login));
    }

    protected void openActivateUrl() {
        final String url = "http://cab.hs.lib.tsinghua.edu.cn/ClientWeb/xcus/ic/Login.aspx";
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    protected boolean validate(MaterialEditText editText) {
        boolean isValid = true;
        String content = editText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            isValid = false;
            editText.setError(context.getString(R.string.tutu_login_empty));
        }
        return isValid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                if (isLogin) {
                    CabUtilities.cabLogout();
                    clear();
                    context.clear();
                } else {
                    if (validate(studentIdEt) && validate(passwordEt)) {
                        refreshProgress.start();
                        username = studentIdEt.getText().toString();
                        password = passwordEt.getText().toString();
                        account = new StudentAccount(username, password);
                        LoginSubscriber subscriber = new LoginSubscriber(account);
                        subscriber.add(this);
                        subscriber.add(observer);
                        CabUtilities.cabLogin(new StudentAccount(username, password))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(subscriber);
                    }
                }
                break;
            case R.id.activate_button:
                openActivateUrl();
                break;
        }
    }

    @Override
    public void onLoginSuccess(StudentDetails details, StudentAccount account) {
        refreshProgress.stop();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                instance.dismissAllowingStateLoss();
            }
        }, TutuConstants.Constants.DELAY_DURATION);
    }

    @Override
    public void onStudentIdFailure(int resId) {
        refreshProgress.stop();
        studentIdEt.setError(context.getString(R.string.tutu_login_username_error));
    }

    @Override
    public void onPasswordFailure(int resId) {
        refreshProgress.stop();
        passwordEt.setError(context.getString(R.string.tutu_login_password_error));
    }

    @Override
    public void onActivateFailure(int resId) {
        refreshProgress.stop();
        studentIdEt.setError(context.getString(R.string.tutu_login_activate_error));
    }

    @Override
    public void onNetworkFailure(int resId) {
        refreshProgress.stop();
        studentIdEt.setError(context.getString(R.string.tutu_login_network_error));
    }

}
