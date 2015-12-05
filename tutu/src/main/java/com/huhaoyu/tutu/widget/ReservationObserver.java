package com.huhaoyu.tutu.widget;

/**
 * Reservation callback
 * Created by coderhuhy on 15/12/3.
 */
public interface ReservationObserver {

    void onAccountError();

    void onNetworkError();

    void onReservationSuccess();

}
