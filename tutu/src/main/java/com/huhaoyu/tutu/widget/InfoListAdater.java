package com.huhaoyu.tutu.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huhaoyu.tutu.R;
import com.huhaoyu.tutu.entity.ReservationInfoWrapper;

/**
 * Personal info list adapter
 * Created by coderhuhy on 15/11/30.
 */
public class InfoListAdater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LogTag = InfoListAdater.class.getCanonicalName();

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    private ReservationInfoWrapper infos;
    private Context context;

    public InfoListAdater(ReservationInfoWrapper infos, Context context) {
        super();
        this.infos = infos;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.info_header_item, parent, false);
                return new InfoHeaderItemHolder(view);
            }
            case TYPE_CELL: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_reservation_item, parent, false);
                return new InfoItemHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((InfoHeaderItemHolder) holder).bind(infos, this.context);
                break;
            case TYPE_CELL:
                ((InfoItemHolder) holder).bind(infos.get(position), position, this.context);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

}
