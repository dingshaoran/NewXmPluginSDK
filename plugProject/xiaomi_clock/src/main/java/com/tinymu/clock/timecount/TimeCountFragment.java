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
import com.tinymu.clock.widget.TimeDownPicker;
import com.tinymu.clock.widget.TimeDownView;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.device.api.Callback;
import com.zimi.clockmyk.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by mi on 17-10-24.
 */

public class TimeCountFragment extends StatusActFragment implements TimeDownView.OnTimeDownListener, DeviceClock.SubscribeLisenter {

    private static final String HISTORYTIME = "timecount_history";
    private static final String SPLIT = " H:";
    private static final String TAG = "TimeCountFragment";
    private static final String STATUS_RUNNING = "running";
    private static final String STATUS_RESUME= "resume";
    private static final String STATUS_PAUSE = "pause";
    private static final String STATUS_NONE = "none";
    private static final String COUNTDOWN = "count_down";
    private static final String[] EVENTS = new String[]{COUNTDOWN};
    private TimeDownPicker mViewDate;
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
    private long mEventTime;

    @Override
    public void onSubscribeData(String data) {
        try {
            JSONArray arr = new JSONArray(data);
            if (arr.length() > 0) {
                JSONObject jsonObject = arr.optJSONObject(0);
                JSONArray result = jsonObject.optJSONArray("value");
                if (result != null && result.length() >= 3) {
                    String status = result.optString(0);
                    setStatus(result.optLong(1) * 1000 + result.optLong(2) * 1000, status);
                }
            }
        } catch (Exception e1) {
            LogUtils.e(TimeCountFragment.TAG, e1);
        }
    }

    @Override
    public int getcontentView() {
        return R.layout.fragment_timecount;
    }

    @Override
    public void injectView(View contentView) {
        mViewDate = (TimeDownPicker) contentView.findViewById(R.id.time_picker);
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
        mViewDate.setCurrentMinute(0);
        mViewDate.setCurrentHour(0);
        rv.setLayoutManager(new LinearLayoutManager(activity()));
        TimeCountAdapter countAdapter = new TimeCountAdapter(activity(), mList, this);
        rv.setAdapter(countAdapter);
        mTdv.setOnTimeDownListener(this);
        countAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long aLong = mList.get(i).datetime;
                mViewDate.setCurrentHour((int) (aLong / 60));
                mViewDate.setCurrentMinute((int) (aLong % 60));
            }
        });
        requestList();
        requestData();
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

    private void requestData() {
        try {
            JSONArray params = new JSONArray();
//            params.put("operation", DeviceClock.OPERATION_QUERY);
//            params.put("parser_timestamp", System.currentTimeMillis());
//            params.put("index", 0);
//            params.put("req_type", DeviceClock.TYPE_TIMER);
            DeviceClock.getDevice(getDeviceStat()).callMethod("get_count_down", params, new Callback<String>() {
                @Override
                public void onSuccess(String ss) {
                    try {
                        JSONObject jo = new JSONObject(ss);
                        JSONArray result = jo.optJSONArray("result");
                        if (result != null && result.length() >= 3) {
                            String status = result.optString(0);
                            setStatus(result.optLong(1) * 1000 + result.optLong(2) * 1000, status);
                            if (!STATUS_NONE.equals(status)) {
                                DeviceClock.getDevice(getDeviceStat()).subscribeEvent(TimeCountFragment.this, EVENTS);
                            }
                        }
                    } catch (Exception e) {
                        onError(ERROR_JSON);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    onError(i);
                    LogUtils.i(TAG, i + s);
                }
            });
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
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
                    mList.add(0, alarmClockBean);
                    rv.getAdapter().notifyDataSetChanged();
                }
                break;
            case R.id.tvBlue:
                if (tvWhite.getVisibility() == View.VISIBLE) {
                    requestOperate(mTdv.getStart() ? DeviceClock.OPERATION_PAUSE : DeviceClock.OPERATION_RESUME);
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
                        requestOperate(DeviceClock.OPERATION_CANCLE);
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
            if (DeviceClock.OPERATION_CREATE.equals(operate)) {
                mEventTime = System.currentTimeMillis() + total * 1000;
                data.put("offset", total);
                data.put("circle", DeviceClock.CIRCLE_ONCE);
                data.put("volume", 100);
                data.put("reminder_audio", DeviceClock.AUDIO);
                data.put("event_timestamp", mEventTime);
            }
            JSONArray arr = new JSONArray();
            arr.put(data);
            params.put("data", arr);
            DeviceClock.getDevice(getDeviceStat()).callMethod(DeviceClock.METHORD_ALARM_OPS, params, new Callback<String>() {
                @Override
                public void onSuccess(String ss) {
                    try {
                        JSONObject jo = new JSONObject(ss);
                        JSONArray result = jo.optJSONArray("result");
                        if (result != null && result.length() > 0) {
                            JSONObject jsonObject = result.optJSONObject(0);
                            if (jsonObject != null && DeviceClock.RESULT_OK.equals(jsonObject.optString("ack"))) {
                                switch (operate) {
                                    case DeviceClock.OPERATION_CREATE:
                                        DeviceClock.getDevice(getDeviceStat()).subscribeEvent(TimeCountFragment.this, EVENTS);
                                    case DeviceClock.OPERATION_RESUME:
                                        setStatus(mEventTime, STATUS_RUNNING);
                                        break;
                                    case DeviceClock.OPERATION_PAUSE:
                                        setStatus(mEventTime, STATUS_PAUSE);
                                        break;
                                    case DeviceClock.OPERATION_CANCLE:
                                        setStatus(mEventTime, STATUS_NONE);
                                        DeviceClock.getDevice(getDeviceStat()).unsubscribeEvent(EVENTS);
                                        break;
                                }
                            }
                        } else {
                            onError(ERROR_DATA);
                        }
                    } catch (Exception e) {
                        LogUtils.e(TAG, e);
                        onError(ERROR_JSON);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    LogUtils.i(TAG, i + s);
                    onError(i);
                }
            });
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    private void setStatus(long time, String operate) {
        LogUtils.i(TAG, "setStatus  " + time + operate);
        if (STATUS_RUNNING.equals(operate)||STATUS_RESUME.equals(operate)) {
            llRun.setVisibility(View.VISIBLE);
            tvWhite.setVisibility(View.VISIBLE);
            llCreate.setVisibility(View.GONE);
            tvBlue.setText(R.string.timecount_pause);
            mTdv.setStart(true, time);
        } else if (STATUS_PAUSE.equals(operate)) {
            llRun.setVisibility(View.VISIBLE);
            tvWhite.setVisibility(View.VISIBLE);
            llCreate.setVisibility(View.GONE);
            tvBlue.setText(R.string.timecount_continue);
            mTdv.setStart(false, time);
        } else if (STATUS_NONE.equals(operate)) {
            mViewDate.setCurrentMinute(0);
            mViewDate.setCurrentHour(0);
            llRun.setVisibility(View.GONE);
            tvWhite.setVisibility(View.GONE);
            llCreate.setVisibility(View.VISIBLE);
            tvBlue.setText(R.string.timecount_start);
            mTdv.setStart(false, time);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveLocal();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveLocal();
        mTdv.setStart(false, System.currentTimeMillis());
        DeviceClock.getDevice(getDeviceStat()).unsubscribeEvent(EVENTS);
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

    @Override
    public void onFinish() {
        setStatus(System.currentTimeMillis(), STATUS_NONE);
        DeviceClock.getDevice(getDeviceStat()).unsubscribeEvent(EVENTS);
        LogUtils.i(TAG, "onFinish");
    }

}
