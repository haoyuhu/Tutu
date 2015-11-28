package mu.lab.thulib.thucab;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import mu.lab.thulib.R;

/**
 * Date time utilities
 * Created by coderhuhy on 15/11/12.
 */
public class DateTimeUtilities {

    private DateTimeUtilities() {

    }

    public enum DayRound {
        Today(0), Tomorrow(1), DayAfterTomorrow(2);
        private int increment;

        DayRound(int increment) {
            this.increment = increment;
        }

        int getIncrement() {
            return this.increment;
        }

        boolean isToday() {
            return getIncrement() == 0;
        }
    }

    public enum TimePeriod {
        Morning("08:00", "10:30", R.string.thucab_morning, R.mipmap.ic_brightness_5_grey600),
        Afternoon("10:30", "16:30", R.string.thucab_afternoon, R.mipmap.ic_brightness_4_grey600),
        Night("16:30", "22:00", R.string.thucab_evening, R.mipmap.ic_brightness_2_grey600),
        AllDay("8:00", "22:00", R.string.thucab_allday, R.mipmap.ic_brightness_7_grey600);
        String start;
        String end;
        int resId;
        int imageId;

        TimePeriod(String start, String end, int resId, int imageId) {
            this.start = start;
            this.end = end;
            this.resId = resId;
            this.imageId = imageId;
        }

        /**
         * @param time Time
         * @return Is the time in period
         * @throws DateTimeException
         */
        public final boolean inPeriod(String time) throws DateTimeException {
            return calculateInterval(time, this.start) >= 0 && calculateInterval(time, this.end) <= 0;
        }

        public int getResId() {
            return this.resId;
        }

        public int getImageId() {
            return imageId;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }
    }

    public static String getCurrentTimePoint() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        minute = ((minute / CabConstants.ReservationConstants.MINUTE_OF_RESERVATION_INTERVAL) + 1)
            * CabConstants.ReservationConstants.MINUTE_OF_RESERVATION_INTERVAL;
        if (minute >= CabConstants.DateTimeConstants.MINUTE_OF_HOUR) {
            hour += 1;
            minute -= CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
        }
        String hourStr = (hour < 10 ? "0" : "") + hour;
        String minuteStr = (minute < 10 ? "0" : "") + minute;
        return String.format("%s:%s", hourStr, minuteStr);
    }

    public static String getTimePart(String dateTime) throws DateTimeException {
        String[] dt = dateTime.split(" ");
        if (dt.length != 2) {
            throw new DateTimeException(String.format("wrong datetime[%s] cannot be resolved...", dateTime));
        } else {
            return dt[1];
        }
    }

    public static String getDatePart(String dateTime) throws DateTimeException {
        String[] dt = dateTime.split(" ");
        if (dt.length != 2) {
            throw new DateTimeException(String.format("wrong datetime[%s] cannot be resolved...", dateTime));
        } else {
            return dt[0];
        }
    }

    /**
     * @param round Day round
     * @return Calendar
     */
    public static Calendar dayRoundToCalendar(DayRound round) {
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, round.getIncrement());
        return calendar;
    }

    /**
     * @param round Today, tomorrow or day after tomorrow
     * @return format date as yyyyMMdd(20150113)
     */
    public static String formatReservationDate(DayRound round) {
        String format = "yyyyMMdd";
        return formatReservationDate(round, format);
    }

    /**
     * @param round Today, tomorrow or day after tomorrow
     * @param pattern pattern to format date time
     * @return format date as user want
     */
    public static String formatReservationDate(DayRound round, String pattern) {
        Calendar calendar = dayRoundToCalendar(round);
        return formatReservationDate(calendar, pattern);
    }

    /**
     * @param calendar Calendar with date time
     * @param pattern  Format pattern
     * @return format date as pattern
     */
    public static String formatReservationDate(Calendar calendar, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(calendar.getTime());
    }

    /**
     * @param time Time as 08:00
     * @return Calendar
     * @throws DateTimeException
     */
    public static Calendar timeToCalendar(String time) throws DateTimeException {
        DateTimeException exception =
            new DateTimeException(String.format("wrong time[%s] cannot convert to calendar.", time));
        String[] hm = time.split(":");
        if (hm.length != 2) {
            throw exception;
        }
        try {
            int hour = Integer.parseInt(hm[0]);
            int minute = Integer.parseInt(hm[1]);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            return calendar;
        } catch (NumberFormatException error) {
            throw exception;
        }
    }

    /**
     * @param date Date as 11-11
     * @return Calendar
     * @throws DateTimeException
     */
    public static Calendar dateToCalendar(String date) throws DateTimeException {
        DateTimeException exception =
            new DateTimeException(String.format("wrong date[%s] cannot convert to calendar.", date));
        String[] md = date.split("-");
        if (md.length != 2) {
            throw exception;
        }
        try {
            int month = Integer.parseInt(md[0]);
            int day = Integer.parseInt(md[1]);
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            if (month < currentMonth) {
                int year = calendar.get(Calendar.YEAR) + 1;
                calendar.set(Calendar.YEAR, year);
            }
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            return calendar;
        } catch (NumberFormatException error) {
            throw exception;
        }
    }

    /**
     * @param dateTime Date time as 11-11 23:12
     * @return Calendar
     * @throws DateTimeException
     */
    public static Calendar dateTimeToCalendar(String dateTime) throws DateTimeException {
        DateTimeException exception =
            new DateTimeException(String.format("wrong datetime[%s] cannot convert to calendar.", dateTime));
        String[] dt = dateTime.split(" ");
        if (dt.length != 2) {
            throw exception;
        }
        Calendar dc = dateToCalendar(dt[0]);
        Calendar tc = timeToCalendar(dt[1]);
        dc.set(Calendar.HOUR_OF_DAY, tc.get(Calendar.HOUR_OF_DAY));
        dc.set(Calendar.MINUTE, tc.get(Calendar.MINUTE));
        return dc;
    }

    /**
     * @param t1 String of time
     * @param t2 String of time
     * @return Abs of interval in millis
     * @throws DateTimeException
     */
    public static long calculateAbsInterval(String t1, String t2) throws DateTimeException {
        return Math.abs(calculateInterval(t1, t2));
    }

    /**
     * @param t1 String of front time
     * @param t2 String of rear time
     * @return Interval in millis(t1 subtract t2)
     * @throws DateTimeException
     */
    public static long calculateInterval(String t1, String t2) throws DateTimeException {
        Calendar calendar = timeToCalendar(t1);
        Calendar another = timeToCalendar(t2);
        return calculateInterval(calendar, another);
    }

    public static long calculateInterval(Calendar calendar, Calendar another) {
        return calendar.getTimeInMillis() - another.getTimeInMillis();
    }

    public static class DateTimeException extends Exception {

        private String details;

        public DateTimeException(String detailMessage) {
            super(detailMessage);
            this.details = detailMessage;
        }

        public String getDetails() {
            return "DateTimeException: " + details;
        }

    }

}
