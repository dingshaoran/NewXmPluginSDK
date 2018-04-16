package com.tinymu.clock.base;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by zoudong on 16/7/14.
 */
public class HeaderAndFooterRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER_VIEW = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER_VIEW = Integer.MAX_VALUE >> 1;
    private static final int TYPE_INNER = 0;

    /**
     * RecyclerView使用的，真正的Adapter
     */
    private RecyclerView.Adapter mInnerAdapter;

    private ArrayList<RecyclerView.ViewHolder> mHeaderViews = new ArrayList<>(2);
    private ArrayList<RecyclerView.ViewHolder> mFooterViews = new ArrayList<>(1);

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int headerViewsCountCount = getHeaderViewsCount();
            notifyItemRangeChanged(fromPosition + headerViewsCountCount, toPosition + headerViewsCountCount + itemCount);
        }
    };

    public HeaderAndFooterRecyclerViewAdapter(RecyclerView.Adapter innerAdapter) {
        setAdapter(innerAdapter);
    }

    /**
     * 设置adapter
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {

        if (adapter != null) {
            if (adapter instanceof RecyclerAdapter) {
                ((RecyclerAdapter) adapter).wrapAdapter = this;
            }
        }

        if (mInnerAdapter != null) {
            notifyItemRangeRemoved(getHeaderViewsCount(), mInnerAdapter.getItemCount());
            mInnerAdapter.unregisterAdapterDataObserver(mDataObserver);
        }

        this.mInnerAdapter = adapter;
        mInnerAdapter.registerAdapterDataObserver(mDataObserver);
        notifyItemRangeInserted(getHeaderViewsCount(), mInnerAdapter.getItemCount());
    }

    public RecyclerView.Adapter getInnerAdapter() {
        return mInnerAdapter;
    }

    public void addHeaderView(RecyclerView.ViewHolder header) {
        if (header == null) {
            throw new RuntimeException("header is null");
        }
        int indexOf = mHeaderViews.indexOf(header);
        if (indexOf > -1) { //已经存在  移动即可
            if (indexOf != mHeaderViews.size() - 1) {
                this.notifyItemMoved(indexOf, mHeaderViews.size() - 1);
            }
        } else {
            mHeaderViews.add(header);
            this.notifyItemInserted(mHeaderViews.size() - 1);
            notifyItemRangeChanged(mHeaderViews.size(), getItemCount() - 1);
        }
    }

    public void addHeaderView(int index, RecyclerView.ViewHolder header) {

        if (header == null) {
            throw new RuntimeException("header is null");
        }
        int indexOf = mHeaderViews.indexOf(header);
        if (indexOf > -1) { //已经存在  移动即可
            if (indexOf != index) {
                this.notifyItemMoved(indexOf, index);
            }
        } else {
            if (index > mHeaderViews.size() - 1) {
                mHeaderViews.add(header);
                this.notifyItemInserted(mHeaderViews.size() - 1);
                notifyItemRangeChanged(mHeaderViews.size(), getItemCount() - 1);

            } else {
                mHeaderViews.add(index, header);
                this.notifyItemInserted(index);
                notifyItemRangeChanged(index + 1, getItemCount() - 1);

            }
        }
    }

    public void notifyDataSetChanged(RecyclerView.ViewHolder header) {
        int ii = mHeaderViews.indexOf(header);
        if (ii >= 0) {
            this.notifyItemChanged(ii);
        }
    }

    public void removeHeaderView(RecyclerView.ViewHolder view) {
        int i = mHeaderViews.indexOf(view);
        if (i >= 0) {
            mHeaderViews.remove(i);
            this.notifyDataSetChanged();
        }

    }

    public void addFooterView(RecyclerView.ViewHolder footer) {
        if (footer == null) {
            throw new RuntimeException("footer is null");
        }
        int indexOf = mFooterViews.indexOf(footer);
        if (indexOf > -1) { //已经存在  移动即可
            if (indexOf != mFooterViews.size() - 1) {
                this.notifyItemMoved(indexOf, mFooterViews.size() - 1);
            }
        } else {
            mFooterViews.add(footer);
            this.notifyItemInserted(getItemCount());
        }
    }

    public void removeFooterView(RecyclerView.ViewHolder view) {
        if (mFooterViews.remove(view)) {
            this.notifyDataSetChanged();
        }
    }

    public int getHeaderViewsCount() {
        return mHeaderViews.size();
    }

    public int getFooterViewsCount() {
        return mFooterViews.size();
    }

    public boolean isHeader(int position) {
        return getHeaderViewsCount() > 0 && position == 0;
    }

    public boolean isFooter(int position) {
        int lastPosition = getItemCount() - 1;
        return getFooterViewsCount() > 0 && position == lastPosition;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int headerViewsCountCount = getHeaderViewsCount();
        if (position >= headerViewsCountCount && position < headerViewsCountCount + mInnerAdapter.getItemCount()) {
            mInnerAdapter.onBindViewHolder(holder, position - headerViewsCountCount);
        } else {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderViewsCount() + getFooterViewsCount() + mInnerAdapter.getItemCount();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int headerSize = mHeaderViews.size();
        if (viewType < TYPE_HEADER_VIEW + headerSize) {//header
            return mHeaderViews.get(viewType - TYPE_HEADER_VIEW);
        } else if (viewType >= TYPE_FOOTER_VIEW && mFooterViews.size() > 0) {//footer
            return mFooterViews.get(viewType - TYPE_FOOTER_VIEW);
        } else {//inner
            return mInnerAdapter.onCreateViewHolder(parent, viewType - TYPE_INNER);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int headerSize = mHeaderViews.size();
        if (position < headerSize) {//header
            return TYPE_HEADER_VIEW + position;
        } else if (headerSize <= position && position < headerSize + mInnerAdapter.getItemCount()) {//inner
            int innerItemViewType = mInnerAdapter.getItemViewType(position - headerSize);
            return TYPE_INNER + innerItemViewType;
        } else {//footer
            return TYPE_FOOTER_VIEW + (position - headerSize - mInnerAdapter.getItemCount());
        }
    }
}