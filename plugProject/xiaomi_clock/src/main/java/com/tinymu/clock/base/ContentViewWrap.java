package com.tinymu.clock.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zimi.clockmyk.R;


public class ContentViewWrap implements StatusHandle {
    private FrameLayout mLoadRoot;
    private FrameLayout mErrorRoot;
    private int mLoadStatus = 0;
    private View.OnClickListener mRefreshClick = null;
    private int mMarginTop;
    private View cp = null;
    private TextView tvLoadingDesc = null;
    private View noData = null;
    private View noNet = null;
    private View error = null;
    private View vShadow = null;


    public ContentViewWrap(FrameLayout root) {
        mLoadRoot = root;
        mErrorRoot = mLoadRoot;
    }

    public ContentViewWrap(FrameLayout loadRoot, FrameLayout errorRoot) {
        mLoadRoot = loadRoot;
        mErrorRoot = errorRoot;
    }

    public void setErrorRoot(FrameLayout mErrorRoot) {
        this.mErrorRoot = mErrorRoot;
    }

    @Override
    public void clickError(View.OnClickListener l) {
        this.mRefreshClick = l;
    }

    public void clickError(View.OnClickListener l, int marginLeft, int marginTop, int marginRight, int marginBottom) {
        this.mRefreshClick = l;
        this.mMarginTop = marginTop;
    }

    public void onLoading(int type, String desc) {
        if (cp == null) {
            View load = View.inflate(mLoadRoot.getContext(), R.layout.activity_baseroot, null);
            mLoadRoot.addView(load);
            cp = load.findViewById(R.id.cp);
            tvLoadingDesc = (TextView) load.findViewById(R.id.tvLoadingDesc);
        }
        mLoadStatus = type;
        cp.setVisibility(View.VISIBLE);
        if (desc == null) {
            tvLoadingDesc.setVisibility(View.GONE);
        } else {
            tvLoadingDesc.setVisibility(View.VISIBLE);
            tvLoadingDesc.setText(desc);
        }
        if ((type & StatusHandle.TYPE_UNCLICK) == StatusHandle.TYPE_UNCLICK) {
            cp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        } else if ((type & StatusHandle.TYPE_UNCANCLE) == 0) {
            cp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cp.setVisibility(View.VISIBLE);
                }
            });
        } else {
            cp.setOnClickListener(null);
            cp.setClickable(false);
        }
    }


    public void onLoadingFinish(boolean dismissAll) {
        this.mLoadStatus = StatusHandle.TYPE_NORMAL;
        cp.setVisibility(View.INVISIBLE);
        if (dismissAll) {
            if (noData != null) {
                noData.setVisibility(View.GONE);
            }
            if (noNet != null) {
                noNet.setVisibility(View.GONE);
            }
            if (error != null) {
                error.setVisibility(View.GONE);
            }
        }
    }

    public void onNoData() {
        if (noData == null) {
            noData = View.inflate(mErrorRoot.getContext(), R.layout.include_nodata, null);
            noData.setOnClickListener(mRefreshClick);
        }
        if (noData.getParent() == null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
            layoutParams.topMargin = mMarginTop;
            mErrorRoot.addView(noData, layoutParams);
        }
        noData.setVisibility(View.VISIBLE);
    }

    public void onNoNetwork() {
        if (noNet == null) {
            noNet = View.inflate(mErrorRoot.getContext(), R.layout.include_nonetwork, null);
            noNet.setOnClickListener(mRefreshClick);
        }
        if (noNet.getParent() == null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
            layoutParams.topMargin = mMarginTop;
            mErrorRoot.addView(noNet, layoutParams);
        }
        noNet.setVisibility(View.VISIBLE);
    }

    public void onError(int errorCode) {
        if (error == null) {
            error = View.inflate(mErrorRoot.getContext(), R.layout.include_error, null);
            if (mRefreshClick != null) {
                error.setOnClickListener(mRefreshClick);
            }
        }
        if (error.getParent() == null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
            layoutParams.topMargin = mMarginTop;
            mErrorRoot.addView(error, layoutParams);
        }
        error.setVisibility(View.VISIBLE);
    }

    public View setShadow(int marginTop) {
        if (marginTop < 0) {
            if (vShadow != null) {
                mLoadRoot.removeView(vShadow);
                vShadow = null;
            }
        } else {
            if (vShadow == null) {
                vShadow = new View(mLoadRoot.getContext());
                mLoadRoot.addView(vShadow);
                setParams(marginTop);
                vShadow.setBackgroundResource(R.drawable.shadow_top);
            } else {
                setParams(marginTop);
                vShadow.requestLayout();
            }
        }
        return vShadow;
    }

    private void setParams(int marginTop) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) vShadow.getLayoutParams();
        params.height = (int) (vShadow.getContext().getResources().getDisplayMetrics().density * 4);
        params.topMargin = marginTop;
    }

    public void setError(View error) {
        this.error = error;
    }

    public void setNoNet(View noNet) {
        this.noNet = noNet;
    }

    public void setNoData(View noData) {
        this.noData = noData;
    }

    public void removeAllView() {
        mLoadRoot.removeAllViews();
        if (mErrorRoot != mLoadRoot)
            mErrorRoot.removeAllViews();
    }
}