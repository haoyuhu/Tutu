package com.huhaoyu.tutu.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.ReservationStatesWrapper;
import com.huhaoyu.tutu.utils.MemoryWatcher;
import com.huhaoyu.tutu.utils.TutuConstants;
import com.huhaoyu.tutu.widget.ReservationListAdapter;
import com.squareup.leakcanary.RefWatcher;

import java.util.Arrays;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import mu.lab.thulib.thucab.CabUtilities;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.CabFilter;
import mu.lab.thulib.thucab.entity.ReservationState;
import mu.lab.util.Log;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationListFragment extends Fragment
        implements Observer<List<ReservationState>>, RefreshableFragment {

    private static final String LogTag = ReservationListFragment.class.getCanonicalName();

    private static final int DEFAULT_MIN_INTERVAL_IN_HOUR = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ReservationStatesWrapper mStates;

    private DateTimeUtilities.DayRound mRound;
    private CabFilter mFilter;
    private RefreshObserver mRefreshObserver;
    private long mTimeStamp = 0;

    public static ReservationListFragment newInstance(DateTimeUtilities.DayRound round, RefreshObserver callback) {
        ReservationListFragment fragment = new ReservationListFragment();
        fragment.mRound = round;
        fragment.mRefreshObserver = callback;
        fragment.mFilter = new CabFilter(null, DEFAULT_MIN_INTERVAL_IN_HOUR);
        fragment.mStates = new ReservationStatesWrapper(round);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new RecyclerViewMaterialAdapter(new ReservationListAdapter(mStates, getActivity()));
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(mAdapter));
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher watcher = MemoryWatcher.getWatcher(getActivity());
        if (watcher != null) {
            watcher.watch(this);
        }
    }

    public void resetFilter(List<DateTimeUtilities.TimePeriod> periods, int minInterval) {
        this.mFilter.refresh(periods, minInterval);
    }

    public CabFilter getFilter() {
        return this.mFilter;
    }

    private void refreshState(CabFilter filter) {
        CabUtilities.queryRoomState(Arrays.asList(mRound), filter, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void refresh(boolean force, RefreshObserver callback) {
        if (callback != null) {
            mRefreshObserver = callback;
        }
        if (force || System.currentTimeMillis() - this.mTimeStamp >= TutuConstants.Constants.REFRESH_INTERVAL) {
            if (mRefreshObserver != null) {
                mRefreshObserver.onRefreshStart();
            }
            refreshState(this.mFilter);
        }
    }

    @Override
    public void onCompleted() {
        Log.i(LogTag, "fetch reservation states success...");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(LogTag, e.getMessage(), e);
        if (mRefreshObserver != null) {
            mRefreshObserver.onRefreshComplete(false);
        }
    }

    @Override
    public void onNext(List<ReservationState> states) {
        mStates.refresh(states);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        this.mTimeStamp = System.currentTimeMillis();
        if (mRefreshObserver != null) {
            mRefreshObserver.onRefreshComplete(true);
        }
    }
}
