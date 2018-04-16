package com.tinymu.clock.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.xiaomi.smarthome.device.api.XmPluginBaseActivity;
import com.zimi.clockmyk.R;


public abstract class BasePluginActivity extends XmPluginBaseActivity implements StatusHandle, StartActivity, View.OnClickListener {

    protected ContentViewWrap mStatusHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {//final 新版本听云要去掉
        beforeSuper(savedInstanceState);
        super.onCreate(savedInstanceState);
        View view = getContentView(getcontentView());
        setContentView(view);
        View title_bar_return = findViewById(R.id.title_bar_return);
        if (title_bar_return != null) {
            title_bar_return.setOnClickListener(this);
        }
        mStatusHandler = injectStatus(view);
        injectView(view);
        afterInjectView(view);
        // 【友盟推送】 统计应用启动数据
        //        PushAgent.getInstance(this).onAppStart();
    }

    public View getContentView(int layout) {
        View view = View.inflate(activity(), layout, null);
        if (view instanceof FrameLayout) {
            return (FrameLayout) view;
        } else {
            FrameLayout frameLayout = new FrameLayout(activity());
            frameLayout.addView(view);
            return frameLayout;
        }
    }

    public ContentViewWrap injectStatus(View view) {
        View flError = findViewById(R.id.flError);
        if (flError instanceof FrameLayout) {
            return new ContentViewWrap((FrameLayout) view, (FrameLayout) flError);
        } else {
            return new ContentViewWrap((FrameLayout) view);
        }
//        ContentViewWrap viewWrap = new ContentViewWrap((FrameLayout) view);
//        viewWrap.setShadow(getResources().getDimensionPixelOffset(R.dimen.height_50dp));
//        return viewWrap;
    }

    public Activity getThis() {
        return activity();
    }

    public void injectView(View contentView) {
    }

    public void beforeSuper(Bundle savedInstanceState) {
    }


    public abstract int getcontentView();

    public abstract void afterInjectView(View view);

    /**
     * 显示 dialog 如果0可以取消,点击
     *
     * @param type 可以传值控制是否可以取消和点击下面的界面,如 不可点击并不可取消 DialogHandle.TYPE_UNCANCLE | DialogHandle.TYPE_UNCLICK
     */
    public void onLoading(int type) {
        onLoading(type, null);
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

    /**
     * 如果子类要展示 onNoData,onNoNetwork,onError 界面,请实现此方法并传入OnClickListener
     *
     * @param l 当点击 onNoData,onNoNetwork,onError 界面是回调
     */
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

    public final void onError(int error) {
        if (mStatusHandler != null) {
            mStatusHandler.onError(error);
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

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, intent.getComponent().getClassName(), requestCode);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_bar_return) {
            onBackPressed();
        }
    }
}
