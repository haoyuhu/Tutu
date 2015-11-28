package mu.lab.thulib.thucab.resvutils;

import mu.lab.thulib.thucab.httputils.ResponseState;

/**
 * Executor result observer
 * Created by coderhuhy on 15/11/15.
 */
public interface ExecutorResultObserver {

    void onError(ResponseState state);

    void onSuccess();

}
