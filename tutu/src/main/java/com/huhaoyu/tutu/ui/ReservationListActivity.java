package com.huhaoyu.tutu.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.utils.DrawerManager;
import com.huhaoyu.tutu.utils.DrawerManagerImpl;
import com.huhaoyu.tutu.utils.SnackbarManager;
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
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;
import mu.lab.thulib.thucab.httputils.LoginStateObserver;
import mu.lab.tufeedback.common.TUFeedback;
import mu.lab.util.Log;

public class ReservationListActivity extends BaseActivity
        implements View.OnClickListener, RefreshCallback {

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
    private List<ReservationListFragment> fragments = new ArrayList<>();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) ||
                super.onOptionsItemSelected(item);
    }

    private void setViewPager() {
        String[] tabs = getResources().getStringArray(R.array.tutu_resv_tab_title_array);
        List<FragmentTitle> list = new ArrayList<>();
        DateTimeUtilities.DayRound[] rounds = DateTimeUtilities.DayRound.values();
        for (int i = 0; i != 3; ++i) {
            ReservationListFragment fragment = ReservationListFragment.newInstance(rounds[i], this);
            FragmentTitle item = new FragmentTitle(fragment, tabs[i]);
            list.add(item);
            fragments.add(fragment);
        }
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
                        break;
                    case R.id.drawer_auto_reservation:
                        break;
                    case R.id.drawer_reservation_info:
                        materialViewPager.getViewPager().setCurrentItem(MY_RESERVATION_PAGE, true);
                        break;
                    case R.id.drawer_seat_state:
                        break;
                    case R.id.drawer_switch_account:
                        break;
                    case R.id.drawer_logout:
                        break;
                    case R.id.drawer_feedback:
                        TUFeedback.openFeedbackActivity(ReservationListActivity.this);
                        break;
                    case R.id.drawer_check_update:
                        checkUmengAppUpdate(fabGroup);
                        break;
                    case R.id.drawer_help:
                        break;
                    case R.id.drawer_donation:
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
        StudentAccount account = null;
        try {
            account = PreferenceUtilities.getStudentAccount();
        } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
            Log.e(LogTag, error.toString(), error);
        }
        drawerManager = new DrawerManagerImpl(account, navigation, this);
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
    }

    private void setFab() {
        if (PreferenceUtilities.hasStudentAccount()) {
            fabSmartReservation.setVisibility(View.VISIBLE);
            fabAutoReservation.setVisibility(View.VISIBLE);
        } else {
            fabSmartReservation.setVisibility(View.GONE);
            fabAutoReservation.setVisibility(View.GONE);
        }
        fabRefresh.setOnClickListener(this);
        fabFilter.setOnClickListener(this);
        fabSmartReservation.setOnClickListener(this);
        fabAutoReservation.setOnClickListener(this);
    }

    private void setRefresher() {
        refresherManager = RefresherManager.newInstance(refreshProgress);
    }

    private void setUmengComponent() {
        TUFeedback.start();
        PushAgent pushAgent = PushAgent.getInstance(this);
        pushAgent.enable();
        addUmengAlias();
        UmengMessageHandler handler = new UmengMessageHandler() {
            @Override
            public void handleMessage(Context context, UMessage uMessage) {
                if (!TUFeedback.isFeedbackMessage(getApplicationContext(), uMessage.custom)) {
                    // TODO: handle custom messages
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
        Today(R.color.blue, "http://pre07.deviantart.net/5717/th/pre/f/2015/030/e/c/_minflat__android_l_wallpaper_moonshine__4k__by_dakoder-d7uzmrv.jpg"),
        Tomorrow(R.color.cyan, "http://i0.wp.com/androidlive.it/wp-content/uploads/2014/08/image_new-103.jpg"),
        DayAfterTomorrow(R.color.red, "http://i2.wp.com/androidlive.it/wp-content/uploads/2014/08/image_new-92.jpg"),
        MyReservation(R.color.purple, "http://www.lirent.net/wp-content/uploads/2014/10/Android-Lollipop-wallpapers-p-800x500.png");
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

    private void refresh() {
        int count = DateTimeUtilities.DayRound.values().length;
        int current = materialViewPager.getViewPager().getCurrentItem();
        if (current < count) {
            fragments.get(current).refresh(true, this);
        }
    }

    @Override
    public void onRefreshComplete(boolean result) {
        refresherManager.stop();
        if (!result) {
            SnackbarManager manager = new SnackbarManager(fabGroup);
            manager.setContent(R.string.tutu_refresh_reservation_failure).show();
        }
    }

    @Override
    public void onRefreshStart() {
        refresherManager.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_refresh:
                refresh();
                fabGroup.collapse();
                break;
            case R.id.fab_filter:
                fabGroup.collapse();
                break;
            case R.id.fab_smart_reservation:
                fabGroup.collapse();
                break;
            case R.id.fab_auto_reservation:
                fabGroup.collapse();
                break;
        }
    }

    private LoginStateObserver loginObserver = new LoginStateObserver() {
        @Override
        public void onLoginSuccess(StudentDetails details, StudentAccount account) {
            super.onLoginSuccess(details, account);
            drawerManager.onLogin(account);
            setFab();
        }

        @Override
        public void onStudentIdFailure(int resId) {
            SnackbarManager manager = new SnackbarManager(fabGroup);
            manager.setContent(resId).show();
        }

        @Override
        public void onPasswordFailure(int resId) {
            SnackbarManager manager = new SnackbarManager(fabGroup);
            manager.setContent(resId).show();
        }

        @Override
        public void onNetworkFailure(int resId) {
            SnackbarManager manager = new SnackbarManager(fabGroup);
            manager.setContent(resId).show();
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
                StudentAccount account = PreferenceUtilities.getStudentAccount();
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
