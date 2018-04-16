package com.tinymu.clock.alarmclock;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tinymu.clock.DeviceClock;
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

public class AlarmClockFragment extends StatusActFragment {


    private static final int INIT_INDEX = 0;
    private static final String TAG = "AlarmClockFragment";
    public static final int REQUESTCODE_CREATE = 1;
    private List<AlarmClockBean> mList = new ArrayList<>();
    private RecyclerView rv;
    private int mIndex = INIT_INDEX;
    private int mId;
    private HeaderAndFooterRecyclerViewAdapter mAdapter;
    private RecyclerViewHolder mHeaderView;
    private Runnable mRefresh = new Runnable() {//每个一分钟刷新一次，时间文案有变化
        @Override
        public void run() {
            rv.postDelayed(this, 61000 - System.currentTimeMillis() % 60000);
        }
    };
    private RecyclerView.OnScrollListener mListener = new RecyclerView.OnScrollListener() {
        long minute;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (minute == System.currentTimeMillis() / 60000) {//滑动的时候不刷新，抬起手的时候判断是不是要刷新
                    rv.postDelayed(mRefresh, 61000 - System.currentTimeMillis() % 60000);
                } else {
                    rv.post(mRefresh);
                }
            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                rv.removeCallbacks(mRefresh);
                minute = System.currentTimeMillis() / 60000;
            }
        }
    };

    @Override
    public int getcontentView() {
        return R.layout.activity_recyclerview;
    }

    @Override
    public void afterInjectView(View view) {
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(activity()));
        AlarmClockAdapter alarmClockAdapter = new AlarmClockAdapter(activity(), mList, this);
        mAdapter = new HeaderAndFooterRecyclerViewAdapter(alarmClockAdapter);
        mHeaderView = new RecyclerViewHolder(View.inflate(activity(), R.layout.header_alarmclock, null));
        mAdapter.addHeaderView(mHeaderView);
        mAdapter.addFooterView(new RecyclerViewHolder(View.inflate(activity(), R.layout.footer_alarmclock, null)));
        rv.setAdapter(mAdapter);
        rv.addOnScrollListener(mListener);
        requestData(mIndex);
        mHeaderView.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(activity(), AlarmCreateActivity.class), REQUESTCODE_CREATE);
            }
        });
    }

    private void requestData(final int index) {
        try {
            JSONObject params = new JSONObject();
            params.put("operation", "query");
            params.put("req_type", DeviceClock.TYPE_ALARM);
            params.put("parser_timestamp", System.currentTimeMillis());
            params.put("index", index);
            DeviceClock.getDevice(getDeviceStat()).callMethod("set_alarm", params, new Callback<String>() {

                @Override
                public void onSuccess(String ss) {
                    try {
                        JSONObject jo = new JSONObject(ss);
                        mId = jo.optInt("id");
                        JSONArray result = jo.optJSONArray("result");
                        int hasMore = result.length();
                        AlarmClockBean.parseList(result, mList);
                        if (mList.size() == 0) {
//                            onNoData();
                        }
                        rv.getAdapter().notifyDataSetChanged();
                        if (hasMore == 0) {//需要注意的是，由于OT通道负载为1k，因此当闹钟列表比较长的时候要分段传输，手机端要把获取的index传给设备，   设备端根据index来返回闹钟列表，暂定设备端每次最多返回3个闹钟。设备端返回的时候，会把剩余闹钟个数返给手机端。
                            rv.removeCallbacks(mRefresh);
                            rv.postDelayed(mRefresh, 61000 - System.currentTimeMillis() % 60000);//每个一分钟刷新一次，时间有变化
                        } else {
                            requestData(mList.size());
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "", e);
                        onError(ERROR_JSON);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.i(TAG, i + s);
                    if (i == ERROR_TIMEOUT) {
                        requestData(index);
                    } else if (i == ERROR_NET) {
                        onNoNetwork();
                    } else {
                        onError(i);
                    }
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
                ((AlarmClockAdapter) mAdapter.getInnerAdapter()).setStatusEdit(true);
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
                ((AlarmClockAdapter) mAdapter.getInnerAdapter()).setStatusEdit(false);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void requestDelete() {
        try {
            JSONObject params = new JSONObject();
            params.put("operation", DeviceClock.OPERATION_DELETE);
            params.put("parser_timestamp", System.currentTimeMillis());
            JSONArray arr = new JSONArray();
            for (int i = 0; i < mList.size(); i++) {
                JSONObject joItem = new JSONObject();
                AlarmClockBean alarmClockBean = mList.get(i);
                if (alarmClockBean.delete) {
//                    joItem.put("type", alarmClockBean.type);
                    joItem.put("id", alarmClockBean.id);
                    arr.put(joItem);
                }
            }
            params.put("data", arr);
            if (arr.length() != 0) {
                DeviceClock.getDevice(getDeviceStat()).callMethod("set_alarm", params, new Callback<String>() {
                    @Override
                    public void onSuccess(String ss) {
                        try {
                            JSONObject jo = new JSONObject(ss);
                            if (jo.optInt("code") == SUCCESS) {
                                for (int j = mList.size() - 1; j >= 0; j--) {
                                    if (mList.get(j).delete) {
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
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.i("dsrrequest2", i + s);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        rv.removeCallbacks(mRefresh);
    }
}
