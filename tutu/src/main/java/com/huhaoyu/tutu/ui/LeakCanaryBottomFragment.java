package com.huhaoyu.tutu.ui;

import com.huhaoyu.tutu.utils.MemoryWatcher;
import com.huhaoyu.tutu.widget.BottomSheetFragment;
import com.squareup.leakcanary.RefWatcher;

/**
 * Leak canary bottom sheet fragment
 * Created by coderhuhy on 15/12/14.
 */
public class LeakCanaryBottomFragment extends BottomSheetFragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher watcher = MemoryWatcher.getWatcher(getActivity());
        if (watcher != null) {
            watcher.watch(this);
        }
    }
}
