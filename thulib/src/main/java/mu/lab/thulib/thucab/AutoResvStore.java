package mu.lab.thulib.thucab;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import mu.lab.common.rx.realm.Exec;
import mu.lab.common.rx.realm.Query;
import mu.lab.common.rx.realm.RealmDatabase;
import mu.lab.thulib.thucab.entity.AutoReservationItem;
import mu.lab.thulib.thucab.entity.AutoResvRealmItem;
import mu.lab.thulib.thucab.entity.StudentAccount;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Auto reservation store
 * Created by coderhuhy on 15/11/18.
 */
public class AutoResvStore {

    /**
     * Get observable of auto reservation record list
     *
     * @param account Student account
     * @return
     */
    public static Observable<List<AutoReservationItem>> getAutoResvItems(final StudentAccount account) {
        return RealmDatabase.createQuery(new Query<AutoResvRealmItem>() {
            @Override
            public RealmResults<AutoResvRealmItem> call(Realm realm) {
                return realm.where(AutoResvRealmItem.class)
                    .equalTo("username", account.getStudentId()).findAll();
            }
        }).map(new Func1<RealmResults<AutoResvRealmItem>, List<AutoReservationItem>>() {
            @Override
            public List<AutoReservationItem> call(RealmResults<AutoResvRealmItem> list) {
                List<AutoReservationItem> ret = new ArrayList<>();
                for (AutoResvRealmItem item : list) {
                    ret.add(AutoReservationItem.from(item));
                }
                return ret;
            }
        });
    }

    /**
     * Get auto reservation record list
     *
     * @param account   Student account
     * @param subcriber Observer of auto reservation item list
     */
    public static void getAutoResvItems(final StudentAccount account, Observer<List<AutoReservationItem>> subscriber) {
        getAutoResvItems(account)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(subscriber);
    }

    /**
     * Save auto reservation item list to realm
     *
     * @param list    List of auto reservation item list
     * @param account Student account
     */
    public static void saveAutoResvItemsToRealm(List<AutoReservationItem> list, StudentAccount account) {
        for (AutoReservationItem item : list) {
            AutoResvRealmItem rsv = item.toRealm(account);
            RealmDatabase.insertOrUpdate(rsv).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    /**
     * Clear user data by account
     *
     * @param account Student account
     */
    public static void clear(final StudentAccount account) {
        RealmDatabase.exec(new Exec() {
            @Override
            public void run(Realm realm) {
                RealmResults<AutoResvRealmItem> results = realm.where(AutoResvRealmItem.class)
                    .equalTo("username", account.getStudentId()).findAll();
                results.clear();
            }
        }, AutoResvRealmItem.class).subscribeOn(Schedulers.io()).subscribe();
    }

}
