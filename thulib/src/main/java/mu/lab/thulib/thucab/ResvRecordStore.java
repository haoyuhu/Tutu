package mu.lab.thulib.thucab;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import mu.lab.common.rx.realm.Exec;
import mu.lab.common.rx.realm.Query;
import mu.lab.common.rx.realm.RealmDatabase;
import mu.lab.thulib.thucab.entity.RealmReservationRecord;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.ReservationRecordBuilder;
import mu.lab.thulib.thucab.entity.StudentAccount;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Reservation record store
 * Created by coderhuhy on 15/11/18.
 */
public class ResvRecordStore {

    private final static String LogTag = ResvRecordStore.class.getSimpleName();

    /**
     * Get reservation records from realm
     *
     * @param account Student account
     * @return Observable of reservation records
     */
    public static Observable<List<ReservationRecord>> getResvRecordsFromRealm(final StudentAccount account) {
        return RealmDatabase.createQuery(new Query<RealmReservationRecord>() {
            @Override
            public RealmResults<RealmReservationRecord> call(Realm realm) {
                return realm.where(RealmReservationRecord.class)
                    .equalTo("studentId", account.getStudentId()).findAll();
            }
        }).map(new Func1<RealmResults<RealmReservationRecord>, List<ReservationRecord>>() {
            @Override
            public List<ReservationRecord> call(RealmResults<RealmReservationRecord> records) {
                List<ReservationRecord> ret = new ArrayList<>();
                for (RealmReservationRecord record : records) {
                    ReservationRecordBuilder builder = new ReservationRecordBuilder();
                    ret.add(builder.from(record).build());
                }
                return ret;
            }
        });
    }

    protected static Observable<List<ReservationRecord>> getResvRecords(StudentAccount account, final boolean update) {
        return CabUtilities.getReservationRecords(account, 10, update)
            .map(new Func1<List<RealmReservationRecord>, List<ReservationRecord>>() {
                @Override
                public List<ReservationRecord> call(List<RealmReservationRecord> records) {
                    List<ReservationRecord> ret = new ArrayList<>();
                    for (RealmReservationRecord record : records) {
                        if (update) {
                            RealmDatabase.insertOrUpdate(record).subscribeOn(Schedulers.io()).subscribe();
                        }
                        ReservationRecordBuilder builder = new ReservationRecordBuilder();
                        ret.add(builder.from(record).build());
                    }
                    return ret;
                }
            });
    }

    /**
     * Get reservation records from tsinghua cab system for ui
     *
     * @param account  Student account
     * @param update   Update or not
     * @param observer Observer of reservation records
     */
    public static void getResvRecords(StudentAccount account, boolean update, Observer<List<ReservationRecord>> observer) {
        getResvRecords(account, update)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer);
    }

    /**
     * Refresh reservation records from tsinghua cab system for backend
     *
     * @param account Student account
     */
    public static void refresh(StudentAccount account) {
        getResvRecords(account, true)
            .subscribeOn(Schedulers.io())
            .subscribe(new Observer<List<ReservationRecord>>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(LogTag, e.getMessage(), e);
                }

                @Override
                public void onNext(List<ReservationRecord> reservationRecords) {
                }
            });
    }

    /**
     * Clear user data by student account
     *
     * @param account Student account
     */
    public static void clear(final StudentAccount account) {
        RealmDatabase.exec(new Exec() {
            @Override
            public void run(Realm realm) {
                RealmResults<RealmReservationRecord> results = realm
                    .where(RealmReservationRecord.class).equalTo("studentId", account.getStudentId()).findAll();
                results.clear();
            }
        }, RealmReservationRecord.class).subscribeOn(Schedulers.io()).subscribe();
    }

}
