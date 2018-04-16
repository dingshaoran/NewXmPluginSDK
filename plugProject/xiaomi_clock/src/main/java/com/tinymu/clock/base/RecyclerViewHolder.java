package com.tinymu.clock.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView .ViewHolder
 *
 * @author zoudong
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    EViewHolder viewHolder;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        if (itemView.getLayoutParams() == null) {
            itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        } else {
            itemView.getLayoutParams().width = -1;
        }
        viewHolder = EViewHolder.getViewHolder(itemView);
        viewHolder.setViewHolder(this);
    }

    public EViewHolder getViewHolder() {
        return viewHolder;
    }
}
