package com.huhaoyu.tutu.utils;

import mu.lab.thulib.thucab.CabConstants;

/**
 * Constants for tutu
 * Created by coderhuhy on 15/11/27.
 */
public class TutuConstants {

    public interface Constants {
        int DEFAULT_SMART_RESERVATION_INTERVAL_IN_HOUR = 2;
        long DEFAULT_PRIORITY_FILTER_VALUE = 950l * CabConstants.DateTimeConstants.SECOND_OF_MINUTE
                * CabConstants.DateTimeConstants.MILLIS_OF_SECOND;
        long REFRESH_INTERVAL = CabConstants.DateTimeConstants.MINUTE_OF_HOUR
                * CabConstants.DateTimeConstants.MILLIS_OF_SECOND
                * CabConstants.DateTimeConstants.SECOND_OF_MINUTE;
        long DELAY_DURATION = CabConstants.DateTimeConstants.MILLIS_OF_SECOND;

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
    }

}
