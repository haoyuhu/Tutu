package mu.lab.thulib;

import android.content.Context;

import io.realm.RealmConfiguration;
import mu.lab.common.rx.realm.RealmDatabase;
import mu.lab.thulib.thucab.ThuCab;

/**
 * @author guangchen.
 */
public class ThuLib {

    /**
     * Init the whole thulib library.
     * All sub components init method should be called here.
     * Note: you must init {@link RealmDatabase} by yourself before init thulib.
     * Your configuration must include {@link ThuLibRealmModule}
     *
     * @param context Application context
     */
    public static void init(Context context) {
        ThuCab.init(context);
    }
}
