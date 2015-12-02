package com.huhaoyu.tutu.entity;

import android.content.Context;

/**
 * Personal info header
 * Created by coderhuhy on 15/11/30.
 */
public interface PersonalInfoHeader {

    String getName(Context context);

    String getStudentId(Context context);

    String getDepartment(Context context);

    String getPhone(Context context);

    String getEmail(Context context);

    String getRefreshDateTime(String pattern);

    boolean hasReservation();

}
