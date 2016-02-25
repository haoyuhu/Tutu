package mu.lab.thulib.thucab.resvutils;

/**
 * Executor result observer
 * Created by coderhuhy on 15/11/15.
 */
public interface ExecutorResultObserver {

    void onConflict();

    void onNetworkFailure();

    void onSuccess();

}
