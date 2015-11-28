package com.huhaoyu.tutu.entity;

import android.content.Context;

import java.util.List;

import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * ReservationSummary
 * Created by coderhuhy on 15/11/23.
 */
public interface ReservationSummary {

    /**
     * @param pattern Pattern format date and time
     * @return String of date time formatted
     */
    String getRefreshDateTime(String pattern);
    /**
     * @param context Context
     * @return Date format with default
     */
    String getDate(Context context);

    /**
     * @param pattern Pattern
     * @return Date format with pattern
     */
    String getDate(String pattern);

    /**
     * @param floor Second floor or Third floor
     * @return Total available reservation time count
     */
    int getTotalAvailableTimeCount(HsLibFloor floor);

    /**
     * @param floor   Second floor or Third floor
     * @param pattern Pattern with 1 param
     * @return String format with pattern
     */
    String getTotalAvailableTimeCount(HsLibFloor floor, String pattern);

    /**
     * @param floor  Second floor or Third floor
     * @param period Morning, Afternoon, Evening, AllDay
     * @return Available time count in period
     */
    int getAvailableTimeCountInPeriod(HsLibFloor floor, DateTimeUtilities.TimePeriod period);

    /**
     * @param floor   Second floor or Third floor
     * @param period  Morning, Afternoon, Evening, AllDay
     * @param pattern Pattern with 1 param
     * @return Available time count string format with pattern
     */
    String getAvailableTimeCountInPeriod(HsLibFloor floor, DateTimeUtilities.TimePeriod period, String pattern);

    /**
     * @param floor  Second floor or Third floor
     * @param period Morning, Afternoon, Evening, AllDay
     * @return Available reservations of max interval in period
     */
    long getAvailableTimeIntervalInPeriod(HsLibFloor floor, DateTimeUtilities.TimePeriod period);

    /**
     * @param floor   Second floor or Third floor
     * @param period  Morning, Afternoon, Evening, AllDay
     * @param pattern Patterns with 2 params and with 1 param
     * @return Available reservations of max interval in period of string format with pattern
     */
    String getAvailableTimeIntervalInPeriod(HsLibFloor floor, DateTimeUtilities.TimePeriod period,
                                                   String pattern, String patternWithoutMins);

    /**
     * @return List of recent reservations
     */
    List<String> getRecentReservation();

}
