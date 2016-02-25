package mu.lab.thulib.common;

import com.squareup.okhttp.OkHttpClient;

import java.net.CookieManager;

/**
 * @author guangchen.
 */
public class HttpClientFactory {
    private static volatile boolean isInit = false;

    protected static OkHttpClient client = new OkHttpClient();

    private HttpClientFactory() {}

    protected static synchronized void init() {
        if (!isInit) {
            isInit = true;
            CookieManager manager = new CookieManager();
            client.setCookieHandler(manager);
        }
    }

    public static OkHttpClient getClient() {
        if (!isInit) {
            init();
        }
        return client;
    }
}
