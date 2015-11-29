package mu.lab.thulib.thucab;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.realm.RealmConfiguration;
import mu.lab.common.rx.realm.RealmDatabase;
import mu.lab.thulib.ThuLibRealmModule;
import mu.lab.thulib.thucab.entity.AutoReservationItem;
import mu.lab.thulib.thucab.entity.CabFilter;
import mu.lab.thulib.thucab.entity.JsonReservationState;
import mu.lab.thulib.thucab.entity.RealmReservationRecord;
import mu.lab.thulib.thucab.entity.RecommendResv;
import mu.lab.thulib.thucab.entity.ReservationRecord;
import mu.lab.thulib.thucab.entity.ReservationRecordBuilder;
import mu.lab.thulib.thucab.entity.ReservationState;
import mu.lab.thulib.thucab.entity.ReservationStateBuilder;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.entity.StudentDetails;
import mu.lab.thulib.thucab.httputils.LoginStateObserver;
import mu.lab.thulib.thucab.httputils.LoginSubscriber;
import mu.lab.thulib.thucab.httputils.ResponseState;
import mu.lab.thulib.thucab.resvutils.CabAutoResvCommand;
import mu.lab.thulib.thucab.resvutils.CabCommand;
import mu.lab.thulib.thucab.resvutils.CabCommandCreator;
import mu.lab.thulib.thucab.resvutils.CabSmartResvCommand;
import mu.lab.thulib.thucab.resvutils.ExecutorResultObserver;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * Cab utilities test
 * Created by coderhuhy on 15/11/12.
 */
@RunWith(AndroidJUnit4.class)
public class CabUtilitiesTest {

    private final StudentAccount account = new StudentAccount("2014210130", "hhyzy76441590");

    @Before
    public void setUp() throws Exception {
        RealmConfiguration configuration = new RealmConfiguration
            .Builder(InstrumentationRegistry.getContext())
            .setModules(new ThuLibRealmModule())
            .schemaVersion(0)
            .build();
        RealmDatabase.init(configuration);
        RealmDatabase.deleteDatabase();
    }

    @Test
    public void testLogin() throws Exception {
        LoginStateObserver observer = new TestObserver(account);
        LoginSubscriber subscriber = new LoginSubscriber(account);
        subscriber.add(observer);
        CabUtilities.cabLogin(account).subscribe(subscriber);
    }

    class TestObserver extends LoginStateObserver {

        private StudentAccount account;

        public TestObserver(StudentAccount account) {
            this.account = account;
        }

        @Override
        public void onLoginSuccess(StudentDetails details, StudentAccount account) {
            System.out.println(account.getStudentId()
                + "-" + account.getPassword() + "-" + details.toString());
        }

        @Override
        public void onStudentIdFailure(int resId) {
            String detail = InstrumentationRegistry.getContext().getString(resId);
            System.out.println(account.getStudentId()
                + "-" + account.getPassword() + "-" + detail);
        }

        @Override
        public void onPasswordFailure(int resId) {
            String detail = InstrumentationRegistry.getContext().getString(resId);
            System.out.println(account.getStudentId()
                + "-" + account.getPassword() + "-" + detail);
        }

        @Override
        public void onNetworkFailure(int resId) {
            String detail = InstrumentationRegistry.getContext().getString(resId);
            System.out.println(account.getStudentId()
                + "-" + account.getPassword() + "-" + detail);
        }
    }

