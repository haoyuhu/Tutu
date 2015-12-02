package mu.lab.thulib.thucab.resvutils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mu.lab.thulib.thucab.CabUtilities;
import mu.lab.thulib.thucab.ResvRecordStore;
import mu.lab.thulib.thucab.entity.RecommendResv;
import mu.lab.thulib.thucab.entity.StudentAccount;
import mu.lab.thulib.thucab.httputils.ResponseState;
import rx.Observable;
import rx.Observer;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Implement of Cab command executor
 * Created by coderhuhy on 15/11/19.
 */
public class CabCmdExecutorImpl implements CabCommandExecutor, Observer<ExecuteResult> {

    private static final String LogTag = CabCmdExecutorImpl.class.getSimpleName();

    protected List<CabCommand> commands = new ArrayList<>();
    protected List<ReservationLoginCallback> callbacks = new ArrayList<>();

    private static class CabCmdExecutorHolder {
        static CabCmdExecutorImpl instance = new CabCmdExecutorImpl();
    }

    private CabCmdExecutorImpl() {
    }

    public static CabCommandExecutor getInstance() {
        return CabCmdExecutorHolder.instance;
    }

    /**
     * You should register callback on resume when you need
     *
     * @param callback Reservation login callback
     */
    @Override
    public void registerCallback(ReservationLoginCallback callback) {
        callbacks.add(callback);
    }

    /**
     * You should unregister callback on pause
     *
     * @param callback Reservation login callback
     */
    @Override
    public void unregisterCallback(ReservationLoginCallback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }

    @Override
    public void addCommand(CabCommand command) {
        commands.add(command);
    }

    @Override
    public void addCommands(List<CabCommand> cmds) {
        commands.addAll(cmds);
    }

    @Override
    public void remove(CabCommand command) {
        commands.remove(command);
    }

    @Override
    public void remove() {
        commands.clear();
    }

    @Override
    public void execute(@NonNull StudentAccount account, CabCommand... commands) {
        List<CabCommand> tasks = new ArrayList<>();
        tasks.addAll(Arrays.asList(commands));
        execute(account, tasks);
    }

    @Override
    public void execute(@NonNull StudentAccount account) {
        List<CabCommand> tasks = new ArrayList<>();
        synchronized (CabCommandExecutor.class) {
            for (CabCommand command : this.commands) {
                tasks.add(command);
            }
            this.commands.clear();
        }
        execute(account, tasks);
    }

    protected void execute(@NonNull StudentAccount account, final List<CabCommand> commands) {
        Observable.just(account).filter(new Func1<StudentAccount, Boolean>() {
            @Override
            public Boolean call(StudentAccount account) {
                try {
                    ResponseState resp = CabUtilities.login(account);
                    switch (resp) {
                        case Success:
                            return true;
                        default:
                            throw OnErrorThrowable.from(new RuntimeException(ErrorTagManager.from(resp)));
                    }
                } catch (Exception e) {
                    Exception exception = new Exception(ErrorTagManager.from(ResponseState.OtherFailure));
                    throw OnErrorThrowable.from(exception);
                }
            }
        }).flatMap(new Func1<StudentAccount, Observable<ExecuteResult>>() {
            @Override
            public Observable<ExecuteResult> call(StudentAccount account) {
                List<ExecuteResult> results = new ArrayList<>();
                for (CabCommand command : commands) {
                    try {
                        results.add(command.executeCommand());
                    } catch (Exception e) {
                        Log.e(LogTag, e.getMessage(), e);
                    }
                }
                return Observable.from(results);
            }
        }).subscribeOn(Schedulers.io()).subscribe(this);
    }

    @Override
    public void refresh(@NonNull StudentAccount account) {
        ResvRecordStore.refresh(account);
        CabCommand command = CabCommandCreator.createAutoResvCmdGroup();
        this.addCommand(command);
        this.execute(account);
    }

    @Override
    public void clear() {
        commands.clear();
    }

    @Override
    public void onCompleted() {
        Log.i(LogTag, "execute commands success...");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(LogTag, e.getMessage(), e);
        ResponseState state = ErrorTagManager.toState(e);
        switch (state) {
            case ActivateFailure:
                for (ReservationLoginCallback callback : callbacks) {
                    callback.onActivationError();
                }
                break;
            case IdFailure:
            case PasswordFailure:
                for (ReservationLoginCallback callback : callbacks) {
                    callback.onAccountError();
                }
                break;
            case DateFailure:
                for (ReservationLoginCallback callback : callbacks) {
                    callback.onLocalError();
                }
                break;
            default:
                for (ReservationLoginCallback callback : callbacks) {
                    callback.onNetworkError();
                }
        }
    }

    @Override
    public void onNext(ExecuteResult result) {
        if (result.hasObserver()) {
            CommandKind kind = result.getCommandKind();
            ExecuteResult.CommandResultState state = result.getResultState();
            ExecutorResultObserver observer = result.getObserver();
            if (kind.equals(CommandKind.SmartReservation)
                    && state.equals(ExecuteResult.CommandResultState.Recommendation)) {
                CabSmartResvCommand.SmartReservationObserver smartObserver
                        = (CabSmartResvCommand.SmartReservationObserver) observer;
                List<RecommendResv> list = smartObserver.getRecommandList();
                smartObserver.onNoMatchedRoom(list);
            } else {
                switch (state) {
                    case Success:
                        observer.onSuccess();
                        break;
                    case Conflict:
                        observer.onConflict();
                        break;
                    case NetworkFailure:
                        observer.onNetworkFailure();
                        break;
                    default:
                }
            }
        }

    }
}
