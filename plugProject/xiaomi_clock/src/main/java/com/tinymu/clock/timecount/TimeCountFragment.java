package com.tinymu.clock.timecount;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.tinymu.clock.DeviceClock;
import com.tinymu.clock.alarmclock.AlarmClockBean;
import com.tinymu.clock.base.StatusActFragment;
import com.tinymu.clock.main.MainActivity;
import com.tinymu.clock.utils.FormatUtils;
import com.tinymu.clock.utils.LogUtils;
import com.tinymu.clock.widget.TimeDownView;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.common.ui.widget.TimePicker;
import com.xiaomi.smarthome.device.api.Callback;
import com.zimi.clockmyk.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

/**
 * Created by mi on 17-10-24.
 */

public class TimeCountFragment extends StatusActFragment {


    private static final String HISTORYTIME = "timecount_history";
    private static final String SPLIT = " H:";
    private TimePicker mViewDate;
    private TextView tvWhite;
    private TextView timeTip;
    private TextView tvBlue;
    private RecyclerView rv;
    private List<AlarmClockBean> mList = new ArrayList<>();
    private Executor e = Executors.newSingleThreadExecutor();
    private SharedPreferences mSP;
    private View llRun;
    private View llCreate;
    private TimeDownView mTdv;
    private View flTop;
    private View llControl;
    private long mStartTime;

    @Override
    public int getcontentView() {
        return R.layout.fragment_timecount;
    }

    @Override
    public void injectView(View contentView) {
        mViewDate = (TimePicker) contentView.findViewById(R.id.time_picker);
        llRun = contentView.findViewById(R.id.llRun);
        llCreate = contentView.findViewById(R.id.llCreate);
        flTop = contentView.findViewById(R.id.flTop);
        llControl = contentView.findViewById(R.id.llControl);
        mTdv = (TimeDownView) contentView.findViewById(R.id.tdv);
        tvWhite = (TextView) contentView.findViewById(R.id.tvWhite);
        timeTip = (TextView) contentView.findViewById(R.id.time_tip);
        tvBlue = (TextView) contentView.findViewById(R.id.tvBlue);
        rv = (RecyclerView) contentView.findViewById(R.id.rv);
        timeTip.setOnClickListener(this);
        tvWhite.setOnClickListener(this);
        tvBlue.setOnClickListener(this);
    }

