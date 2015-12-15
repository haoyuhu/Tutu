package com.huhaoyu.tutu;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.huhaoyu.tutu.utils.MemoryWatcher;
import com.huhaoyu.tutu.utils.PreferencesUtils;
import com.huhaoyu.tutu.backend.RegularAlarmManager;
import com.huhaoyu.tutu.backend.TutuNotificationManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import mu.lab.common.rx.realm.RealmDatabase;
import mu.lab.thulib.ThuLib;
import mu.lab.thulib.ThuLibRealmModule;
import mu.lab.tufeedback.common.TUFeedback;
import mu.lab.util.Log;

/**
 * TUTu application
 * Created by coderhuhy on 15/11/24.
 */
public class TUTuApplication extends Application {

    private static final String LogTag = TUTuApplication.class.getCanonicalName();

    private static final String REALM_NAME = "com.huhaoyu.tutu.realm";
    private static final long REALM_VERSION = 1L;
    private static TUTuApplication instance;

    public static TUTuApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MemoryWatcher.init(this);
        RealmDatabase.init(new RealmConfiguration.Builder(this)
                .name(REALM_NAME)
                .schemaVersion(REALM_VERSION)
                .setModules(Realm.getDefaultModule(), new ThuLibRealmModule())
                .build());
        TUFeedback.init(this);
        ThuLib.init(this);
        TutuNotificationManager.getInstance().init(this);
        RegularAlarmManager.getInstance().init(this);
        PreferencesUtils.init(this);
        Log.i(LogTag, "Application started...");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
