package com.huhaoyu.tutu.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.huhaoyu.tutu.BuildConfig;
import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.ReservationRecordDecorator;
import com.huhaoyu.tutu.entity.ReservationStateDecorator;
import com.huhaoyu.tutu.utils.DrawerManager;
import com.huhaoyu.tutu.utils.DrawerManagerImpl;
import com.huhaoyu.tutu.utils.RefresherManager;
import com.huhaoyu.tutu.utils.SnackbarManager;
import com.huhaoyu.tutu.utils.TutuConstants;
import com.huhaoyu.tutu.backend.TutuNotificationManager;
import com.huhaoyu.tutu.widget.FilterCallback;
import com.huhaoyu.tutu.widget.ReservationObserver;
import com.huhaoyu.tutu.widget.TemplateFragment;
import com.rey.material.widget.ProgressView;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.entity.UMessage;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.PreferenceUtilities;
import mu.lab.thulib.thucab.ThuCab;
import mu.lab.thulib.thucab.UserAccountManager;
import mu.lab.thulib.thucab.entity.CabFilter;
import mu.lab.thulib.thucab.entity.RecommendResv;
import mu.lab.thulib.thucab.entity.ReservationState;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;
import mu.lab.thulib.thucab.httputils.CabHttpClient;
import mu.lab.thulib.thucab.httputils.LoginStateObserver;
import mu.lab.tufeedback.common.TUFeedback;
import com.huhaoyu.tutu.utils.SystemBarTintManager;
import mu.lab.util.Log;

