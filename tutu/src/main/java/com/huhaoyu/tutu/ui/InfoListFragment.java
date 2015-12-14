package com.huhaoyu.tutu.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.ReservationInfoWrapper;
import com.huhaoyu.tutu.utils.MemoryWatcher;
import com.huhaoyu.tutu.utils.SnackbarManager;
import com.huhaoyu.tutu.utils.TutuConstants;
import com.huhaoyu.tutu.widget.InfoListAdater;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import mu.lab.thulib.thucab.PreferenceUtilities;
import mu.lab.thulib.thucab.ResvRecordStore;
import mu.lab.thulib.thucab.UserAccountManager;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;
import mu.lab.thulib.thucab.httputils.ResponseState;
import mu.lab.thulib.thucab.resvutils.ErrorTagManager;
import mu.lab.util.Log;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Info list fragment
 * Created by coderhuhy on 15/11/30.
 */
public class InfoListFragment extends Fragment
        implements Observer<List<ReservationRecord>>, RefreshableFragment {

    private static final String LogTag = InfoListFragment.class.getCanonicalName();

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private View fab;
    private long mTimeStamp = 0;

    private RefreshObserver mRefreshObserver;
    private ReservationInfoWrapper mInfos;
    private RecyclerView.Adapter mAdapter;
    private UserAccountManager mManager;

    private Observer<List<ReservationRecord>> realmSubscriber;

    public static InfoListFragment newInstance(FloatingActionsMenu fab, RefreshObserver callback) {
        InfoListFragment fragment = new InfoListFragment();
        fragment.mRefreshObserver = callback;
        fragment.mInfos = new ReservationInfoWrapper();
        fragment.mManager = UserAccountManager.getInstance();
        fragment.fab = fab;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new RecyclerViewMaterialAdapter(new InfoListAdater(mInfos, getActivity()));
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(mAdapter));
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);

        realmSubscriber = new Observer<List<ReservationRecord>>() {
            @Override
            public void onCompleted() {
                Log.i(LogTag, "fetch data from realm success...");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LogTag, e.getMessage(), e);
            }

            @Override
            public void onNext(List<ReservationRecord> records) {
                StudentDetails details = null;
                if (mManager.hasDetails()) {
                    try {
                        details = mManager.getDetails();
                    } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
                        Log.e(LogTag, error.toString(), error);
                    }
                }
                mInfos.refresh(records, details);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
        getDataFromRealm();
    }

    private void getDataFromRealm() {
        if (mManager.hasAccount()) {
            try {
                StudentAccount account = mManager.getAccount();
                ResvRecordStore.getResvRecordsFromRealm(account)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(realmSubscriber);
            } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
                Log.e(LogTag, error.toString(), error);
            }
        }
    }

    private void refreshRecords() {
        if (mManager.hasAccount()) {
            try {
                StudentAccount account = mManager.getAccount();
                if (mRefreshObserver != null) {
                    mRefreshObserver.onRefreshStart();
                }
                ResvRecordStore.getResvRecords(account, true, this);
            } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
                Log.e(LogTag, error.toString(), error);
            }
        }
    }

    @Override
    public void refresh(boolean force, RefreshObserver callback) {
        if (callback != null) {
            mRefreshObserver = callback;
        }
        if (force || System.currentTimeMillis() - this.mTimeStamp >= TutuConstants.Constants.REFRESH_INTERVAL) {
            refreshRecords();
        }
    }

    @Override
    public void onCompleted() {
        Log.i(LogTag, "refresh info list fragment success...");
    }

    @Override
    public void onError(Throwable e) {
        ResponseState resp = ErrorTagManager.toState(e);
        SnackbarManager manager = new SnackbarManager(fab).setContent(resp.getDetails());
        switch (resp) {
            case ActivateFailure:
                manager.setDuration(SnackbarManager.Duration.Long)
                        .setAction(R.string.tutu_activate, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String url = "http://cab.hs.lib.tsinghua.edu.cn/ClientWeb/xcus/ic/Login.aspx";
                                Uri uri = Uri.parse(url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                getActivity().startActivity(intent);
                            }
                        });
                if (mRefreshObserver != null) {
                    mRefreshObserver.onAccountNeedActivate();
                }
                break;
            case IdFailure:
            case PasswordFailure:
                if (mRefreshObserver != null) {
                    mRefreshObserver.onAccountError();
                }
                break;
            default:
                if (mRefreshObserver != null) {
                    mRefreshObserver.onRefreshComplete(false);
                }
        }
        manager.show();
    }

    @Override
    public void onNext(List<ReservationRecord> records) {
        StudentDetails details = null;
        if (mManager.hasDetails()) {
            try {
                details = mManager.getDetails();
            } catch (PreferenceUtilities.StudentAccountNotFoundError error) {
                Log.e(LogTag, error.toString(), error);
            }
        }
        mInfos.refresh(records, details);
        mRefreshObserver.onRefreshComplete(true);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        this.mTimeStamp = System.currentTimeMillis();
    }

    public void clear() {
        mInfos.clear();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher watcher = MemoryWatcher.getWatcher(getActivity());
        if (watcher != null) {
            watcher.watch(this);
        }
    }
}
