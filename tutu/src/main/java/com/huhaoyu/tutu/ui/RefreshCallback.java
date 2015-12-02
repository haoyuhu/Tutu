package com.huhaoyu.tutu.ui;

/**
 * Refresh callback
 * Created by coderhuhy on 15/11/27.
 */
public interface RefreshCallback {

    void onRefreshComplete(boolean result);

    void onRefreshStart();

    void onAccountError();

    void onAccountNeedActivate();

}
