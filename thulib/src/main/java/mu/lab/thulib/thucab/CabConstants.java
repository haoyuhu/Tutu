package mu.lab.thulib.thucab;

/**
 * Cab constants
 * Created by coderhuhy on 15/11/16.
 */
public class CabConstants {

    public interface ReservationConstants {
        int MAX_RESERVATION_HOURS = 4;
        long MAX_RESERVATION_MILLIS = MAX_RESERVATION_HOURS * DateTimeConstants.MINUTE_OF_HOUR
            * DateTimeConstants.SECOND_OF_MINUTE * DateTimeConstants.MILLIS_OF_SECOND;
        int MIN_RESERVATION_MINUTES = 30;
        long MIN_RESERVATION_MILLIS = MIN_RESERVATION_MINUTES * DateTimeConstants.SECOND_OF_MINUTE
                * DateTimeConstants.MILLIS_OF_SECOND;
        int MINUTE_OF_RESERVATION_INTERVAL = 10;
        int LIMIT_RESERVATION_DAYS = 3;
        String START_TIME = "8:00";
        String END_TIME = "22:00";
    }

    public interface DateTimeConstants {
        int MINUTE_OF_HOUR = 60;
        int SECOND_OF_MINUTE = 60;
        int MILLIS_OF_SECOND = 1000;
        int DAY_OF_WEEK = 7;
        long ALLOWABLE_ERROR = 1000l;
    }

}
