package com.huhaoyu.tutu.utils;

import mu.lab.thulib.thucab.CabConstants;

/**
 * Constants for tutu
 * Created by coderhuhy on 15/11/27.
 */
public class TutuConstants {

    public interface Constants {
        //reservation constants
        int DEFAULT_SMART_RESERVATION_INTERVAL_IN_HOUR = 2;
        long DEFAULT_PRIORITY_FILTER_VALUE = 950l * CabConstants.DateTimeConstants.SECOND_OF_MINUTE
                * CabConstants.DateTimeConstants.MILLIS_OF_SECOND;
        long REFRESH_INTERVAL = CabConstants.DateTimeConstants.MINUTE_OF_HOUR
                * CabConstants.DateTimeConstants.MILLIS_OF_SECOND
                * CabConstants.DateTimeConstants.SECOND_OF_MINUTE;
        long DELAY_DURATION = CabConstants.DateTimeConstants.MILLIS_OF_SECOND;
        int DEFAULT_AUTO_RESERVATION_NUMBER_LIMIT = 2;

        // regular task interval
        long DEFAULT_NOTIFICATION_INTERVAL_IN_MILLIS = 30 * CabConstants.DateTimeConstants.SECOND_OF_MINUTE
                * CabConstants.DateTimeConstants.MILLIS_OF_SECOND;
        long ALARM_INTERVAL_IN_MILLIS = 15 * CabConstants.DateTimeConstants.SECOND_OF_MINUTE
                * CabConstants.DateTimeConstants.MILLIS_OF_SECOND;
        long ALARM_INTERVAL_VALIDATE_IN_MILLIS = 10 * CabConstants.DateTimeConstants.SECOND_OF_MINUTE
                * CabConstants.DateTimeConstants.MILLIS_OF_SECOND;
        long DEFAULT_AUTO_RESERVATION_TASK_INTERVAL_IN_MILLIS = CabConstants.DateTimeConstants.MINUTE_OF_HOUR
                * CabConstants.DateTimeConstants.SECOND_OF_MINUTE * CabConstants.DateTimeConstants.MILLIS_OF_SECOND;
        int DEFAULT_POSTPONE_INTERVAL_IN_MINUTE = 10;
        int DEFAULT_REGULAR_TASK_MUTE_START_HOUR = 0;
        int DEFAULT_REGULAR_TASK_MUTE_START_MINUTE = 30;
        int DEFAULT_REGULAR_TASK_MUTE_END_HOUR = 7;
        int DEFAULT_REGULAR_TASK_MUTE_END_MINUTE = 30;

        // notification id
        int TUTU_RESERVATION_NOTIFICATION_ID = 100;
        int TUTU_MESSAGE_NOTIFICATION_ID = 101;
        int TUTU_TEST_NOTIFICATION_ID = 102;

        String background[] = {
                "http://git.oschina.net/huhaoyu/picture/raw/master/tutu_main_background_1.jpg",
                "http://git.oschina.net/huhaoyu/picture/raw/master/tutu_main_background_2.jpg",
                "http://git.oschina.net/huhaoyu/picture/raw/master/tutu_main_background_3.png",
                "http://git.oschina.net/huhaoyu/picture/raw/master/tutu_main_background_5.jpg"
        };
    }

    public interface BundleKey {
        String BUNDLE_KEY = "TUTU_BUNDLE_KEY";
        String EXTRA_BUNDLE_KEY = "TUTU_EXTRA_BUNDLE_KEY";
    }

    public interface RequestCode {
        int REQUEST_CODE_RECOMMENDATION = 0;
        int REQUEST_CODE_AUTO_SETTINGS = 1;
    }

}
