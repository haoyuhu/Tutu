package com.huhaoyu.tutu.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.utils.SnackbarManager;
import com.huhaoyu.tutu.utils.TutuConstants;
import com.huhaoyu.tutu.widget.RecommendationAdapter;
import com.huhaoyu.tutu.widget.ReservationObserver;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.RecommendResv;
import com.huhaoyu.tutu.utils.SystemBarTintManager;

public class RecommendationActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.empty_recommendation_tv)
    TextView emptyRecommendationTv;

    public static final int RESULT_CODE_NONE = 1;
    public static final int RESULT_CODE_ACCOUNT_ERROR = 2;
    public static final int RESULT_CODE_SUCCESS = 3;

    private List<RecommendResv> list;
    private DateTimeUtilities.DayRound round;
    private RecommendationAdapter adapter;

    private ReservationObserver observer = new ReservationObserver() {
        @Override
        public void onAccountError() {
            Intent intent = new Intent();
            intent.putExtra(TutuConstants.BundleKey.BUNDLE_KEY, RESULT_CODE_ACCOUNT_ERROR);
            setResult(RESULT_OK, intent);
        }

        @Override
        public void onNetworkError() {
            Intent intent = new Intent();
            intent.putExtra(TutuConstants.BundleKey.BUNDLE_KEY, RESULT_CODE_NONE);
            setResult(RESULT_OK, intent);
        }

        @Override
        public void onReservationSuccess() {
            Intent intent = new Intent();
            intent.putExtra(TutuConstants.BundleKey.BUNDLE_KEY, RESULT_CODE_SUCCESS);
            intent.putExtra(TutuConstants.BundleKey.EXTRA_BUNDLE_KEY, round.ordinal());
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
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
        Intent intent = getIntent();
        list = intent.getParcelableArrayListExtra(TutuConstants.BundleKey.BUNDLE_KEY);
        int increment = intent.getIntExtra(TutuConstants.BundleKey.EXTRA_BUNDLE_KEY, -1);
        if (list == null || increment == -1) {
            SnackbarManager manager = new SnackbarManager(recyclerView);
            manager.setContent(R.string.tutu_recommendation_unknown_error).show();
            finish();
        }
        round = DateTimeUtilities.DayRound.values()[increment];
    }

    private void initView() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.tutu_purple_transparent));
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

        adapter = new RecommendationAdapter(list, round, this);
        recyclerView.setAdapter(adapter);

        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyRecommendationTv.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyRecommendationTv.setVisibility(View.GONE);
        }
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

    public void openReservationFragment(RecommendResv recommend) {
        ReservationFragment fragment = ReservationFragment.newInstance(recommend, round, observer);
        fragment.show(getSupportFragmentManager(), R.id.bottom_sheet);
    }

}
