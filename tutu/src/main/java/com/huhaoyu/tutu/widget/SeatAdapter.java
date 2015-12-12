package com.huhaoyu.tutu.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huhaoyu.tutu.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mu.lab.thulib.thuseat.SeatState;

/**
 * Seat adapter
 * Created by coderhuhy on 15/12/9.
 */
public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private static final String LogTag = SeatAdapter.class.getCanonicalName();
    private static final int HEADER_COUNT = 1;
    private static final int HEADER_TYPE = 0;
    private static final int EVEN_ITEM_TYPE = 1;
    private static final int ODD_ITEM_TYPE = 2;

    List<SeatState> list;
    Context context;

    protected abstract class SeatViewHolder extends RecyclerView.ViewHolder {

        public SeatViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(SeatState state);
    }

    protected class HeaderViewHolder extends SeatViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(SeatState state) {
        }
    }

    protected class ItemViewHolder extends SeatViewHolder {

        @Bind(R.id.area_tv)
        TextView areaTv;
        @Bind(R.id.state_tv)
        TextView stateTv;
        @Bind(R.id.occupied_tv)
        TextView occupiedTv;
        @Bind(R.id.rest_tv)
        TextView restTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(SeatState state) {
            String area = state.getArea();
            SeatState.State s = state.getState();
            String current;
            switch (s) {
                case Busy:
                    current = context.getString(R.string.tutu_seat_state_busy);
                    break;
                case Well:
                    current = context.getString(R.string.tutu_seat_state_well);
                    break;
                case Idle:
                    current = context.getString(R.string.tutu_seat_state_idle);
                    break;
                default:
                    current = context.getString(R.string.tutu_seat_unknown_state);
            }
            String occupied = "" + state.getOccupied();
            String rest = "" + state.getRest();

            areaTv.setText(area);
            stateTv.setText(current);
            occupiedTv.setText(occupied);
            restTv.setText(rest);
        }

    }

    public SeatAdapter(List<SeatState> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_TYPE;
        } else if (position % 2 == 0) {
            return EVEN_ITEM_TYPE;
        } else {
            return ODD_ITEM_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(SeatViewHolder holder, int position) {
        int realPos = position - HEADER_COUNT;
        if (realPos >= 0 && realPos < list.size()) {
            holder.bind(list.get(realPos));
        }
    }

    @Override
    public SeatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seat_header_item, parent, false);
                return new HeaderViewHolder(view);
            case EVEN_ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seat_even_item, parent, false);
                return new ItemViewHolder(view);
            case ODD_ITEM_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seat_odd_item, parent, false);
                return new ItemViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return list.size() + HEADER_COUNT;
    }
}
