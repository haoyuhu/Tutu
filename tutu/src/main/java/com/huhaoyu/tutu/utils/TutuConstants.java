package com.huhaoyu.tutu.utils;

import mu.lab.thulib.thucab.CabConstants;

/**
 * Constants for tutu
 * Created by coderhuhy on 15/11/27.
 */
public class TutuConstants {

    public interface Constants {
        long REFRESH_INTERVAL = 10 * CabConstants.DateTimeConstants.MILLIS_OF_SECOND
                * CabConstants.DateTimeConstants.SECOND_OF_MINUTE;
        long DELAY_DURATION = CabConstants.DateTimeConstants.MILLIS_OF_SECOND;

        String background[] = {
                "http://git.oschina.net/huhaoyu/picture/raw/master/tutu_main_background_1.jpg?dir=0" +
                        "&filepath=tutu_main_background_1.jpg&oid=82c407aa7e38eb55c83e0aff846827c1baf2b7b6" +
                        "&sha=64b501311c905fe91888b9522273d2c2c004c1a3",
                "http://git.oschina.net/huhaoyu/picture/raw/master/tutu_main_background_2.jpg?dir=0" +
                        "&filepath=tutu_main_background_2.jpg&oid=6d56a4b5f25e34bdf879384b1592d0289b9be34e" +
                        "&sha=64b501311c905fe91888b9522273d2c2c004c1a3",
                "http://git.oschina.net/huhaoyu/picture/raw/master/tutu_main_background_3.png?dir=0" +
                        "&filepath=tutu_main_background_3.png&oid=0b2f1c9bc23ccee44b7cd3763d26148aa6d260d2" +
                        "&sha=64b501311c905fe91888b9522273d2c2c004c1a3",
                "http://git.oschina.net/huhaoyu/picture/raw/master/tutu_main_background_5.jpg?dir=0" +
                        "&filepath=tutu_main_background_5.jpg&oid=9a86107669f0083b061de27c6b334198ab0ce043" +
                        "&sha=64b501311c905fe91888b9522273d2c2c004c1a3"
        };
    }

    public interface BundleKey {

    }

}
