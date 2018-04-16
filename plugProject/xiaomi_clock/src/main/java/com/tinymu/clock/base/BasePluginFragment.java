package com.tinymu.clock.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xiaomi.smarthome.device.api.BaseFragment;
import com.xiaomi.smarthome.device.api.DeviceStat;

/**
 * Created by yxp on 16/7/2.
 */
public abstract class BasePluginFragment extends BaseFragment implements StatusHandle, StartActivity, View.OnClickListener {

    protected Activity mActivity;
    protected View root;
    private boolean loadOnce;
    private ContentViewWrap mStatusHandler;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    public Activity getThis() {
        return this.mActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root != null) {
            ViewGroup parent = (ViewGroup) root.getParent();
            if (parent != null) {
                parent.removeView(root);
            }
            return root;
        }
        root = getContentView(getcontentView());
        injectView(root);
        mStatusHandler = injectStatus(root);
        Log.i("BaseFragment", getClass().getName());
        afterInjectView(root);
        if (getUserVisibleHint() && !loadOnce) {
            loadOnce = true;
            getData();
        }
        return root;
    }

    public View getContentView(int layout) {
        View view = View.inflate(mActivity, layout, null);
        if (view instanceof FrameLayout) {
            return (FrameLayout) view;
        } else {
            FrameLayout frameLayout = new FrameLayout(mActivity);
            frameLayout.addView(view);
            return frameLayout;
        }
    }

    public ContentViewWrap injectStatus(View view) {//只有复写了getContentView 才知道返回的是个什么 view
//        ContentViewWrap viewWrap = new ContentViewWrap((FrameLayout) view);
//        viewWrap.setShadow(getResources().getDimensionPixelOffset(R.dimen.height_50dp));
//        return viewWrap;
        return ((BasePluginActivity) xmPluginActivity()).mStatusHandler;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (root != null && isVisibleToUser && !loadOnce) {//如果View、可见、没加载过都是 ture 则第一次加载数据
            loadOnce = true;
            getData();
        }
    }

    public DeviceStat getDeviceStat() {
        return xmPluginActivity().getDeviceStat();
    }

    public void injectView(View contentView) {
    }


    public abstract int getcontentView();

    public abstract void afterInjectView(View view);

    public void getData() {
    }

    public Activity activity() {
        return mActivity;
    }

    /**
     * 显示 dialog 如果0可以取消,点击
     *
     * @param type 可以传值控制是否可以取消和点击下面的界面,如 不可点击并不可取消 DialogHandle.TYPE_UNCANCLE | DialogHandle.TYPE_UNCLICK
     */
    public void onLoading(int type) {
        onLoading(type, "");
    }

    @Override
    public void clickError(View.OnClickListener l) {
        if (mStatusHandler != null) {
            mStatusHandler.clickError(l);
        }
    }

    /**
     * 如果子类要展示 onNoData,onNoNetwork,onError 界面,请实现此方法并传入OnClickListener
     *
     * @param l 当点击 onNoData,onNoNetwork,onError 界面是回调
     */
    public void clickError(View.OnClickListener l, int marginLeft, int marginTop, int marginRight, int marginBottom) {
        if (mStatusHandler != null) {
            mStatusHandler.clickError(l, marginLeft, marginTop, marginRight, marginBottom);
        }
    }

    /**
     * 显示 dialog 如果0可以取消,点击
     *
     * @param type 可以传值控制是否可以取消和点击下面的界面,如 不可点击并不可取消 StatusHandle.TYPE_UNCANCLE | DialogHandle.TYPE_UNCLICK
     * @param desc 显示的提示信息
     */
    public void onLoading(int type, String desc) {
        if (mStatusHandler != null) {
            mStatusHandler.onLoading(type, desc);
        }
    }

    public final void onLoadingFinish(boolean dismissAll) {
        if (mStatusHandler != null) {
            mStatusHandler.onLoadingFinish(dismissAll);
        }
    }

    @Override
    public void onClick(View v) {
    }

    public final void onNoData() {
        if (mStatusHandler != null) {
            mStatusHandler.onNoData();
        }
    }

    public final void onNoNetwork() {
        if (mStatusHandler != null) {
            mStatusHandler.onNoNetwork();
        }
    }

    public void setNoData(View noData) {
        if (mStatusHandler != null) {
            mStatusHandler.setNoData(noData);
        }
    }

    public void setNoNet(View noNet) {
        if (mStatusHandler != null) {
            mStatusHandler.setNoNet(noNet);
        }
    }

    public void setError(View error) {
        if (mStatusHandler != null) {
            mStatusHandler.setError(error);
        }
    }

    public final void onError(int error) {
        if (mStatusHandler != null) {
            mStatusHandler.onError(error);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, intent.getComponent().getClassName(), requestCode);
    }

    public void onActivityClick(View view) {
    }
}