public class ReservationListActivity extends BaseActivity
        implements View.OnClickListener {

    private static final String LogTag = ReservationListActivity.class.getCanonicalName();
    @Bind(R.id.material_view_pager)
    MaterialViewPager materialViewPager;
    @Bind(R.id.fab_refresh)
    FloatingActionButton fabRefresh;
    @Bind(R.id.fab_filter)
    FloatingActionButton fabFilter;
    @Bind(R.id.fab_auto_reservation)
    FloatingActionButton fabAutoReservation;
    @Bind(R.id.fab_smart_reservation)
    FloatingActionButton fabSmartReservation;
    @Bind(R.id.fab_group)
    FloatingActionsMenu fabGroup;
    @Bind(R.id.refresh_progress)
    ProgressView refreshProgress;
    @Bind(R.id.navigation)
    NavigationView navigation;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerManager drawerManager;
    private RefresherManager refresherManager;
    private List<RefreshableFragment> fragments = new ArrayList<>();
    private UserAccountManager accountManager = UserAccountManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);
        ButterKnife.bind(this);

        setUmengComponent();
        setViewPager();
        setDrawerAndToggle();
        setDrawerHeaderAndMenu();
        setToolbar();
        setFab();
        setRefresher();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawerManager.onResume();

        for (RefreshableFragment fragment : fragments) {
            fragment.refresh(false, refreshObserver);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) ||
                super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TutuConstants.RequestCode.REQUEST_CODE_RECOMMENDATION && resultCode == RESULT_OK) {
            int result = data.getIntExtra(TutuConstants.BundleKey.BUNDLE_KEY, -1);
            if (result == -1) return;
            switch (result) {
                case RecommendationActivity.RESULT_CODE_ACCOUNT_ERROR:
                    ReservationListActivity.this.clear();
                    openLoginFragment();
                    break;
                case RecommendationActivity.RESULT_CODE_SUCCESS:
                    int index = data.getIntExtra(TutuConstants.BundleKey.EXTRA_BUNDLE_KEY, -1);
                    int myInfoPos = fragments.size() - 1;
                    if (index >= 0 && index < myInfoPos) {
                        refresh(index);
                        refresh(myInfoPos);
                    }
                    break;
            }
        }
        if (requestCode == TutuConstants.RequestCode.REQUEST_CODE_AUTO_SETTINGS && resultCode == RESULT_OK) {
            int result = data.getIntExtra(TutuConstants.BundleKey.BUNDLE_KEY,
                    AutoReservationActivity.RESULT_CODE_DEFAULT_VALUE);
            if (result == AutoReservationActivity.RESULT_CODE_ACCOUNT_ERROR_VALUE) {
                ReservationListActivity.this.clear();
                openLoginFragment();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setViewPager() {
        String[] tabs = getResources().getStringArray(R.array.tutu_resv_tab_title_array);
        List<FragmentTitle> list = new ArrayList<>();
        DateTimeUtilities.DayRound[] rounds = DateTimeUtilities.DayRound.values();
        for (int i = 0; i != 3; ++i) {
            ReservationListFragment fragment = ReservationListFragment.newInstance(rounds[i], refreshObserver);
            FragmentTitle item = new FragmentTitle(fragment, tabs[i]);
            list.add(item);
            fragments.add(fragment);
        }
        InfoListFragment fragment = InfoListFragment.newInstance(fabGroup, refreshObserver);
        FragmentTitle info = new FragmentTitle(fragment, tabs[3]);
        list.add(info);
        fragments.add(fragment);

        materialViewPager.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                final int myReservationPos = fragments.size() - 1;
                fabFilter.setVisibility(position == myReservationPos ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        materialViewPager.getViewPager().setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), list));
        materialViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                TabImageLoader[] loaders = TabImageLoader.values();
                int color = loaders[page].colorId;
                String url = loaders[page].url;
                return HeaderDesign.fromColorResAndUrl(color, url);
            }
        });
        materialViewPager.getViewPager().setOffscreenPageLimit(materialViewPager.getViewPager().getAdapter().getCount());
        materialViewPager.getPagerTitleStrip().setViewPager(materialViewPager.getViewPager());

        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                final int DEFAULT_PAGE = 0;
                final int MY_RESERVATION_PAGE = 3;
                switch (item.getItemId()) {
                    case R.id.drawer_home:
                        materialViewPager.getViewPager().setCurrentItem(DEFAULT_PAGE, true);
                        break;
                    case R.id.drawer_smart_reservation:
                        openSmartReservationFragment();
                        break;
                    case R.id.drawer_auto_reservation:
                        openAutoReservationActivity();
                        break;
                    case R.id.drawer_reservation_info:
                        materialViewPager.getViewPager().setCurrentItem(MY_RESERVATION_PAGE, true);
                        break;
                    case R.id.drawer_seat_state:
                        openCheckSeatsActivity();
                        break;
                    case R.id.drawer_switch_account:
                        clear();
                        openLoginFragment();
                        break;
                    case R.id.drawer_logout:
                        clear();
                        break;
                    case R.id.drawer_feedback:
                        openFeedbackActivity();
                        break;
                    case R.id.drawer_check_update:
                        checkUmengAppUpdate(fabGroup);
                        break;
                    case R.id.drawer_help:
                        openHelpFragment();
                        break;
                    case R.id.drawer_donation:
                        openDonationFragment();
                        break;
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

    private void setDrawerAndToggle() {
        final int NULL_RES_ID = 0;
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, NULL_RES_ID, NULL_RES_ID);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void setDrawerHeaderAndMenu() {
        drawerManager = new DrawerManagerImpl(navigation, this);
        drawerManager.onInit();
    }

    private void setToolbar() {
        final String NULL_TITLE = "";
        setTitle(NULL_TITLE);
        Toolbar toolbar = materialViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }
        if (toolbar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.tutu_black_transparent);
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            toolbar.setPadding(0, config.getPixelInsetTop(false), 0, config.getPixelInsetBottom());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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

    private void setFab() {
        refreshFab();
        fabRefresh.setOnClickListener(this);
        fabFilter.setOnClickListener(this);
        fabSmartReservation.setOnClickListener(this);
        fabAutoReservation.setOnClickListener(this);
    }

    public void refreshFab() {
        if (accountManager.hasAccount()) {
            fabSmartReservation.setVisibility(View.VISIBLE);
            fabAutoReservation.setVisibility(View.VISIBLE);
        } else {
            fabSmartReservation.setVisibility(View.GONE);
            fabAutoReservation.setVisibility(View.GONE);
        }
    }

    private void setRefresher() {
        refresherManager = RefresherManager.newInstance(refreshProgress);
    }

    private void setUmengComponent() {
        TUFeedback.start();
        PushAgent pushAgent = PushAgent.getInstance(this);
        pushAgent.enable();
        pushAgent.setDebugMode(BuildConfig.DEBUG);
        addUmengAlias();
        UmengMessageHandler handler = new UmengMessageHandler() {
            @Override
            public void handleMessage(Context context, UMessage uMessage) {
                if (!TUFeedback.isFeedbackMessage(getApplicationContext(), uMessage.custom)) {
                    boolean light = uMessage.play_lights;
                    boolean sound = uMessage.play_sound;
                    boolean vibrate = uMessage.play_vibrate;
                    String ticker = uMessage.ticker;
                    String content = uMessage.text;
                    String type = uMessage.after_open;
                    String custom = uMessage.custom;
                    TutuNotificationManager.getInstance().notifyMessage(light, vibrate, sound,
                            ticker, content, type, custom);
                }
            }
        };
        pushAgent.setMessageHandler(handler);
        UmengUpdateAgent.update(this);
        String device_token = UmengRegistrar.getRegistrationId(this);
        Log.d(LogTag, "device_token: " + device_token);
    }

    protected void addUmengAlias() {
        new UmengAliasThread(this).start();
    }

    enum TabImageLoader {
        Today(R.color.blue, TutuConstants.Constants.background[0]),
        Tomorrow(R.color.cyan, TutuConstants.Constants.background[1]),
        DayAfterTomorrow(R.color.red, TutuConstants.Constants.background[2]),
        MyReservation(R.color.purple, TutuConstants.Constants.background[3]);
        int colorId;
        String url;

        TabImageLoader(int colorId, String url) {
            this.colorId = colorId;
            this.url = url;
        }
    }

    /**
     * @param view if view is fab or fam, then animating fab/fam
     */
    private void checkUmengAppUpdate(final View view) {
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setDeltaUpdate(true);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        UmengUpdateAgent.showUpdateDialog(getApplicationContext(), updateInfo);
                        break;
                    case UpdateStatus.No: // has no update
                        new SnackbarManager(view).setContent(R.string.tutu_already_up_to_date).show();
                        break;
                    case UpdateStatus.NoneWifi: // none wifi
                        new SnackbarManager(view).setContent(R.string.tutu_please_connect_wifi)
                                .setAction(R.string.tutu_snackbar_reset_wifi, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                        startActivity(intent);
                                    }
                                }).show();
                        break;
                    case UpdateStatus.Timeout: // time out
                        new SnackbarManager(view).setContent(R.string.tutu_update_timeout).show();
                        break;
                    default:
                        Log.w(LogTag, "umeng update returns unexpected update status");
                        break;
                }
                UmengUpdateAgent.setUpdateAutoPopup(true);
                UmengUpdateAgent.setUpdateListener(null);
            }
        });
        UmengUpdateAgent.forceUpdate(getApplicationContext());
    }

    private void refreshAll() {
        for (RefreshableFragment fragment : fragments) {
            fragment.refresh(true, refreshObserver);
        }
    }

    private void refresh() {
        int current = materialViewPager.getViewPager().getCurrentItem();
        refresh(current);
    }

    private void refresh(int index) {
        fragments.get(index).refresh(true, refreshObserver);
    }

    private void refreshInfo() {
        final int myInfoPos = 3;
        fragments.get(myInfoPos).refresh(true, refreshObserver);
        drawerManager.onResume();
    }

    public void openLoginFragment() {
        StudentAccount account = null;
        try {
            account = accountManager.getAccount();
        } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
            Log.e(LogTag, error.toString(), error);
        }
        LoginFragment.show(getFragmentManager(), this, loginObserver, account);
    }

    public void openFilterFragment() {
        int current = materialViewPager.getViewPager().getCurrentItem();
        int count = DateTimeUtilities.DayRound.values().length;
        if (current >= 0 && current < count) {
            final ReservationListFragment fragment = (ReservationListFragment) fragments.get(current);
            CabFilter old = fragment.getFilter();
            FilterFragment filterFrg = FilterFragment.newInstance(old, new FilterCallback() {
                @Override
                public void onConfirm(List<DateTimeUtilities.TimePeriod> periods, int minInterval) {
                    fragment.resetFilter(periods, minInterval);
                    refresh();
                }

                @Override
                public void onClear() {
                    fragment.resetFilter(null, CabFilter.DefaultMinInterval);
                    refresh();
                }
            });
            filterFrg.show(getSupportFragmentManager(), R.id.bottom_sheet);
        }
    }

    public void openReservationFragment(ReservationStateDecorator state, ReservationState.TimeRange range) {
        int current = materialViewPager.getViewPager().getCurrentItem();
        DateTimeUtilities.DayRound[] rounds = DateTimeUtilities.DayRound.values();
        if (current >= 0 && current < rounds.length) {
            DateTimeUtilities.DayRound round = rounds[current];
            ReservationFragment fragment = ReservationFragment.newInstance(state, range, round, reservationObserver);
            fragment.show(getSupportFragmentManager(), R.id.bottom_sheet);
        }
    }

    public void openAutoReservationActivity() {
        startOtherActivityForResult(AutoReservationActivity.class, TutuConstants.RequestCode.REQUEST_CODE_AUTO_SETTINGS);
    }

    public void openSmartReservationFragment() {
        SmartReservationFragment fragment = SmartReservationFragment.newInstance(reservationObserver);
        fragment.show(getSupportFragmentManager(), R.id.bottom_sheet);
    }

    public void openModificationFragment(ReservationRecordDecorator record) {
        ModificationFragment fragment = ModificationFragment.newInstance(record, reservationObserver);
        fragment.show(getSupportFragmentManager(), R.id.bottom_sheet);
    }

    public void openDeletionFragment(ReservationRecordDecorator record) {
        DeletionFragment fragment = DeletionFragment.newInstance(record, reservationObserver);
        fragment.show(getSupportFragmentManager(), R.id.bottom_sheet);
    }

    public void openRecommendationActivity(List<RecommendResv> list, DateTimeUtilities.DayRound round, long priorityFilter) {
        ArrayList<RecommendResv> data = new ArrayList<>();
        for (RecommendResv resv : list) {
            if (resv.getPriority() <= priorityFilter) {
                data.add(resv);
            } else {
                break;
            }
        }
        int increment = round.ordinal();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(TutuConstants.BundleKey.BUNDLE_KEY, data);
        bundle.putInt(TutuConstants.BundleKey.EXTRA_BUNDLE_KEY, increment);
        startOtherActivityForResult(RecommendationActivity.class, bundle, TutuConstants.RequestCode.REQUEST_CODE_RECOMMENDATION);
    }

    public void openRecommendationActivity(List<RecommendResv> list, DateTimeUtilities.DayRound round) {
        openRecommendationActivity(list, round, TutuConstants.Constants.DEFAULT_PRIORITY_FILTER_VALUE);
    }

    public void openHelpFragment() {
        HelperFragment fragment = HelperFragment.newInstance(this);
        fragment.show();
    }

    public void openCheckSeatsActivity() {
        startOtherActivity(SeatStateActivity.class);
    }

    public void openDonationFragment() {
        TemplateFragment.TutuFragmentBuilder builder = new TemplateFragment.TutuFragmentBuilder(this);
        builder.title(R.string.tutu_donation)
                .titleBackground(R.drawable.shape_login_background)
                .content(R.string.tutu_donation_details)
                .rightButton(R.string.tutu_donate)
                .rightButtonBackground(R.drawable.selector_blue_clickable_button)
                .leftButton(R.string.tutu_donation_quit)
                .callback(new TemplateFragment.ButtonClickCallback() {
                    @Override
                    public void onLeftButtonClicked(View view) {
                    }

                    @Override
                    public void onRightButtonClicked(View view) {
                        String url = "http://www.huhaoyu.com/tsinghua-tutu/";
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
        builder.show();
    }

    public void openFeedbackActivity() {
        TUFeedback.openFeedbackActivity(ReservationListActivity.this);
    }

    public void clear() {
        try {
            StudentAccount account = accountManager.getAccount();
            ThuCab.clear(account);
        } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
            Log.e(LogTag, error.toString(), error);
        }
        final int myReservationPos = fragments.size() - 1;
        CabHttpClient.cancel();
        drawerManager.onClearUp();
        refreshFab();
        ((InfoListFragment) fragments.get(myReservationPos)).clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_refresh:
                refresh();
                fabGroup.collapse();
                break;
            case R.id.fab_filter:
                openFilterFragment();
                fabGroup.collapse();
                break;
            case R.id.fab_smart_reservation:
                openSmartReservationFragment();
                fabGroup.collapse();
                break;
            case R.id.fab_auto_reservation:
                openAutoReservationActivity();
                fabGroup.collapse();
                break;
        }
    }

    private ReservationObserver reservationObserver = new ReservationObserver() {
        @Override
        public void onAccountError() {
            ReservationListActivity.this.clear();
            openLoginFragment();
        }

        @Override
        public void onNetworkError() {
        }

        @Override
        public void onReservationSuccess() {
            refresh();
            final int myInfoPos = 3;
            int current = materialViewPager.getViewPager().getCurrentItem();
            if (current != myInfoPos) {
                refreshInfo();
            }
        }
    };

    private RefreshObserver refreshObserver = new RefreshObserver() {
        @Override
        public void onRefreshComplete(boolean result) {
            refresherManager.stop();
            if (!result) {
                SnackbarManager manager = new SnackbarManager(fabGroup);
                manager.setContent(R.string.tutu_refresh_reservation_failure).show();
            }
        }

        @Override
        public void onAccountError() {
            onAccountNeedActivate();
            openLoginFragment();
        }

        @Override
        public void onAccountNeedActivate() {
            refresherManager.stop();
            ReservationListActivity.this.clear();
        }

        @Override
        public void onRefreshStart() {
            refresherManager.start();
        }
    };

    private LoginStateObserver loginObserver = new LoginStateObserver() {
        @Override
        public void onLoginSuccess(StudentDetails details, StudentAccount account) {
            super.onLoginSuccess(details, account);
            final int myReservationPos = fragments.size() - 1;
            drawerManager.onLogin(account);
            refreshFab();
            fragments.get(myReservationPos).refresh(true, refreshObserver);
        }
    };

    protected class FragmentPagerAdapter extends FragmentStatePagerAdapter {

        protected List<FragmentTitle> list;

        public FragmentPagerAdapter(FragmentManager fm, List<FragmentTitle> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position).fragment;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position).title;
        }
    }

    protected class FragmentTitle {
        Fragment fragment;
        String title;

        public FragmentTitle(Fragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }
    }

    protected class UmengAliasThread extends Thread {

        private final static String ALIAS_TYPE = "username";
        private Context context;

        public UmengAliasThread(Context context) {
            super();
            this.context = context;
        }

        @Override
        public void run() {
            super.run();
            try {
                StudentAccount account = accountManager.getAccount();
                PushAgent pushAgent = PushAgent.getInstance(context);
                pushAgent.addAlias(account.getStudentId(), ALIAS_TYPE);
            } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
                Log.e(LogTag, "username is null, cannot upload username as alias...", error);
            } catch (Exception e) {
                Log.e(LogTag, e.getMessage(), e);
            }
        }
    }

}
