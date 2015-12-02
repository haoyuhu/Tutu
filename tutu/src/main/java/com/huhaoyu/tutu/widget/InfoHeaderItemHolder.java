package com.huhaoyu.tutu.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.PersonalInfoHeader;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Info header item holder
 * Created by coderhuhy on 15/11/30.
 */
public class InfoHeaderItemHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.refresh_tv)
    TextView refreshTv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.student_id_tv)
    TextView studentIdTv;
    @Bind(R.id.department_tv)
    TextView departmentTv;
    @Bind(R.id.phone_tv)
    TextView phoneTv;
    @Bind(R.id.email_tv)
    TextView emailTv;
    @Bind(R.id.empty_info_tv)
    TextView emptyInfoTv;

    public InfoHeaderItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(PersonalInfoHeader info, Context context) {
        String refreshTime, name, studentId, department, phone, email;
        final String prefix = context.getString(R.string.tutu_refresh_time);
        final String pattern = "yyyy-MM-dd HH:mm";
        refreshTime = prefix + info.getRefreshDateTime(pattern);
        name = info.getName(context);
        studentId = info.getStudentId(context);
        department = info.getDepartment(context);
        phone = info.getPhone(context);
        email = info.getEmail(context);

        refreshTv.setText(refreshTime);
        nameTv.setText(name);
        studentIdTv.setText(studentId);
        departmentTv.setText(department);
        phoneTv.setText(phone);
        emailTv.setText(email);
        emptyInfoTv.setVisibility(info.hasReservation() ? View.GONE : View.VISIBLE);
    }

}
