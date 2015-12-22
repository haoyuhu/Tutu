package com.huhaoyu.tutu.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.ui.RecommendationActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.DateTimeUtilities;
import mu.lab.thulib.thucab.entity.RecommendResv;

/**
 * Recommendation adapter
 * Created by coderhuhy on 15/12/7.
 */
public class RecommendationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LogTag = RecommendationAdapter.class.getCanonicalName();

    List<RecommendResv> list;
    DateTimeUtilities.DayRound round;
    Context context;

    class RecommendationHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.info_image)
        ImageView infoImage;
        @Bind(R.id.room_name_tv)
        TextView roomNameTv;
        @Bind(R.id.deviation_tag_tv)
        TextView deviationTagTv;
        @Bind(R.id.period_tag_tv)
        TextView periodTagTv;
        @Bind(R.id.time_tv)
        TextView timeTv;
        @Bind(R.id.info_background_ll)
        LinearLayout infoBackgroundLl;

        private int[] colors = {
                R.color.tutu_orange_transparent,
                R.color.tutu_pink_transparent,
                R.color.tutu_blue_transparent,
                R.color.tutu_green_transparent
        };

        public RecommendationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final RecommendResv recommend) {
            String room = recommend.getRoomName();
            long priority = recommend.getPriority() / CabConstants.DateTimeConstants.MILLIS_OF_SECOND
                    / CabConstants.DateTimeConstants.SECOND_OF_MINUTE;
            String start = recommend.getStart();
            String end = recommend.getEnd();
            DateTimeUtilities.TimePeriod period = DateTimeUtilities.TimePeriod.AllDay;
            try {
                period = DateTimeUtilities.TimePeriod.from(DateTimeUtilities.timeToCalendar(start));
            } catch (DateTimeUtilities.DateTimeException e) {
                Log.e(LogTag, e.getDetails(), e);
            }
            int background = colors[period.ordinal()];
            int image = period.getImageId();

            roomNameTv.setText(room);
            StringBuilder buffer = new StringBuilder();
            buffer.append(context.getString(R.string.tutu_recommendation_deviation))
                    .append(" ").append(priority);
            deviationTagTv.setText(buffer.toString());
            periodTagTv.setText(period.getResId());
            String time = start + "~" + end;
            timeTv.setText(time);

            Resources res = context.getResources();
            infoImage.setImageResource(image);
            deviationTagTv.setTextColor(res.getColor(background));
            periodTagTv.setTextColor(res.getColor(background));
            infoBackgroundLl.setBackgroundColor(res.getColor(background));

            infoBackgroundLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RecommendationActivity) context).openReservationFragment(recommend);
                }
            });
        }
    }

    public RecommendationAdapter(List<RecommendResv> list, DateTimeUtilities.DayRound round, Context context) {
        this.list = list;
        this.round = round;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecommendationHolder) holder).bind(list.get(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_item, parent, false);
        return new RecommendationHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
