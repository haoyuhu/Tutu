package com.huhaoyu.tutu.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.HsLibFloor;
import com.huhaoyu.tutu.entity.ReservationSummary;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Reservation header item view holder
 * Created by coderhuhy on 15/11/26.
 */
public class ReservationSummaryItemHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.refresh_tv)
    TextView refreshTv;
    @Bind({R.id.second_floor_count, R.id.third_floor_count})
    TextView[] counts;
    @Bind({R.id.second_floor_morning_count, R.id.second_floor_afternoon_count, R.id.second_floor_evening_count})
    TextView[] secondFloorAllCounts;
    @Bind({R.id.second_floor_morning_interval, R.id.second_floor_afternoon_interval, R.id.second_floor_evening_interval})
    TextView[] secondFloorAllIntervals;
    @Bind({R.id.third_floor_morning_count, R.id.third_floor_afternoon_count, R.id.third_floor_evening_count})
    TextView[] thirdFloorAllCounts;
    @Bind({R.id.third_floor_morning_interval, R.id.third_floor_afternoon_interval, R.id.third_floor_evening_interval})
    TextView[] thirdFloorAllIntervals;

    public ReservationSummaryItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(ReservationSummary summary, int position, Context context) {
        String prefix = context.getString(R.string.tutu_refresh_time);
        String pattern = "yyyy-MM-dd HH:mm";
        String patternCnt = context.getString(R.string.tutu_reservation_count);
        String time = prefix + " " + summary.getRefreshDateTime(pattern);
        refreshTv.setText(time);
        HsLibFloor[] floors = HsLibFloor.values();
        for (int i = 0; i != floors.length; ++i) {
            String count = summary.getTotalAvailableTimeCount(floors[i], patternCnt);
            counts[i].setText(count);
        }
        showInfoOnDifferentFloor(context, summary, HsLibFloor.Second, secondFloorAllCounts, secondFloorAllIntervals);
        showInfoOnDifferentFloor(context, summary, HsLibFloor.Third, thirdFloorAllCounts, thirdFloorAllIntervals);
    }

    private void showInfoOnDifferentFloor(Context context, ReservationSummary summary, HsLibFloor floor,
                                          TextView[] cnts, TextView[] intervals) {
        String patternCnt = context.getString(R.string.tutu_reservation_count);
        String patternInt = context.getString(R.string.tutu_max_interval_for_reservation);
        String patternIntWithoutMins = context.getString(R.string.tutu_max_interval_for_reservation_without_mins);
        DateTimeUtilities.TimePeriod[] periods = DateTimeUtilities.TimePeriod.values();
        final int PERIOD_COUNT = 3;

        for (int i = 0; i != PERIOD_COUNT; ++i) {
            String count = summary.getAvailableTimeCountInPeriod(floor, periods[i], patternCnt);
            String interval = summary
                    .getAvailableTimeIntervalInPeriod(floor, periods[i], patternInt, patternIntWithoutMins);
            cnts[i].setText(count);
            intervals[i].setText(interval);
        }
    }

}
