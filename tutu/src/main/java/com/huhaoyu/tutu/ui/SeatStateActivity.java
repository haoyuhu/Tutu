package com.huhaoyu.tutu.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.utils.SnackbarManager;
import com.huhaoyu.tutu.widget.SeatAdapter;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thuseat.SeatState;
import mu.lab.thulib.thuseat.SeatUtilities;
import mu.lab.tufeedback.utils.SystemBarTintManager;
import rx.Observer;

public class SeatStateActivity extends BaseActivity implements Observer<SeatState> {

    private static final String LogTag = SeatStateActivity.class.getCanonicalName();
    private static final long REFRESH_INTERVAL = 10 * CabConstants.DateTimeConstants.SECOND_OF_MINUTE
            * CabConstants.DateTimeConstants.MILLIS_OF_SECOND;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refresh_progress)
    ProgressView refreshProgress;
    @Bind(R.id.refresh_time_tv)
    TextView refreshTimeTv;

    List<SeatState> list = new ArrayList<>();
    SeatAdapter adapter;
    MenuItem refreshItem;
    long refreshTime = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_state);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - refreshTime >= REFRESH_INTERVAL) {
            refresh(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refresh(true);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_seat_state, menu);
        refreshItem = menu.findItem(R.id.menu_refresh);
        return super.onCreateOptionsMenu(menu);
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

        adapter = new SeatAdapter(list, this);
        recyclerView.setAdapter(adapter);

        refreshTimeTv.setText(R.string.tutu_seat_loading);
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

    private void refresh(boolean update) {
        refreshProgress.start();
        if (refreshItem != null) {
            refreshItem.setEnabled(false);
        }
        refreshTimeTv.setText(R.string.tutu_seat_loading);
        list.clear();
        SeatUtilities.getSeatState(update, this);
    }

    @Override
    public void onCompleted() {
        Log.i(LogTag, "fetch seat states completed...");
        refreshProgress.stop();
        if (refreshItem != null) {
            refreshItem.setEnabled(true);
        }
        refreshTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(refreshTime);
        String time = getString(R.string.tutu_seat_refresh) + " "
                + DateTimeUtilities.formatReservationDate(calendar, getString(R.string.tutu_seat_refresh_pattern));
        refreshTimeTv.setText(time);
        refreshTime = System.currentTimeMillis();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e(LogTag, e.getMessage(), e);
        refreshProgress.stop();
        if (refreshItem != null) {
            refreshItem.setEnabled(true);
        }
        refreshTimeTv.setText(R.string.tutu_seat_load_failure);
        SnackbarManager manager = new SnackbarManager(recyclerView);
        manager.setContent(R.string.tutu_seat_load_snackbar_failure).show();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNext(SeatState seatState) {
        list.add(seatState);
    }

}
