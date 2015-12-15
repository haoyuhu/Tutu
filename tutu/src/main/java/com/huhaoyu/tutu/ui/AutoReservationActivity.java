package com.huhaoyu.tutu.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.AutoResvItemDecorator;
import com.huhaoyu.tutu.utils.SnackbarManager;
import com.huhaoyu.tutu.utils.TutuConstants;
import com.huhaoyu.tutu.widget.AutoSettingsAdapter;
import com.huhaoyu.tutu.widget.TemplateFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.AutoResvStore;
import mu.lab.thulib.thucab.PreferenceUtilities;
import mu.lab.thulib.thucab.UserAccountManager;
import mu.lab.thulib.thucab.entity.AutoReservationItem;
import mu.lab.thulib.thucab.entity.StudentAccount;
import com.huhaoyu.tutu.utils.SystemBarTintManager;
import rx.Observer;

public class AutoReservationActivity extends BaseActivity
        implements Observer<List<AutoReservationItem>>, View.OnClickListener {

    private static final String LogTag = AutoReservationActivity.class.getCanonicalName();
    public static final int RESULT_CODE_DEFAULT_VALUE = 1;
    public static final int RESULT_CODE_ACCOUNT_ERROR_VALUE = 0;
    private static final int[] weeks = {
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
    };

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.clear_button)
    TextView clearButton;
    @Bind(R.id.save_button)
    TextView saveButton;

    private AutoSettingsAdapter adapter;
    private List<AutoResvItemDecorator> list;
    private UserAccountManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_reservation);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        list = new ArrayList<>();
        for (int week : weeks) {
            list.add(AutoResvItemDecorator.newInstance(week));
        }
        manager = UserAccountManager.getInstance();
        if (manager.hasAccount()) {
            try {
                StudentAccount account = manager.getAccount();
                AutoResvStore.getAutoResvItems(account, this);
            } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
                onAccountError(error);
                finish();
            }
        }
    }

    private void initView() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.md_light_blue_800));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.tutu_black_transparent);
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            toolbar.setPadding(0, config.getPixelInsetTop(false), 0, config.getPixelInsetBottom());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new AutoSettingsAdapter(list, this);
        recyclerView.setAdapter(adapter);

        saveButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
    }

    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    protected void onAccountError(PreferenceUtilities.StudentAccountNotFoundError error) {
        Log.e(LogTag, error.toString(), error);
        SnackbarManager snackbar = new SnackbarManager(recyclerView);
        snackbar.setContent(R.string.tutu_reservation_account_failure).show();
        Intent intent = new Intent();
        intent.putExtra(TutuConstants.BundleKey.BUNDLE_KEY, RESULT_CODE_ACCOUNT_ERROR_VALUE);
        setResult(RESULT_OK, intent);
    }

    private boolean checkReservationNumber() {
        int count = 0;
        for (AutoResvItemDecorator decorator : list) {
            if (decorator.isEnable()) {
                ++count;
            }
        }
        return count <= TutuConstants.Constants.DEFAULT_AUTO_RESERVATION_NUMBER_LIMIT;
    }

    private void showWarning() {
        TemplateFragment.TutuFragmentBuilder builder = new TemplateFragment.TutuFragmentBuilder(this);
        builder.title(R.string.tutu_auto_warning)
                .titleBackground(R.drawable.shape_warning_background)
                .content(R.string.tutu_auto_warning_details)
                .rightButton(R.string.tutu_auto_confirm)
                .rightButtonBackground(R.drawable.selector_orange_clickable_button);
        builder.show();
    }

    @Override
    public void onClick(View v) {
        StudentAccount account = null;
        if (manager.hasAccount()) {
            try {
                account = manager.getAccount();
            } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
                onAccountError(error);
                finish();
            }
        }
        switch (v.getId()) {
            case R.id.clear_button:
                for (AutoResvItemDecorator decorator : list) {
                    decorator.clear();
                }
                adapter.notifyDataSetChanged();
                AutoResvStore.clear(account);
                finish();
                break;
            case R.id.save_button:
                if (checkReservationNumber()) {
                    List<AutoReservationItem> ret = new ArrayList<>();
                    for (AutoResvItemDecorator decorator : list) {
                        if (decorator.isEnable()) {
                            ret.add(decorator.toItem());
                        }
                    }
                    AutoResvStore.saveAutoResvItemsToRealm(ret, account);
                    finish();
                } else {
                    showWarning();
                }
                break;
        }
    }

    @Override
    public void onCompleted() {
        Log.i(LogTag, "get auto settings from realm success...");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(LogTag, e.getMessage(), e);
    }

    @Override
    public void onNext(List<AutoReservationItem> items) {
        for (AutoReservationItem item : items) {
            for (AutoResvItemDecorator decorator : list) {
                if (decorator.sameDay(item)) {
                    decorator.copy(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

}
