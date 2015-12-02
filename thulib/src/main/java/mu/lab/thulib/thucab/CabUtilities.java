package mu.lab.thulib.thucab;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mu.lab.thulib.thucab.entity.CabFilter;
import mu.lab.thulib.thucab.entity.JsonReservationState;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.ReservationRecordBuilder;
import mu.lab.thulib.thucab.entity.RealmReservationRecord;
import mu.lab.thulib.thucab.entity.ReservationState;
import mu.lab.thulib.thucab.entity.ReservationStateBuilder;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.httputils.CabArrayResponse;
import mu.lab.thulib.thucab.httputils.CabHttpClient;
import mu.lab.thulib.thucab.httputils.CabObjectResponse;
import mu.lab.thulib.thucab.httputils.RequestAction;
import mu.lab.thulib.thucab.httputils.ResponseState;
import mu.lab.thulib.thucab.resvutils.CabModificationCommand;
import mu.lab.thulib.thucab.resvutils.CabReservationCommand;
import mu.lab.thulib.thucab.resvutils.ErrorTagManager;
import mu.lab.thulib.thucab.resvutils.ExecutorResultObserver;
import mu.lab.util.Log;
import rx.Observable;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;

/**
 * Cab Utilities
 * Created by coderhuhy on 15/11/11.
 */
public class CabUtilities {

    private final static String LogTag = CabUtilities.class.getSimpleName();

    private static Observable<List<RealmReservationRecord>> observableRecords;

    private CabUtilities() {

    }

    protected static HttpUrl getLoginUrl(StudentAccount account) {
        long current = System.currentTimeMillis();
        return getLoginUrl(account, current);
    }

    protected static HttpUrl getLoginUrl(StudentAccount account, long current) {
        final String url = "http://cab.hs.lib.tsinghua.edu.cn/ClientWeb/pro/ajax/login.aspx";

        return HttpUrl.parse(url).newBuilder()
            .addQueryParameter("act", RequestAction.Login.toString())
            .addQueryParameter("id", account.getStudentId())
            .addQueryParameter("pwd", account.getPassword())
            .addQueryParameter("_", String.valueOf(current))
            .build();
    }

    protected static HttpUrl getLogoutUrl() {
        long current = System.currentTimeMillis();
        final String url = "http://cab.hs.lib.tsinghua.edu.cn/ClientWeb/pro/ajax/login.aspx";

        return HttpUrl.parse(url).newBuilder()
            .addQueryParameter("act", RequestAction.Logout.toString())
            .addQueryParameter("_", String.valueOf(current))
            .build();
    }

    protected static HttpUrl getQueryUrl(String date) {
        String url = "http://cab.hs.lib.tsinghua.edu.cn/ClientWeb/pro/ajax/device.aspx";
        return HttpUrl.parse(url).newBuilder()
            .addQueryParameter("act", RequestAction.ReservationQuery.toString())
            .addQueryParameter("date", date)
            .build();
    }

    protected static HttpUrl getQueryUrl(DateTimeUtilities.DayRound round) {
        String date = DateTimeUtilities.formatReservationDate(round);
        return getQueryUrl(date);
    }

    protected static HttpUrl getRecordUrl() {
        String url = "http://cab.hs.lib.tsinghua.edu.cn/ClientWeb/xcus/ic/my.aspx";
        return HttpUrl.parse(url).newBuilder().build();
    }

    protected static HttpUrl modifyReservationUrl(String reservationId, String start, String end) {
        String url = "http://cab.hs.lib.tsinghua.edu.cn/ClientWeb/pro/ajax/reserve.aspx";
        return HttpUrl.parse(url).newBuilder()
            .addQueryParameter("resv_id", reservationId)
            .addQueryParameter("start", start)
            .addQueryParameter("end", end)
            .addQueryParameter("act", RequestAction.SetReservation.toString())
            .build();
    }

    protected static HttpUrl modifyReservationUrl(CabModificationCommand command) throws DateTimeUtilities.DateTimeException {
        return modifyReservationUrl(command.getReservationId(), command.getStart(), command.getEnd());
    }

    protected static HttpUrl deleteReservationUrl(String reservationId) {
        String url = "http://cab.hs.lib.tsinghua.edu.cn/ClientWeb/pro/ajax/reserve.aspx";
        return HttpUrl.parse(url).newBuilder()
            .addQueryParameter("id", reservationId)
            .addQueryParameter("act", RequestAction.DeleteReservation.toString())
            .build();
    }

    protected static HttpUrl getTicketUrl(CabReservationCommand command) {
        String devKind, dev, labId, date, time;
        devKind = command.getDevKind();
        dev = command.getDev();
        labId = command.getLabId();
        date = command.getDate();
        time = command.getStart();
        return getTicketUrl(devKind, dev, labId, date, time);
    }

