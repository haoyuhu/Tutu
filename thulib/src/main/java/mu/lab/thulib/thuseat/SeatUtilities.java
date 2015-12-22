package mu.lab.thulib.thuseat;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mu.lab.thulib.common.HttpClientFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Seat Utilities
 * Created by coderhuhy on 15/12/8.
 */
public class SeatUtilities {

    private static final String LogTag = SeatUtilities.class.getCanonicalName();
    private static final int DEFAULT_RETRY = 5;

    private static Observable<SeatState> observable;

    private static HttpUrl getSeatStateUrl() {
        final String url = "http://seat.lib.tsinghua.edu.cn/roomshow/";
        return HttpUrl.parse(url).newBuilder().build();
    }

    public static void getSeatState(boolean update, Observer<SeatState> observer) {
        int retry = DEFAULT_RETRY;
        getObservableSeatState(update, retry)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private static Observable<SeatState> getObservableSeatState(final boolean update, int retry) {
        HttpUrl url = getSeatStateUrl();
        return Observable.just(url).flatMap(new Func1<HttpUrl, Observable<SeatState>>() {
            @Override
            public Observable<SeatState> call(HttpUrl url) {
                try {
                    if (!update) {
                        if (observable == null) {
                            synchronized (SeatUtilities.class) {
                                if (observable == null) {
                                    observable = Observable.from(getSeatState(url));
                                }
                            }
                        }
                    } else {
                        observable = Observable.from(getSeatState(url));
                    }
                    return observable;
                } catch (SeatException | IOException e) {
                    throw OnErrorThrowable.from(e);
                }
            }
        }).retry(retry);
    }

    private static List<SeatState> getSeatState(HttpUrl url) throws SeatException, IOException {
        OkHttpClient client = HttpClientFactory.getClient();
        Request request = new Request.Builder().url(url).get().build();
        String response = client.newCall(request).execute().body().string();
        return parse(response);
    }

    private static List<SeatState> parse(String all) throws SeatException {
        List<SeatState> ret = new ArrayList<>();
        final String re = "<td width=\"300\" +align=\"center\" height=\"50\" " +
                "(bgcolor=\"[#a-zA-Z0-9]+\" )*style=\"font-size:20.0pt\">([\\u4e00-\\u9fa5a-zA-Z0-9]+)" +
                "</td>\\s*<td width=\"300\" +align=\"center\" height=\"50\" (bgcolor=\"[#a-zA-Z0-9]+\" )" +
                "*style=\"font-size:20.0pt\">(\\d+)</td>\\s*<td width=\"300\" +align=\"center\" " +
                "height=\"50\" (bgcolor=\"[#a-zA-Z0-9]+\" )*style=\"font-size:20.0pt\">(\\d+)</td>";
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(all);
        while (matcher.find()) {
            final int count = 6;
            if (matcher.groupCount() != count) {
                throw new SeatException("cannot resolve seat response: " + all);
            }
            String area = matcher.group(2);
            int occupied = Integer.parseInt(matcher.group(4));
            int rest = Integer.parseInt(matcher.group(6));
            SeatState state = new SeatStateImpl(area, occupied, rest);
            ret.add(state);
        }
        return ret;
    }

    public static class SeatException extends Exception {

        private String details;

        public SeatException(String detailMessage) {
            super(detailMessage);
            this.details = detailMessage;
        }

        public String toString() {
            return details;
        }

    }

}
