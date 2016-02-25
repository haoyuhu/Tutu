package mu.lab.thulib.thucab.httputils;

/**
 * Request action
 * Created by coderhuhy on 15/11/12.
 */
public enum RequestAction {

    Login("login"), Logout("logout"), notFound("notFound"),
    ReservationQuery("get_rsv_sta"), SetReservation("set_resv"),
    DeleteReservation("del_resv");
    String action;

    RequestAction(String action) {
        this.action = action;
    }

    public String toString() {
        return this.action;
    }

    public static RequestAction from(String action) {
        for (RequestAction act : RequestAction.values()) {
            if (act.toString().equals(action)) {
                return act;
            }
        }
        return notFound;
    }

}
