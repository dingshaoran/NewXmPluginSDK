package com.tinymu.clock.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EViewHolder {
    private final SparseArray<View> mViewArray;
    private View mConvertView;
    private RecyclerView.ViewHolder viewHolder;

    private EViewHolder(View view) {
        mViewArray = new SparseArray<View>();
        mConvertView = view;
        mConvertView.setTag(this);
    }

    private EViewHolder(Context context, int layoutResID, int position, ViewGroup parent) {
        mViewArray = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutResID, parent, false);
        mConvertView.setTag(this);
    }

    public static EViewHolder getInstanceBy(Context context, int layoutResID, int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            return new EViewHolder(context, layoutResID, position, parent);
        return (EViewHolder) convertView.getTag();
    }

    public static EViewHolder getInstanceByNoConvert(Context context, int layoutResID, int position, View convertView, ViewGroup parent) {
        return new EViewHolder(context, layoutResID, position, parent);
    }

    public static EViewHolder getViewHolder(View view) {
        EViewHolder viewHolder = (EViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new EViewHolder(view);
            view.setTag(viewHolder);
        }
        return viewHolder;
    }

    public RecyclerView.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(RecyclerView.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V getViewById(int viewId) {
        View retView = mViewArray.get(viewId);
        if (retView == null) {
            retView = mConvertView.findViewById(viewId);
            mViewArray.put(viewId, retView);
        }
        return (V) retView;
    }

    public View getConvertView() {
        return mConvertView;
    }
}
