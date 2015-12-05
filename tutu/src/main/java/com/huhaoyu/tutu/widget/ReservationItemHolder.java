package com.huhaoyu.tutu.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.ReservationStateDecorator;
import com.huhaoyu.tutu.ui.ReservationListActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.ReservationState;

/**
 * Reservation item holder
 * Created by coderhuhy on 15/11/25.
 */
public class ReservationItemHolder extends RecyclerView.ViewHolder {

    int[][] colors = {
            {R.color.md_grey_400, R.color.md_grey_600, R.color.md_grey_800},
            {R.color.md_deep_orange_300, R.color.md_pink_600, R.color.md_blue_400}
    };

    @Bind(R.id.morning_bar)
    View morningBar;
    @Bind(R.id.noon_bar)
    View noonBar;
    @Bind(R.id.evening_bar)
    View eveningBar;
    @Bind(R.id.room_name_tv)
    TextView roomNameTv;
    @Bind(R.id.reservation_tags_ll)
    LinearLayout reservationTagsLl;
    @Bind(R.id.time_list_ll)
    LinearLayout timeListLl;

    public ReservationItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(final ReservationStateDecorator decorator, final Context context) {
        reservationTagsLl.removeAllViews();
        timeListLl.removeAllViews();
        Resources rs = context.getResources();
        morningBar.setBackgroundColor(rs.getColor(colors[0][0]));
        noonBar.setBackgroundColor(rs.getColor(colors[0][1]));
        eveningBar.setBackgroundColor(rs.getColor(colors[0][2]));

        String name = decorator.getRoomName();
        String interval = decorator.getMaxInterval(context);
        List<String> tags = decorator.getTimePeriodTags(context);
        List<ReservationState.TimeRange> ranges = decorator.getAvailableTimeRanges();

        roomNameTv.setText(name);
        for (String tag : tags) {
            TextView tv = (TextView) LayoutInflater.from(context)
                    .inflate(R.layout.reservation_tag_item, reservationTagsLl, false);
            tv.setText(tag);
            reservationTagsLl.addView(tv);
        }
        TextView intervalTag = (TextView) LayoutInflater.from(context)
                .inflate(R.layout.reservation_tag_item, reservationTagsLl, false);
        intervalTag.setText(interval);
        reservationTagsLl.addView(intervalTag);
        for (final ReservationState.TimeRange range : ranges) {
            View view = LayoutInflater.from(context).inflate(R.layout.reservation_time_item, timeListLl, false);
            ImageView icon = (ImageView) view.findViewById(R.id.time_icon);
            TextView timeTv = (TextView) view.findViewById(R.id.time_tv);
            TextView intervalTv = (TextView) view.findViewById(R.id.interval_tv);

            String time = range.getStart() + "~" + range.getEnd();
            String formatInterval = range.getFormatInterval(context);
            DateTimeUtilities.TimePeriod period = range.getTimePeriod();

            Resources res = context.getResources();
            if (period.equals(DateTimeUtilities.TimePeriod.AllDay)) {
                morningBar.setBackgroundColor(res.getColor(colors[1][0]));
                noonBar.setBackgroundColor(res.getColor(colors[1][1]));
                eveningBar.setBackgroundColor(res.getColor(colors[1][2]));
            } else {
                switch (period) {
                    case Morning:
                        morningBar.setBackgroundColor(res.getColor(colors[1][0]));
                        break;
                    case Afternoon:
                        noonBar.setBackgroundColor(res.getColor(colors[1][1]));
                        break;
                    case Night:
                        eveningBar.setBackgroundColor(res.getColor(colors[1][2]));
                        break;
                }
            }
            icon.setImageResource(range.getPeriodImage());
            timeTv.setText(time);
            intervalTv.setText(formatInterval);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReservationListActivity activity = (ReservationListActivity) context;
                    activity.openReservationFragment(decorator, range);
                }
            });
            timeListLl.addView(view);
        }
    }

}