    protected static HttpUrl getTicketUrl(String devKind, String dev, String labId, String date, String time) {
        String url = "http://cab.hs.lib.tsinghua.edu.cn/ClientWeb/xcus/ic/space_Resvset.aspx";
        return HttpUrl.parse(url).newBuilder()
            .addQueryParameter("devkind", devKind)
            .addQueryParameter("dev", dev)
            .addQueryParameter("labid", labId)
            .addQueryParameter("date", date)
            .addQueryParameter("time", time)
            .build();
    }

    /**
     * @param devKind Dev kind
     * @param dev     Dev id
     * @param date    Date format as yyyyMMdd(20151118)
     * @param labId   Lab id
     * @return Make reservation url
     */
    protected static HttpUrl makeReservationUrl(String devKind, String dev, String date, String labId) {
        String url = "http://cab.hs.lib.tsinghua.edu.cn/clientweb/xcus/ic/space_Resvset.aspx";
        return HttpUrl.parse(url).newBuilder()
            .addQueryParameter("devkind", devKind)
            .addQueryParameter("dev", dev)
            .addQueryParameter("labid", labId)
            .addQueryParameter("date", date)
            .build();
    }

    protected static HttpUrl makeReservationUrl(CabReservationCommand command) {
        String devKind, dev, labId, date;
        devKind = command.getDevKind();
        dev = command.getDev();
        labId = command.getLabId();
        date = command.getDate();
        return makeReservationUrl(devKind, dev, date, labId);
    }

    /**
     * Credential function for backend to login
     *
     * @param account Student account with student id and password
     * @return Login result
     * @throws Exception
     */
    public static ResponseState login(StudentAccount account) throws Exception {
        HttpUrl url = getLoginUrl(account);
        CabObjectResponse response = CabHttpClient.requestForJsonObject(url);
        return response.getState();
    }

    /**
     * Credential function for ui to login
     *
     * @param account Student account
     * @return Observable of CabObjectResponse
     */
    public static Observable<CabObjectResponse> cabLogin(final StudentAccount account) {
        return Observable.just(account).map(new Func1<StudentAccount, CabObjectResponse>() {
            @Override
            public CabObjectResponse call(StudentAccount account) {
                HttpUrl url = getLoginUrl(account);
                try {
                    return CabHttpClient.requestForJsonObject(url);
                } catch (Exception e) {
                    Log.e(LogTag, e.getMessage(), e);
                    throw OnErrorThrowable.from(e);
                }
            }
        });
    }

