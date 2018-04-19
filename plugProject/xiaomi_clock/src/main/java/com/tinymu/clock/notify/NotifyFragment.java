package com.tinymu.clock.notify;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tinymu.clock.DeviceClock;
import com.tinymu.clock.alarmclock.AlarmClockBean;
import com.tinymu.clock.alarmclock.AlarmCreateActivity;
import com.tinymu.clock.base.HeaderAndFooterRecyclerViewAdapter;
import com.tinymu.clock.base.RecyclerViewHolder;
import com.tinymu.clock.base.StatusActFragment;
import com.tinymu.clock.main.MainActivity;
import com.tinymu.clock.utils.LogUtils;
import com.xiaomi.smarthome.device.api.Callback;
import com.zimi.clockmyk.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mi on 17-10-24.
 */

public class NotifyFragment extends StatusActFragment {


    private static final int INIT_INDEX = 0;
    private static final String TAG = "NotifyFragment";
    public static final int REQUESTCODE_CREATE = 1;
    private List<AlarmClockBean> mList = new ArrayList<>();
    private RecyclerView rv;
    private int mIndex = INIT_INDEX;
    private int mId;
    private HeaderAndFooterRecyclerViewAdapter mAdapter;
    private RecyclerViewHolder mHeaderView;

    @Override
    public int getcontentView() {
        return R.layout.activity_recyclerview;
    }

    @Override
    public void afterInjectView(View view) {
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(activity()));
        NotifyAdapter alarmClockAdapter = new NotifyAdapter(activity(), mList, this);
        mAdapter = new HeaderAndFooterRecyclerViewAdapter(alarmClockAdapter);
        mHeaderView = new RecyclerViewHolder(View.inflate(activity(), R.layout.header_alarmclock, null));
        mAdapter.addHeaderView(mHeaderView);
        mAdapter.addFooterView(new RecyclerViewHolder(View.inflate(activity(), R.layout.footer_alarmclock, null)));
        rv.setAdapter(mAdapter);
        requestData(mIndex);
        mHeaderView.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(activity(), NotifyCreateActivity.class), REQUESTCODE_CREATE);
            }
        });
    }

    private void requestData(int index) {
        try {
            JSONObject params = new JSONObject();
            params.put("operation", DeviceClock.OPERATION_QUERY);
            params.put("index", index);
            params.put("parser_timestamp", System.currentTimeMillis());
            params.put("req_type", DeviceClock.TYPE_REMINDER);
            DeviceClock.getDevice(getDeviceStat()).callMethod(DeviceClock.METHORD_ALARM_OPS, params, new Callback<String>() {
                @Override
                public void onSuccess(String ss) {
                    try {
                        JSONObject jo = new JSONObject(ss);
                        mId = jo.optInt("id");
                        int hasMore = jo.optInt("has_more");
                        JSONArray result = jo.optJSONArray("result");
                        AlarmClockBean.parseList(result, mList);
                        if (mList.size() == 0) {
//                            onNoData();
                        }
                        rv.getAdapter().notifyDataSetChanged();
                        if (hasMore == 0) {//需要注意的是，由于OT通道负载为1k，因此当闹钟列表比较长的时候要分段传输，手机端要把获取的index传给设备，   设备端根据index来返回闹钟列表，暂定设备端每次最多返回3个闹钟。设备端返回的时候，会把剩余闹钟个数返给手机端。
//                            rv.removeCallbacks(mRefresh);
//                            rv.postDelayed(mRefresh, 61000 - System.currentTimeMillis() % 60000);//每个一分钟刷新一次，时间有变化
                        } else {
                            requestData(mList.size() + 1);
                        }
                    } catch (Exception e) {
                        onError(ERROR_JSON);
                        LogUtils.e(TAG, e);
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
    public void onActivityClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_share://点击了activity的编辑
                mAdapter.removeHeaderView(mHeaderView);
                ((NotifyAdapter) mAdapter.getInnerAdapter()).setStatusEdit(true);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.tvDelete:
                requestDelete();
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
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.tvCancel:
                mAdapter.addHeaderView(mHeaderView);
                ((NotifyAdapter) mAdapter.getInnerAdapter()).setStatusEdit(false);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void requestDelete() {
        try {
            JSONObject params = new JSONObject();
            JSONArray array = new JSONArray();
            for (int i = 0; i < mList.size(); i++) {
                AlarmClockBean alarmClockBean = mList.get(i);
                JSONObject data = new JSONObject();
                data.put("id", alarmClockBean.id);
                data.put("type", DeviceClock.TYPES[alarmClockBean.type]);
                array.put(data);
            }
            params.put("parser_timestamp", System.currentTimeMillis());
            params.put("operation", DeviceClock.OPERATION_DELETE);
            params.put("data", array);
            if (array.length() != 0) {
                DeviceClock.getDevice(getDeviceStat()).callMethod(DeviceClock.METHORD_ALARM_OPS, params, new Callback<String>() {
                    @Override
                    public void onSuccess(String ss) {
                        try {
                            JSONObject jo = new JSONObject(ss);
                            JSONArray result = jo.optJSONArray("result");
                            JSONObject error = jo.optJSONObject("error");
                            if (error != null) {
                                String message = error.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    Toast.makeText(activity(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject item = result.optJSONObject(i);
                                int id = item.optInt("id");
                                for (int j = mList.size() - 1; j >= 0; j--) {
                                    if (id == mList.get(j).id) {
                                        mList.remove(j);
                                    }
                                }
                            }
                            if (mList.size() == 0) {
//                            onNoData();
                            }
                            ((MainActivity) xmPluginActivity()).onFragmentCall(MainActivity.SELECT_DELETE);
                            rv.getAdapter().notifyDataSetChanged();
                        } catch (Exception e) {
                            LogUtils.e(TAG, e);
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        LogUtils.i(TAG, i + s);
                    }
                });
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUESTCODE_CREATE) {
            AlarmClockBean result = data.getParcelableExtra(AlarmCreateActivity.ID);
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).id == result.id) {
                    mList.set(i, result);
                    mAdapter.notifyDataSetChanged();
                    return;//打断后续的add notify
                }
            }
            mList.add(result);
            mAdapter.notifyDataSetChanged();
        }
    }
}
