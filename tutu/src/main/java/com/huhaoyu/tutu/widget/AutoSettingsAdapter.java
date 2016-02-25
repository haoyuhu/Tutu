package com.huhaoyu.tutu.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.AutoResvItemDecorator;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thucab.CabConstants;
import mu.lab.thulib.thucab.DateTimeUtilities;

/**
 * Auto settings adapter
 * Created by coderhuhy on 15/12/10.
 */
public class AutoSettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LogTag = AutoSettingsAdapter.class.getCanonicalName();

    List<AutoResvItemDecorator> list;
    Context context;
    String[] weeks;

    protected class AutoSettingViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @Bind(R.id.tag_image)
        ImageView tagImage;
        @Bind(R.id.week_tv)
        TextView weekTv;
        @Bind(R.id.start_tv)
        TextView startTv;
        @Bind(R.id.end_tv)
        TextView endTv;
        @Bind(R.id.setting_card)
        CardView settingCard;

        public AutoSettingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(AutoResvItemDecorator item, int position) {
            String week = weeks[position];
            String start = handleTime(item.getStart());
            String end = handleTime(item.getEnd());

            float alpha = item.getAlpha();
            int color = item.getColorId();
            int image = item.getImageId();
            tagImage.setImageResource(image);
            tagImage.setBackgroundColor(context.getResources().getColor(color));
            tagImage.setTag(position);
            weekTv.setText(week);
            startTv.setText(start);
            startTv.setTag(position);
            endTv.setText(end);
            endTv.setTag(position);
            settingCard.setAlpha(alpha);
            startTv.setEnabled(item.isEnable());
            endTv.setEnabled(item.isEnable());

            startTv.setOnClickListener(this);
            endTv.setOnClickListener(this);
            tagImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            switch (v.getId()) {
                case R.id.start_tv:
                    getTime(true, position);
                    break;
                case R.id.end_tv:
                    getTime(false, position);
                    break;
                case R.id.tag_image:
                    boolean enable = list.get(position).isEnable();
                    list.get(position).setEnable(!enable);
                    notifyDataSetChanged();
                    break;
            }
        }

        private String handleTime(String time) {
            while (time.startsWith("0")) {
                time = time.substring(1);
            }
            return time;
        }

        private void getTime(final boolean startOrEnd, final int position) {
            Timepoint[] points = null;
            try {
                points = startOrEnd ? getOptionalStart() : getOptionalEnd();
            } catch (DateTimeUtilities.DateTimeException e) {
                Log.e(LogTag, e.getDetails(), e);
            }
            int sh = 8, sm = 0;
            if (points != null && points.length != 0) {
                int index = startOrEnd ? 0 : points.length - 1;
                sh = points[index].getHour();
                sm = points[index].getMinute();
            }
            TimePickerDialog dialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                    final String pattern = "HH:mm";
                    if (startOrEnd) {
                        Calendar startCal = Calendar.getInstance();
                        startCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startCal.set(Calendar.MINUTE, minute);
                        String start = DateTimeUtilities.formatReservationDate(startCal, pattern);
                        list.get(position).setStart(start);

                        String end = CabConstants.ReservationConstants.END_TIME;
                        try {
                            end = DateTimeUtilities.getMaxOptionalEnd(start, end);
                        } catch (DateTimeUtilities.DateTimeException e) {
                            Log.e(LogTag, e.getDetails(), e);
                        }
                        list.get(position).settEnd(end);
                    } else {
                        Calendar endCal = Calendar.getInstance();
                        endCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endCal.set(Calendar.MINUTE, minute);
                        String end = DateTimeUtilities.formatReservationDate(endCal, pattern);
                        list.get(position).settEnd(end);
                    }
                    notifyDataSetChanged();
                }
            }, sh, sm, false);
            if (points != null) {
                dialog.setSelectableTimes(points);
            }
            dialog.show(((Activity) context).getFragmentManager(), LogTag);
        }

        private Timepoint[] getOptionalStart() throws DateTimeUtilities.DateTimeException {
            List<Timepoint> list = new ArrayList<>();
            Timepoint start = getTimePoint(CabConstants.ReservationConstants.START_TIME);
            Timepoint end = getTimePoint(CabConstants.ReservationConstants.END_TIME);
            int hour = start.getHour();
            int minute = start.getMinute();
            int endh = end.getHour();
            int endm = end.getMinute() - CabConstants.ReservationConstants.MIN_RESERVATION_MINUTES;
            if (endm < 0) {
                --endh;
                endm += CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
            }
            while (toMinute(hour, minute) <= toMinute(endh, endm)) {
                list.add(new Timepoint(hour, minute));
                minute += CabConstants.ReservationConstants.MINUTE_OF_RESERVATION_INTERVAL;
                if (minute >= CabConstants.DateTimeConstants.MINUTE_OF_HOUR) {
                    ++hour;
                    minute -= CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
                }
            }
            return list.toArray(new Timepoint[list.size()]);
        }

        private Timepoint[] getOptionalEnd() throws DateTimeUtilities.DateTimeException {
            List<Timepoint> list = new ArrayList<>();
            String start = startTv.getText().toString();
            String end = endTv.getText().toString();
            Timepoint s = getTimePoint(start);
            Timepoint e = getTimePoint(end);
            int hour = s.getHour();
            int minute = s.getMinute() + CabConstants.ReservationConstants.MIN_RESERVATION_MINUTES;
            int endh = e.getHour();
            int endm = e.getMinute();
            if (minute >= CabConstants.DateTimeConstants.MINUTE_OF_HOUR) {
                ++hour;
                minute -= CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
            }
            while (toMinute(hour, minute) <= toMinute(endh, endm)) {
                list.add(new Timepoint(hour, minute));
                minute += CabConstants.ReservationConstants.MINUTE_OF_RESERVATION_INTERVAL;
                if (minute >= CabConstants.DateTimeConstants.MINUTE_OF_HOUR) {
                    ++hour;
                    minute -= CabConstants.DateTimeConstants.MINUTE_OF_HOUR;
                }
            }
            return list.toArray(new Timepoint[list.size()]);
        }

        private Timepoint getTimePoint(String time) {
            String[] hm = time.split(":");
            return new Timepoint(Integer.parseInt(hm[0]), Integer.parseInt(hm[1]));
        }

        private int toMinute(int hours, int minutes) {
            return hours * CabConstants.DateTimeConstants.MINUTE_OF_HOUR + minutes;
        }
    }

    public AutoSettingsAdapter(List<AutoResvItemDecorator> list, Context context) {
        this.list = list;
        this.context = context;
        this.weeks = context.getResources().getStringArray(R.array.tutu_auto_weeks);
    }

    public void setList(List<AutoResvItemDecorator> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((AutoSettingViewHolder) holder).bind(list.get(position), position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.auto_reservation_setting_item, parent, false);
        return new AutoSettingViewHolder(view);
    }

}
