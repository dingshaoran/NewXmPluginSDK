package com.tinymu.clock.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * RecyclerView  Baseadapter
 *
 * @author zoudong
 */
public abstract class RecyclerAdapter<T> extends android.support.v7.widget.RecyclerView.Adapter<RecyclerViewHolder> implements OnDataChanged {
    protected static final int TYPE_NORMAL = -1;
    protected static final int TYPE_HEADER = 0;
    protected Context mContext;
    protected List<T> mList;
    HeaderAndFooterRecyclerViewAdapter wrapAdapter;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;

    public RecyclerAdapter(Context mContext, List<T> mList) {
        this.mContext = mContext;
        this.mList = mList == null ? new ArrayList<T>() : mList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = onCreateView(parent, viewType);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);
        return viewHolder;
    }

    protected View onCreateView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(onCreateViewLayoutID(viewType), parent, false);
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_NORMAL;
    }

    public T getItem(int position) {
        if (position < 0 || mList == null || mList.size() <= position) return null;
        return mList.get(position);
    }

    public void add(T object) {
        mList.add(object);
        notifyItemInserted(getItemCount());
        notifyItemRangeChanged(getItemCount(), 1);
    }

    protected abstract int onCreateViewLayoutID(int viewType);

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        onSetView(holder.getViewHolder(), getItem(position), position);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        onItemClickListener.onItemClick(null, v, holder.getLayoutPosition() - (wrapAdapter == null ? 0 : wrapAdapter.getHeaderViewsCount()), holder.getItemId());
                    } catch (Throwable e) {
                    }
                }
            });
        }
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                                       @Override
                                                       public boolean onLongClick(View v) {
                                                           return onItemLongClickListener.onItemLongClick(null, v, holder.getLayoutPosition() - (wrapAdapter == null ? 0 : wrapAdapter.getHeaderViewsCount()), holder.getItemId());
                                                       }
                                                   }
            );
        }

    }

    public void swap(int start, int target) {
    }

    public abstract void onSetView(EViewHolder holder, T item, int position);

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void set(List<T> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void set(int index, T object) {
        mList.set(index, object);
        notifyItemChanged(index);
    }

    public void remove(int index) {
        if (mList.size() > index && index >= 0) {
            mList.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void addAll(Collection<? extends T> collection) {
        mList.addAll(collection);
        notifyDataSetChanged();
    }

    public void addAll(int index, Collection<? extends T> collection) {
        mList.addAll(index, collection);
        notifyDataSetChanged();
    }

    public void insert(int index, T object) {
        mList.add(index, object);
        notifyItemInserted(index);
        notifyItemRangeChanged(index + 1, getItemCount() - 1);
    }

    public List<T> getList() {
        return mList;
    }


    public void insert(int index, Collection<? extends T> object) {
        mList.addAll(index, object);
        notifyItemInserted(index);
        notifyItemRangeChanged(index + object.size(), getItemCount() - object.size());
    }

    @Override
    public void notifyDataChanged(boolean success) {
        notifyDataSetChanged();
    }

    public void remove(T object) {
        mList.remove(object);
        super.notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public void sort(Comparator<? super T> comparator) {
        Collections.sort(mList, comparator);
        notifyDataSetChanged();
    }
}
