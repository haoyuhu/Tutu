package com.huhaoyu.tutu.ui;

/**
 * Refreshable fragment interface
 * Created by coderhuhy on 15/11/27.
 */
public interface RefreshableFragment {

    void refresh(boolean force, RefreshCallback callback);

}
