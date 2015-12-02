package com.huhaoyu.tutu.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.ReservationRecordDecorator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * info item holder
 * Created by coderhuhy on 15/11/30.
 */
public class InfoItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    @Bind(R.id.info_image)
    ImageView infoImage;
    @Bind(R.id.room_name_tv)
    TextView roomNameTv;
    @Bind(R.id.round_tag_tv)
    TextView roundTagTv;
    @Bind(R.id.happen_tag_tv)
    TextView happenTagTv;
    @Bind(R.id.period_tag_tv)
    TextView periodTagTv;
    @Bind(R.id.time_tv)
    TextView timeTv;
    @Bind(R.id.info_background_ll)
    LinearLayout infoBackgroundLl;
    @Bind(R.id.modify_button)
    FrameLayout modifyButton;
    @Bind(R.id.delete_button)
    FrameLayout deleteButton;
    @Bind(R.id.button_container_ll)
    LinearLayout buttonContainerLl;

    public InfoItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(ReservationRecordDecorator record, int position, Context context) {
        String roomName, round, happen, perioid, time;
        roomName = record.getRoomName();
        round = record.getDayRound(context);
        perioid = record.getPeriod(context);
        happen = record.getState(context);

        time = record.getStart2End();
        int background = record.getPeriodBackgroundColor();

        infoImage.setImageResource(record.getPeriodImageRes());
        roomNameTv.setText(roomName);
        roundTagTv.setText(round);
        periodTagTv.setText(perioid);
        happenTagTv.setText(happen);
        timeTv.setText(time);
        infoBackgroundLl.setBackgroundColor(background);
        roundTagTv.setTextColor(background);
        periodTagTv.setTextColor(background);
        happenTagTv.setTextColor(background);

        buttonContainerLl.setVisibility(record.isHasStarted() ? View.GONE : View.VISIBLE);
        modifyButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_button:
                break;
            case R.id.delete_button:
                break;
        }
    }
}
