package com.huhaoyu.tutu.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.ReservationStatesWrapper;

/**
 * Header recycler view adapter
 * Created by coderhuhy on 15/11/21.
 */
public class ReservationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ReservationStatesWrapper states;
    Context context;

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    public ReservationListAdapter(ReservationStatesWrapper states, Context context) {
        this.states = states;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return states.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reservation_summary_item, parent, false);
                return new ReservationSummaryItemHolder(view);
            }
            case TYPE_CELL: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reservation_state_item, parent, false);
                return new ReservationItemHolder(view);
            }
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((ReservationSummaryItemHolder) holder).bind(states, position, this.context);
                break;
            case TYPE_CELL:
                ((ReservationItemHolder) holder).bind(states.get(position), this.context);
                break;
        }
    }

}