    @Override
    public void afterInjectView(View view) {
        tvWhite.setVisibility(View.GONE);
        mViewDate.setIs24HourView(true);
        mViewDate.setCurrentMinute(0);
        mViewDate.setCurrentHour(0);
        rv.setLayoutManager(new LinearLayoutManager(activity()));
        TimeCountAdapter countAdapter = new TimeCountAdapter(activity(), mList, this);
        rv.setAdapter(countAdapter);
        countAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long aLong = mList.get(i).datetime;
                mViewDate.setCurrentHour((int) (aLong / 60));
                mViewDate.setCurrentMinute((int) (aLong % 60));
            }
        });
        requestList();
    }

    private void requestList() {
        e.execute(new Runnable() {
            private List<AlarmClockBean> list = new ArrayList<>();

            @Override
            public void run() {
                mSP = activity().getSharedPreferences(DeviceClock.SHAREPREFERENCE, Context.MODE_PRIVATE);
                String string = mSP.getString(HISTORYTIME, "");
                String[] split = string.split(SPLIT);
                for (String s : split) {
                    long l = FormatUtils.parseLong(s, -1);
                    if (l != -1) {
                        AlarmClockBean alarmClockBean = new AlarmClockBean();
                        alarmClockBean.datetime = l;
                        list.add(alarmClockBean);
                    }
                }
                rv.post(new Runnable() {
                    @Override
                    public void run() {
                        mList.clear();
                        mList.addAll(list);
                        rv.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.time_tip:
                long total = mViewDate.getCurrentMinute() + mViewDate.getCurrentHour() * 60;
                if (contains(total)) {
                    Toast.makeText(activity(), getString(R.string.timecount_exit, mViewDate.getCurrentHour(), mViewDate.getCurrentMinute()), Toast.LENGTH_LONG).show();
                } else {
                    AlarmClockBean alarmClockBean = new AlarmClockBean();
                    alarmClockBean.datetime = total;
                    mList.add(alarmClockBean);
                    rv.getAdapter().notifyDataSetChanged();
                }
                break;
            case R.id.tvBlue:
                if (tvWhite.getVisibility() == View.VISIBLE) {
                    requestOperate(mTdv.getStart() ? DeviceClock.OPERATION_PAUSE : DeviceClock.OPERATION_RUNNING);
                } else {
                    requestOperate(DeviceClock.OPERATION_CREATE);
                }
                break;
            case R.id.tvWhite:
                new MLAlertDialog.Builder(activity()).setTitle(getString(R.string.timecount_stop_ask))
                        .setNegativeButton(R.string.timecount_stop_ask_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (dialogInterface != null) {
                                    dialogInterface.dismiss();
                                }
                            }
                        }).setPositiveButton(R.string.timecount_stop_ask_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestOperate(DeviceClock.OPERATION_DELETE);
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                }).show();
                break;
        }
    }

    private boolean contains(long total) {
        for (AlarmClockBean bean : mList) {
            if (bean.datetime == total) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_share://点击了activity的编辑
                flTop.setVisibility(View.GONE);
                llControl.setVisibility(View.GONE);
                ((TimeCountAdapter) rv.getAdapter()).setStatusEdit(true);
                rv.getAdapter().notifyDataSetChanged();
                break;
            case R.id.tvDelete:
                for (int i = mList.size() - 1; i >= 0; i--) {
                    if (mList.get(i).delete) {
                        mList.remove(i);
                    }
                }
                ((MainActivity) xmPluginActivity()).onFragmentCall(MainActivity.SELECT_DELETE);
                rv.getAdapter().notifyDataSetChanged();
                break;
            case R.id.tvAll:
                if (((TextView) view).getText().equals(getString(R.string.home_delete_all))) {
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).delete = true;
                    }
                    ((MainActivity) xmPluginActivity()).onFragmentCall(MainActivity.SELECT_NONE);
                } else {
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).delete = false;
                    }
                    ((MainActivity) xmPluginActivity()).onFragmentCall(MainActivity.SELECT_ALL);
                }
                rv.getAdapter().notifyDataSetChanged();
                break;
            case R.id.tvCancel:
                flTop.setVisibility(View.VISIBLE);
                llControl.setVisibility(View.VISIBLE);
                ((TimeCountAdapter) rv.getAdapter()).setStatusEdit(false);
                rv.getAdapter().notifyDataSetChanged();
                break;
        }
    }

    private void requestOperate(final String operate) {
        try {
            JSONObject params = new JSONObject();
            params.put("operation", operate);
            params.put("parser_timestamp", System.currentTimeMillis());
            JSONObject data = new JSONObject();
            int total = mViewDate.getCurrentHour() * 60 + mViewDate.getCurrentMinute();
            data.put("type", DeviceClock.TYPE_TIMER);
            data.put("offset", total);
            data.put("circle", DeviceClock.CIRCLE_ONCE);
            data.put("volume", 100);
            data.put("reminder_audio", DeviceClock.AUDIO);
            if (DeviceClock.OPERATION_CREATE.equals(operate)) {
                mStartTime = System.currentTimeMillis() + total * 1000;
            }
            data.put("event_timestamp", mStartTime);
            JSONArray arr = new JSONArray();
            arr.put(data);
            params.put("data", arr);
            DeviceClock.getDevice(getDeviceStat()).callMethod("set_alarm", params, new Callback<String>() {
                @Override
                public void onSuccess(String ss) {
                    try {
                        JSONObject jo = new JSONObject(ss);
                        JSONArray result = jo.optJSONArray("result");
                        if (result != null && result.length() > 0) {
                            JSONObject jsonObject = result.optJSONObject(0);
                            if (jsonObject != null && DeviceClock.RESULT_OK.equals(jsonObject.optString("ack"))) {
                                if (DeviceClock.OPERATION_CREATE.equals(operate)) {
                                    llRun.setVisibility(View.VISIBLE);
                                    tvWhite.setVisibility(View.VISIBLE);
                                    llCreate.setVisibility(View.GONE);
                                    tvBlue.setText(R.string.timecount_pause);
                                    mTdv.setStart(true);
                                } else if (DeviceClock.OPERATION_PAUSE.equals(operate)) {
                                    tvBlue.setText(R.string.timecount_continue);
                                    mTdv.setStart(false);
                                } else if (DeviceClock.OPERATION_RUNNING.equals(operate)) {
                                    tvBlue.setText(R.string.timecount_pause);
                                    mTdv.setStart(true);
                                } else if (DeviceClock.OPERATION_DELETE.equals(operate)) {
                                    mViewDate.setCurrentMinute(0);
                                    mViewDate.setCurrentHour(0);
                                    llRun.setVisibility(View.GONE);
                                    tvWhite.setVisibility(View.GONE);
                                    llCreate.setVisibility(View.VISIBLE);
                                    tvBlue.setText(R.string.timecount_start);
                                    mTdv.setStart(false);
                                }
                            }
                        } else {
                            onError(ERROR_DATA);
                        }
                    } catch (Exception e) {
                        onError(ERROR_JSON);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    onError(i);
                }
            });
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveLocal();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveLocal();
    }

    private void saveLocal() {
        e.execute(new Runnable() {
            @Override
            public void run() {
                if (mSP != null) {
                    StringBuilder stringBuilder = new StringBuilder(mList.size() * 10);
                    for (AlarmClockBean bean : mList) {
                        stringBuilder.append(bean.datetime).append(SPLIT);
                    }
                    mSP.edit().putString(HISTORYTIME, stringBuilder.toString()).apply();
                }
            }
        });
    }
}
