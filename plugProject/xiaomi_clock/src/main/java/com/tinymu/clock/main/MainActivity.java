package com.tinymu.clock.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tinymu.clock.alarmclock.AlarmClockFragment;
import com.tinymu.clock.base.BasePluginActivity;
import com.tinymu.clock.base.BasePluginFragment;
import com.tinymu.clock.notify.NotifyFragment;
import com.tinymu.clock.timecount.TimeCountFragment;
import com.zimi.clockmyk.R;

public class MainActivity extends BasePluginActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    public static final int SELECT_NONE = 0;
    public static final int SELECT_ALL = 1;
    public static final int SELECT_DELETE = 2;
    private SparseArray<BasePluginFragment> mFragments = new SparseArray<>(4);
    private BasePluginFragment mFragment;
    private RadioGroup rg;
    private View mVTitle;
    private View llBottom;
    private View tvCancel;
    private TextView tvAll;
    private View tvDelete;
    private View titleShare;
    private TextView tvTitle;
    private RadioButton tv0;

    @Override
    public int getcontentView() {
        return R.layout.activity_main;
    }

    @Override
    public void injectView(View contentView) {
        rg = (RadioGroup) findViewById(R.id.rg);
        tv0 = (RadioButton) findViewById(R.id.tv0);
        mVTitle = findViewById(R.id.title_bar);
        llBottom = findViewById(R.id.llBottom);
        tvCancel = findViewById(R.id.tvCancel);
        tvAll = (TextView) findViewById(R.id.tvAll);
        tvTitle = (TextView) findViewById(R.id.title_bar_title);
        tvDelete = findViewById(R.id.tvDelete);
        titleShare = findViewById(R.id.title_bar_share);
        tvCancel.setOnClickListener(this);
        tvAll.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        titleShare.setOnClickListener(this);
        rg.setOnCheckedChangeListener(this);
    }

    @Override
    public void afterInjectView(View view) {
        mHostActivity.setTitleBarPadding(findViewById(R.id.flTitle));
        titleShare.setVisibility(View.VISIBLE);
//        View tvAlarm = findViewById(R.id.tvAlarm);
//        View tvNotify = findViewById(R.id.tvNotify);
//        View tvCount = findViewById(R.id.tvCount);
//        tvAlarm.setOnClickListener(this);
//        tvNotify.setOnClickListener(this);
//        tvCount.setOnClickListener(this);
        mFragments.put(R.id.tv0, new AlarmClockFragment());
        mFragments.put(R.id.tv1, new NotifyFragment());
        mFragments.put(R.id.tv2, new TimeCountFragment());
        mFragments.put(R.id.tv3, new AlarmClockFragment());
        tv0.setChecked(true);
    }

    private void changeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = fragmentManager.beginTransaction();
        if (mFragment != null) {
            mTransaction.hide(mFragment);
        }
        int checkPos = rg.getCheckedRadioButtonId();
        String tag = String.valueOf(checkPos);
        mFragment = mFragments.get(checkPos);
        if (mFragment.getTag() != null) {
            mTransaction.show(mFragment);
        } else {
            Fragment addFragment = fragmentManager.findFragmentByTag(tag);
            if (addFragment == null) {
                mTransaction.add(R.id.flBody, mFragment, tag);
            } else if (addFragment == mFragment) {
                mTransaction.show(addFragment);
            } else {
                mTransaction.remove(addFragment);
                mTransaction.add(R.id.flBody, mFragment, tag);
            }
        }
        mTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        mFragment.onActivityClick(view);
        switch (view.getId()) {
            case R.id.title_bar_share:
                mVTitle.setVisibility(View.GONE);
                rg.setVisibility(View.GONE);
                break;
            case R.id.tvDelete:
                break;
            case R.id.tvAll:
                break;
            case R.id.tvCancel:
                mVTitle.setVisibility(View.VISIBLE);
                rg.setVisibility(View.VISIBLE);
                break;
//            case R.id.tvAlarm:
//                startActivity(new Intent(this, AlarmClockFragment.class));
//                break;
//            case R.id.tvNotify:
//                startActivity(new Intent(this, NotifyTimeActivity.class));
//                break;
//            case R.id.tvCount:
//                startActivity(new Intent(this, TimeCountActivity.class));
//                break;
        }
    }

    public void onFragmentCall(int event) {
        switch (event) {
            case SELECT_ALL:
                tvAll.setText(R.string.home_delete_all);
                break;
            case SELECT_NONE:
                tvAll.setText(R.string.home_delete_none);
                break;
            case SELECT_DELETE:
                tvCancel.performClick();
                break;
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId > 0) {
            tvTitle.setText(((TextView) group.findViewById(checkedId)).getText());
            changeFragment();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragment.onActivityResult(requestCode, resultCode, data);
    }
}

