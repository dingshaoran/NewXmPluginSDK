package com.tinymu.clock.base;

import android.view.View;
import android.widget.FrameLayout;

import com.zimi.clockmyk.R;

/**
 * Created by yxp on 16/7/2.
 */
public abstract class StatusActFragment extends BasePluginFragment {

    public View getContentView(int layout) {
        return View.inflate(mActivity, layout, null);
    }

    public ContentViewWrap injectStatus(View view) {//只有复写了getContentView 才知道返回的是个什么 view
        View flError = view.findViewById(R.id.flError);
        if (flError instanceof FrameLayout) {
            ((BasePluginActivity) xmPluginActivity()).mStatusHandler.setErrorRoot((FrameLayout) flError);
        }
        return ((BasePluginActivity) xmPluginActivity()).mStatusHandler;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden  ) {
            View flError = root.findViewById(R.id.flError);
            if (flError instanceof FrameLayout) {
                ((BasePluginActivity) xmPluginActivity()).mStatusHandler.setErrorRoot((FrameLayout) flError);
            }
        }
    }
}
