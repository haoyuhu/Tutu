package mu.lab.thulib.thucab.httputils;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;
import java.util.Collection;

import mu.lab.thulib.common.HttpClientFactory;

/**
 * Cab http client
 * Created by coderhuhy on 15/11/12.
 */
public class CabHttpClient {

    protected static long count = 0;
    protected static Collection<String> tags = new ArrayList<>();

    private CabHttpClient() {
    }

    public static CabObjectResponse requestForJsonObject(HttpUrl url) throws Exception {
        String rawResp = requestForText(url);
        return new CabObjectResponse(rawResp);
    }

    public static CabArrayResponse requestForJsonArray(HttpUrl url) throws Exception {
        String rawResp = requestForText(url);
        return new CabArrayResponse(rawResp);
    }

    public static String requestForText(HttpUrl url) throws Exception {
        OkHttpClient client = HttpClientFactory.getClient();
        String tag = url.toString() + "-" + (++count);
        Request request = new Request.Builder().tag(tag).url(url).get().build();
        tags.add(tag);
        return client.newCall(request).execute().body().string();
    }

    public static String requestForText(HttpUrl url, RequestBody body) throws Exception {
        OkHttpClient client = HttpClientFactory.getClient();
        String tag = url.toString() + "-" + (++count);
        Request request = new Request.Builder().tag(tag).url(url).post(body).build();
        tags.add(tag);
        return client.newCall(request).execute().body().string();
    }

    public static synchronized void cancel() {
        OkHttpClient client = HttpClientFactory.getClient();
        for (String tag : tags) {
            client.cancel(tag);
        }
        tags.clear();
        count = 0l;
    }

}