    @Test
    public void testQueryRoomState() throws Exception {
        List<DateTimeUtilities.DayRound> rounds = Arrays.asList(DateTimeUtilities.DayRound.Today,
            DateTimeUtilities.DayRound.DayAfterTomorrow);
        List<DateTimeUtilities.TimePeriod> timeFilter = Arrays.asList(DateTimeUtilities.TimePeriod.Morning,
            DateTimeUtilities.TimePeriod.Night);
        CabFilter filter = new CabFilter(timeFilter, 1);
        CabUtilities.queryRoomState(rounds, filter, 10).subscribe(new Observer<List<ReservationState>>() {
            @Override
            public void onCompleted() {
                System.out.println("query room states on completed...");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onNext(List<ReservationState> states) {
                for (ReservationState state : states) {
                    String name = state.getRoomName();
                    String devId = state.getDevId();
                    System.out.println(name + "-" + devId);
                    for (ReservationState.TimeRange range : state.getAvailableTimeRanges()) {
                        String start = range.getStart();
                        String end = range.getEnd();
                        System.out.println(start + "-" + end);
                    }
                }
            }
        });
    }

    @Test
    public void testGetReservationRecords() throws Exception {
        CabUtilities.getReservationRecords(account, 10, true).subscribe(new Observer<List<RealmReservationRecord>>() {
            @Override
            public void onCompleted() {
                System.out.println("get reservation records on completed...");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onNext(List<RealmReservationRecord> realmReservationRecords) {
                for (RealmReservationRecord record : realmReservationRecords) {
                    String reservationId = record.getReservationId();
                    String studentId = record.getStudentId();
                    String name = record.getName();
                    String roomName = record.getRoomName();
                    String state = record.getState();
                    String start = record.getStart();
                    String end = record.getEnd();
                    System.out.println(reservationId + "-" + studentId + "-" + name + "-" + roomName);
                    System.out.println(state + ": " + start + "-" + end);
                }
            }
        });
    }

    @Test
    public void testModifyReservation() throws Exception {
        ReservationRecordBuilder builder = new ReservationRecordBuilder();
        builder
            .setReservationId("1623644")
            .setStudentId(account.getStudentId())
            .setName("huhaoyu")
            .setRoomName("F2-09")
            .setState("审核通过,预约成功")
            .setStart("11-18 14:00")
            .setEnd("11-18 18:00");
        ReservationRecord record = builder.build();
        ReservationState.TimeRange range = new ReservationState.TimeRange("14:10", "18:00");
        CabCommand command = CabCommandCreator.createModificationCommand(record, range, new ExecutorResultObserver() {
            @Override
            public void onError(ResponseState state) {
                String details = InstrumentationRegistry.getContext().getString(state.getDetails());
                System.out.println(details);
            }

            @Override
            public void onSuccess() {
                System.out.println("modify reservation success...");
            }
        });
        if (CabUtilities.login(account)) {
            command.executeCommand();
        }
    }

    @Test
    public void testDeleteReservation() throws Exception {
        String reservationId = "1623644";
        CabCommand command = CabCommandCreator.createDeletionCommand(reservationId, new ExecutorResultObserver() {
            @Override
            public void onError(ResponseState state) {
                String details = InstrumentationRegistry.getContext().getString(state.getDetails());
                System.out.println(details);
            }

            @Override
            public void onSuccess() {
                System.out.println("delete reservation success...");
            }
        });
        if (CabUtilities.login(account)) {
            command.executeCommand();
        }
    }

    @Test
    public void testMakeReservation() throws Exception {
        JsonReservationState json = new JsonReservationState();
        json.setRoomName("F2-10");
        json.setDevId("10344");
        json.setKindId("10310");
        json.setLabId("10319");
        json.setOpenStart("08:00");
        json.setOpenEnd("22:00");
        json.setStates(new ArrayList<JsonReservationState.JsonStateItem>());
        ReservationStateBuilder builder = new ReservationStateBuilder();
        Calendar date = DateTimeUtilities.dateToCalendar("11-18");
        ReservationState.TimeRange range = new ReservationState.TimeRange("12:00", "13:00");
        ReservationState state = builder.fromJsonReservationState(json, false).build();
        CabCommand command = CabCommandCreator.createReservationCommand(state, date, range,
            new ExecutorResultObserver() {
            @Override
            public void onError(ResponseState state) {
                String details = InstrumentationRegistry.getContext().getString(state.getDetails());
                System.out.println(details);
            }

            @Override
            public void onSuccess() {
                System.out.println("make reservation success...");
            }
        });
        if (CabUtilities.login(account)) {
            command.executeCommand();
        }
    }

    @Test
    public void testSmartReservation() throws Exception {
        DateTimeUtilities.DayRound round = DateTimeUtilities.DayRound.DayAfterTomorrow;
        ReservationState.TimeRange range = new ReservationState.TimeRange("18:00", "22:00");
        int minInterval = 2;
        CabCommand command = CabCommandCreator.createSmartResvCommand(round, range, minInterval,
            new CabSmartResvCommand.SmartReservationObserver() {
                @Override
                public void onNoMatchedRoom(List<RecommendResv> list) {
                    for (RecommendResv resv : list) {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(resv.getRoomName()).append("-").append(resv.getStart())
                            .append("-").append(resv.getEnd());
                        String details = buffer.toString();
                        System.out.println(details);
                    }
                }

                @Override
                public void onError(ResponseState state) {
                    System.out.println(state.getDetails());
                }

                @Override
                public void onSuccess() {
                    System.out.println("smart reservation success...");
                }
            });
        if (CabUtilities.login(account)) {
            command.executeCommand();
        }
    }

    @Test
    public void testAutoReservationStore() throws Exception {
        // test realm database
        ReservationState.TimeRange range = new ReservationState.TimeRange("18:00", "22:00");
        AutoReservationItem mon = new AutoReservationItem(range, Calendar.MONDAY);
        AutoReservationItem wed = new AutoReservationItem(range, Calendar.WEDNESDAY);
        AutoReservationItem thu = new AutoReservationItem(range, Calendar.THURSDAY);
        AutoReservationItem sat = new AutoReservationItem(range, Calendar.SATURDAY);
        AutoReservationItem sun = new AutoReservationItem(range, Calendar.SUNDAY);
        List<AutoReservationItem> list = Arrays.asList(mon, wed, thu, sat, sun);
        AutoResvStore.clear(account);
        AutoResvStore.saveAutoResvItemsToRealm(list, account);
        AutoResvStore.getAutoResvItems(account)
            .subscribeOn(Schedulers.io())
            .subscribe(new Observer<List<AutoReservationItem>>() {
                @Override
                public void onCompleted() {
                    System.out.println("get auto reservation items from realm...");
                }

                @Override
                public void onError(Throwable e) {
                    System.out.println("fail to get auto reservation items...");
                }

                @Override
                public void onNext(List<AutoReservationItem> list) {
                    for (AutoReservationItem item : list) {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(item.getStart() + "-" + item.getEnd())
                            .append("\nweek: ")
                            .append(item.getDate().get(Calendar.DAY_OF_WEEK))
                            .append("\nshould make reservation: " + item.shouldMakeReservation());
                        System.out.println(buffer.toString());
                    }
                }
            });
    }

    @Test
    public void testAutoReservation() throws Exception {
        ReservationState.TimeRange range = new ReservationState.TimeRange("18:00", "22:00");
        AutoReservationItem mon = new AutoReservationItem(range, Calendar.MONDAY);
        AutoReservationItem wed = new AutoReservationItem(range, Calendar.WEDNESDAY);
        AutoReservationItem thu = new AutoReservationItem(range, Calendar.THURSDAY);
        AutoReservationItem sat = new AutoReservationItem(range, Calendar.SATURDAY);
        AutoReservationItem sun = new AutoReservationItem(range, Calendar.SUNDAY);
        List<AutoReservationItem> list = Arrays.asList(mon, wed, thu, sat, sun);
        for (AutoReservationItem item : list) {
            CabCommand command = new CabAutoResvCommand(item);
            if (CabUtilities.login(account)) {
                command.executeCommand();
            }
        }
    }

}
