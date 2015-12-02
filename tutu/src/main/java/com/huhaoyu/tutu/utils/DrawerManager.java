package com.huhaoyu.tutu.utils;

import android.support.design.widget.NavigationView;

import mu.lab.thulib.thucab.entity.StudentAccount;

/**
 * Drawer manager
 * Created by coderhuhy on 15/11/27.
 */
public abstract class DrawerManager {

    protected NavigationView navigation;

    public DrawerManager(NavigationView navigation) {
        this.navigation = navigation;
    }

    public abstract void onLogin(StudentAccount account);

    public abstract void onInit();

    public abstract void onClearUp();

    public abstract void onResume();

}
