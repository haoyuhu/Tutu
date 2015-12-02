package com.huhaoyu.tutu.widget;

import java.util.List;

import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Filter callback
 * Created by coderhuhy on 15/12/2.
 */
public interface FilterCallback {

    void onConfirm(List<DateTimeUtilities.TimePeriod> periods, int minInterval);

    void onClear();

}
