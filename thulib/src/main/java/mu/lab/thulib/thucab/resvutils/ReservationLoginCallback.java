package mu.lab.thulib.thucab.resvutils;

/**
 * Reservation callback
 * Created by coderhuhy on 15/12/2.
 */
public interface ReservationLoginCallback {

    //Login
    void onActivationError();

    void onAccountError();

    //Common
    void onNetworkError();

    void onLocalError();
}