    /**
     * Credential function for ui to logout
     */
    public static void cabLogout() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUrl url = getLogoutUrl();
                    CabHttpClient.requestForJsonObject(url);
                } catch (Exception e) {
                    Log.e(LogTag, e.getMessage(), e);
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     * Query room states
     *
     * @param round Day round
     * @return List of reservation state
     * @throws Exception
     */
    public static List<ReservationState> queryRoomState(DateTimeUtilities.DayRound round)
        throws Exception {
        List<ReservationState> ret = new ArrayList<>();
        HttpUrl url = getQueryUrl(round);
        CabArrayResponse response = CabHttpClient.requestForJsonArray(url);
        JSONArray array = response.getData();
        if (response.isRequestSuccess() && array != null) {
            Type type = new TypeToken<ArrayList<JsonReservationState>>() {
            }.getType();
            List<JsonReservationState> list = new Gson().fromJson(array.toString(), type);
            for (JsonReservationState object : list) {
                ReservationStateBuilder builder = new ReservationStateBuilder();
                ReservationState state = builder.fromJsonReservationState(object, round.isToday()).build();
                if (state.isHumanitiesLibSingleRoom() && state.isRoomAvailable()) {
                    ret.add(state);
                }
            }
        }
        return ret;
    }

    protected static Observable<List<ReservationState>> queryRoomState(
        List<DateTimeUtilities.DayRound> rounds, int retry) {
        return Observable.from(rounds).map(new Func1<DateTimeUtilities.DayRound, List<ReservationState>>() {
            @Override
            public List<ReservationState> call(DateTimeUtilities.DayRound round) {
                try {
                    return queryRoomState(round);
                } catch (Exception e) {
                    Log.e(LogTag, e.getMessage(), e);
                    throw OnErrorThrowable.from(e);
                }
            }
        }).retry(retry);
    }

    /**
     * Query for room states
     *
     * @param rounds Day round you need to query
     * @param filter Time period and min interval you want to query
     * @param retry  Retry time(recommendation: 10)
     * @return Observable of List of Reservation state
     */
    public static Observable<List<ReservationState>> queryRoomState(
        List<DateTimeUtilities.DayRound> rounds, final CabFilter filter, int retry) {
        Observable<List<ReservationState>> obs = queryRoomState(rounds, retry).cache();
        return obs.map(new Func1<List<ReservationState>, List<ReservationState>>() {
            @Override
            public List<ReservationState> call(List<ReservationState> origins) {
                if (filter == null) {
                    return origins;
                }
                List<DateTimeUtilities.TimePeriod> periodFilter = filter.getPeriods();
                int intervalFilter = filter.getIntervalInHour();
                for (ReservationState origin : origins) {
                    List<ReservationState.TimeRange> ranges = origin.getAvailableTimeRanges(intervalFilter);
                    List<ReservationState.TimeRange> rest = new ArrayList<>();
                    for (ReservationState.TimeRange range : ranges) {
                        for (DateTimeUtilities.TimePeriod period : periodFilter) {
                            try {
                                if (period.inPeriod(range.getStart())) {
                                    rest.add(range);
                                    break;
                                }
                            } catch (DateTimeUtilities.DateTimeException e) {
                                Log.e(LogTag, e.getDetails(), e);
                            }
                        }
                    }
                    origin.setAvailableTimeRanges(rest);
                }
                return origins;
            }
        });
    }

    protected static List<RealmReservationRecord> getReservationRecords(StudentAccount account) throws Exception {
        ResponseState resp = login(account);
        if (resp.equals(ResponseState.Success)) {
            HttpUrl url = getRecordUrl();
            String response = CabHttpClient.requestForText(url);
            return parseRecords(response);
        } else {
            String error = ErrorTagManager.from(resp);
            throw new Exception(error);
        }
    }

    /**
     * Fetch user info for backend
     *
     * @return List of reservation record
     * @throws Exception
     */
    public static List<ReservationRecord> getReservationRecordsWithoutLogin() throws Exception {
        HttpUrl url = getRecordUrl();
        String response = CabHttpClient.requestForText(url);
        List<RealmReservationRecord> list = parseRecords(response);
        List<ReservationRecord> ret = new ArrayList<>();
        for (RealmReservationRecord record : list) {
            ReservationRecordBuilder builder = new ReservationRecordBuilder();
            ret.add(builder.from(record).build());
        }
        return ret;
    }

    /**
     * Fetch user info
     *
     * @param account     Student account
     * @param retry       Retry time(suggest: 10)
     * @param forceUpdate update or not
     * @return Observable of list of realm reservation record
     */
    public static Observable<List<RealmReservationRecord>> getReservationRecords(
        StudentAccount account, int retry, boolean forceUpdate) {
        if (observableRecords == null || forceUpdate) {
            observableRecords = Observable.just(account).map(new Func1<StudentAccount, List<RealmReservationRecord>>() {
                @Override
                public List<RealmReservationRecord> call(StudentAccount account) {
                    try {
                        return getReservationRecords(account);
                    } catch (Exception e) {
                        throw OnErrorThrowable.from(e);
                    }
                }
            }).retry(retry);
        }
        return observableRecords;
    }

    protected static List<RealmReservationRecord> parseRecords(String resp) {
        final String pattern = "rsvId='(\\d+)' owner='(\\d+)'><td>\\d+</td><td>([\\u4E00-\\u9FA5]+)" +
            "</td><td></td><td>(\\w\\d-\\d{2}\\w*)</td><td>([\\u4E00-\\u9FA5]+,[\\u4E00-\\u9FA5]+)" +
            "</td><td>(\\d{1,2}-\\d{2} \\d{1,2}:\\d{2})</td><td>(\\d{1,2}-\\d{2} \\d{1,2}:\\d{2})";
        Matcher matcher = Pattern.compile(pattern).matcher(resp);
        List<RealmReservationRecord> ret = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.groupCount() != 7) {
                continue;
            }
            String reservationId, studentId, name, roomName, state, start, end;
            reservationId = matcher.group(1);
            studentId = matcher.group(2);
            name = matcher.group(3);
            roomName = matcher.group(4);
            state = matcher.group(5);
            start = matcher.group(6);
            end = matcher.group(7);
            ReservationRecordBuilder builder = new ReservationRecordBuilder();
            builder.setReservationId(reservationId).setStudentId(studentId).setName(name).setRoomName(roomName)
                .setState(state).setStart(start).setEnd(end);
            ret.add(builder.buildRealmEntity());
        }
        return ret;
    }

    /**
     * Modify existing reservation, should run on work thread
     *
     * @param command  Cab modification command
     * @param observer Executor result observer
     * @return success or not
     * @throws Exception
     */
    public static boolean modifyReservation(CabModificationCommand command,
                                            ExecutorResultObserver observer) throws Exception {
        try {
            HttpUrl url = modifyReservationUrl(command);
            CabObjectResponse response = CabHttpClient.requestForJsonObject(url);
            if (response.isRequestSuccess()) {
                observer.onSuccess();
                return true;
            } else {
                observer.onError(response.getState());
                return false;
            }
        } catch (DateTimeUtilities.DateTimeException e) {
            Log.e(LogTag, e.getDetails(), e);
            observer.onError(ResponseState.DateFailure);
            return false;
        }
    }

    /**
     * Modify existing reservation, should run on work thread
     *
     * @param command Cab modification command
     * @return success or not
     * @throws Exception
     */
    public static boolean modifyReservation(CabModificationCommand command) throws Exception {
        try {
            HttpUrl url = modifyReservationUrl(command);
            return CabHttpClient.requestForJsonObject(url).isRequestSuccess();
        } catch (DateTimeUtilities.DateTimeException e) {
            Log.e(LogTag, e.getDetails(), e);
            return false;
        }
    }

    /**
     * @param reservationId Reservation id
     * @param observer      Executor result observer
     * @return success or not
     * @throws Exception
     */
    public static boolean deleteReservation(String reservationId, ExecutorResultObserver observer)
        throws Exception {
        HttpUrl url = deleteReservationUrl(reservationId);
        CabObjectResponse response = CabHttpClient.requestForJsonObject(url);
        if (response.isRequestSuccess()) {
            observer.onSuccess();
            return true;
        } else {
            observer.onError(response.getState());
            return false;
        }
    }

    /**
     * @param reservationId reservation id
     * @return success or not
     * @throws Exception
     */
    public static boolean deleteReservation(String reservationId) throws Exception {
        HttpUrl url = deleteReservationUrl(reservationId);
        return CabHttpClient.requestForJsonObject(url).isRequestSuccess();
    }

    protected static String getReservationTicket(CabReservationCommand command, ExecutorResultObserver observer)
        throws Exception {
        HttpUrl url = getTicketUrl(command);
        String response = CabHttpClient.requestForText(url);
        Matcher matcher = Pattern.compile("id=\\\"__VIEWSTATE\\\"\\s*value=\\\"(/.+?)\\\" />").matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            if (observer != null) {
                observer.onError(ResponseState.NoTicketFailure);
            }
            throw new Exception(String.format("cannot parse ticket from %s", url.toString()));
        }
    }

    protected static void makeReservation(String ticket, CabReservationCommand command) throws Exception {
        HttpUrl url = makeReservationUrl(command);
        String ddlHourStart = command.getStart();
        String ddlHourEnd = command.getEnd();
        RequestBody body = new FormEncodingBuilder()
            .add("__EVENTTARGET", "Sub")
            .add("__VIEWSTATE", ticket)
            .add("ddlHourStart", ddlHourStart)
            .add("ddlHourEnd", ddlHourEnd)
            .build();
        CabHttpClient.requestForText(url, body);
    }

    /**
     * Make reservation for ui
     *
     * @param command  Cab reservation command
     * @param observer Executor result observer
     * @return Make reservation success or not
     * @throws Exception
     */
    public static boolean makeReservation(CabReservationCommand command, ExecutorResultObserver observer)
        throws Exception {
        String ticket = getReservationTicket(command, observer);
        List<ReservationRecord> pre = getReservationRecordsWithoutLogin();
        if (!checkConflict(command.getDate(), pre)) {
            makeReservation(ticket, command);
            List<ReservationRecord> current = getReservationRecordsWithoutLogin();
            if (checkConflict(command.getDate(), current)) {
                observer.onSuccess();
                return true;
            }
        }
        observer.onError(ResponseState.ConflictFailure);
        return false;
    }

    /**
     * Make reservation for backend
     *
     * @param command Cab reservation command
     * @return Make reservation success or not
     * @throws Exception
     */
    public static boolean makeReservation(CabReservationCommand command) throws Exception {
        String ticket = getReservationTicket(command, null);
        List<ReservationRecord> pre = getReservationRecordsWithoutLogin();
        if (!checkConflict(command.getDate(), pre)) {
            makeReservation(ticket, command);
            List<ReservationRecord> current = getReservationRecordsWithoutLogin();
            if (checkConflict(command.getDate(), current)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param date Date format as yyyyMMdd
     * @param list List of reservation record
     * @return Has conflict or not
     */
    protected static boolean checkConflict(String date, List<ReservationRecord> list) {
        final String pattern = "yyyyMMdd";
        for (ReservationRecord record : list) {
            try {
                Calendar calendar = record.getDate();
                String another = DateTimeUtilities.formatReservationDate(calendar, pattern);
                if (another.equals(date)) {
                    return true;
                }
            } catch (DateTimeUtilities.DateTimeException e) {
                Log.e(LogTag, e.getDetails(), e);
            }
        }
        return false;
    }

}
