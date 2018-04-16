package com.tinymu.clock.alarmclock;

import android.text.TextUtils;
import android.widget.Toast;

import com.tinymu.clock.DeviceClock;
import com.tinymu.clock.base.OnDataChanged;
import com.tinymu.clock.base.StatusCallback;
import com.tinymu.clock.utils.LogUtils;
import com.xiaomi.smarthome.device.api.XmPluginBaseActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClockPresenter {

    private static final String TAG = "ClockPresenter";

    public static void requestCloseOnce(final XmPluginBaseActivity act, final AlarmClockBean item, final OnDataChanged callback) {
        try {
            JSONArray arr = new JSONArray();
            JSONObject params = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("id", item.id);
            data.put("datetime", item.datetime);
            data.put("event", item.event);
            data.put("reminder", item.reminder);
            data.put("circle", item.circle);
            data.put("circle_extra", item.circle_extra);
            data.put("ringtone", item.ringtone);
            data.put("volume", item.volume);
            data.put("disable_datatime", item.datetime);
            data.put("type", DeviceClock.TYPES[item.type]);
            arr.put(data);
            params.put("data", arr);
            params.put("parser_timestamp", System.currentTimeMillis());
            params.put("operation", DeviceClock.OPERATION_MODIFY);
            DeviceClock.getDevice(act.getDeviceStat()).callMethod("set_alarm", params, new StatusCallback(null) {
                @Override
                public void onSuccess(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONObject result = jsonObject.optJSONObject("result");
                        JSONObject error = jsonObject.optJSONObject("error");
                        if (result != null) {
                            item.operation = DeviceClock.OPERATION_CLOSE;
                            callback.notifyDataChanged(true);
                        }
                        if (error != null) {
                            String message = error.optString("message");
                            if (!TextUtils.isEmpty(message)) {
                                Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
                            }
                            callback.notifyDataChanged(false);
                        }
                    } catch (Exception e) {
                        callback.notifyDataChanged(false);
                        LogUtils.e(TAG, e);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    LogUtils.i(TAG, i + s);
                    callback.notifyDataChanged(false);
                }
            });
        } catch (Exception e) {
            callback.notifyDataChanged(false);
            LogUtils.e(TAG, e);
        }
    }

    public static void requestStatusClock(final XmPluginBaseActivity act, final AlarmClockBean item, final boolean closeChange, final OnDataChanged callback) {
        try {
            JSONArray arr = new JSONArray();
            JSONObject params = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("id", item.id);
            data.put("type", DeviceClock.TYPES[item.type]);
            arr.put(data);
            params.put("parser_timestamp", System.currentTimeMillis());
            params.put("operation", closeChange ? DeviceClock.OPERATION_OPEN : DeviceClock.OPERATION_CLOSE);
            params.put("data", arr);
            DeviceClock.getDevice(act.getDeviceStat()).callMethod("set_alarm", params, new StatusCallback(null) {
                @Override
                public void onSuccess(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray result = jsonObject.optJSONArray("result");
                        JSONObject error = jsonObject.optJSONObject("error");
                        if (result != null && result.length() > 0) {//{"code":0,"message":"ok","result":[{"id":11,"ack":"OK"}],"id":5659}
                            item.status = closeChange ? DeviceClock.STATUS_ON : DeviceClock.STATUS_OFF;
                            callback.notifyDataChanged(true);
                        }
                        if (error != null) {
                            String message = error.optString("message");
                            if (!TextUtils.isEmpty(message)) {
                                Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
                                callback.notifyDataChanged(false);
                            }
                        }
                    } catch (Exception e) {
                        LogUtils.e(TAG, e);
                        callback.notifyDataChanged(false);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    LogUtils.i(TAG, i + s);
                    callback.notifyDataChanged(false);
                }
            });
        } catch (Exception e) {
            LogUtils.e(TAG, e);
            callback.notifyDataChanged(false);
        }
    }
}
