package com.huhaoyu.tutu.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.widget.FilterCallback;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Slider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.CabFilter;

/**
 * Filter fragment
 * Created by coderhuhy on 15/12/1.
 */
public class FilterFragment extends LeakCanaryBottomFragment
        implements View.OnClickListener, Slider.OnPositionChangeListener {

    @Bind({R.id.morning_cb, R.id.afternoon_cb, R.id.evening_cb})
    CheckBox[] periodCheckbox;
    @Bind(R.id.picked_interval_tv)
    TextView pickedIntervalTv;
    @Bind(R.id.picked_interval_sl)
    Slider pickedIntervalSl;
    @Bind(R.id.clear_button)
    Button clearButton;
    @Bind(R.id.confirm_button)
    Button confirmButton;

    private CabFilter filter;
    private FilterCallback callback;

    public static FilterFragment newInstance(CabFilter old, FilterCallback callback) {
        FilterFragment fragment = new FilterFragment();
        fragment.filter = old;
        fragment.callback = callback;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (filter == null) {
            filter = new CabFilter(null, CabFilter.DefaultMinInterval);
        }
        for (DateTimeUtilities.TimePeriod period : filter.getPeriods()) {
            int index = period.ordinal();
            if (index >= 0 && index < periodCheckbox.length) {
                periodCheckbox[period.ordinal()].setCheckedImmediately(true);
            }
        }
        pickedIntervalSl.setValue(filter.getIntervalInHour(), true);
        pickedIntervalSl.setOnPositionChangeListener(this);
        String pattern = getContext().getString(R.string.tutu_filter_interval_picked);
        pickedIntervalTv.setText(String.format(pattern, filter.getIntervalInHour()));
        clearButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_button:
                if (callback != null) {
                    callback.onClear();
                }
                this.dismiss();
                break;
            case R.id.confirm_button:
                if (callback != null) {
                    DateTimeUtilities.TimePeriod[] list = DateTimeUtilities.TimePeriod.values();
                    List<DateTimeUtilities.TimePeriod> periods = new ArrayList<>();
                    int min;
                    for (int i = 0; i != periodCheckbox.length; ++i) {
                        if (periodCheckbox[i].isChecked()) {
                            periods.add(list[i]);
                        }
                    }
                    if (periods.isEmpty()) {
                        periods = Arrays.asList(list[0], list[1], list[2]);
                    }
                    min = pickedIntervalSl.getValue();
                    callback.onConfirm(periods, min);
                }
                this.dismiss();
                break;
        }
    }

    @Override
    public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
        String pattern = getContext().getString(R.string.tutu_filter_interval_picked);
        pickedIntervalTv.setText(String.format(pattern, newValue));
    }

}
