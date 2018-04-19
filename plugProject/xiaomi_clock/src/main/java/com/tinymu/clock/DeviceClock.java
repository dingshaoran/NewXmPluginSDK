package com.tinymu.clock;

import android.os.Handler;
import android.os.Looper;

import com.tinymu.clock.utils.LogUtils;
import com.xiaomi.smarthome.device.api.BaseDevice;
import com.xiaomi.smarthome.device.api.Callback;
import com.xiaomi.smarthome.device.api.DeviceStat;
import com.xiaomi.smarthome.device.api.Parser;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//设备功能处理
public class DeviceClock extends BaseDevice {

    // /////////////属性定义
    // 相对湿度
    public static final String METHORD_ALARM_OPS = "alarm_ops";
    public static final String TYPE_ALARM = "alarm";
    public static final String TYPE_REMINDER = "reminder";
    public static final String TYPE_TIMER = "timer";
    public static final String[] TYPES = new String[]{TYPE_ALARM, TYPE_REMINDER, TYPE_TIMER};
    public static final String STATUS_ON = "on";
    public static final String STATUS_OFF = "off";
    public static final String OPERATION_QUERY = "query";
    public static final String OPERATION_MODIFY = "modify";
    public static final String OPERATION_CREATE = "create";
    public static final String OPERATION_OPEN = "open";
    public static final String OPERATION_RESUME = "resume";
    public static final String OPERATION_PAUSE = "pause";
    public static final String OPERATION_CLOSE = "close";
    public static final String OPERATION_DELETE = "delete";
    public static final String OPERATION_CANCLE = "cancle";
    public static final String CIRCLE_ONCE = "once";
    public static final String CIRCLE_WORKDAY = "workday";
    public static final String CIRCLE_MONTOFRI = "montofri";
    public static final String CIRCLE_EVERYDAY = "everyday";
    public static final String CIRCLE_HOLIDAY = "holiday";
    public static final String CIRCLE_WEEKEND = "weekend";
    public static final String CIRCLE_EVERYWEEK = "everyweek";
    public static final String CIRCLE_TWOWEEK = "twoweek";
    public static final String CIRCLE_MONTHLY = "monthly";
    public static final String CIRCLE_YEARLY = "yearly";
    public static final String CIRCLE_CUSTOM = "custom";
    public static final String SHAREPREFERENCE = "alarm_clock";
    public static final String AUDIO = "/usr/share/sounds/alarm.mp3";
    public static final String RESULT_OK = "OK";
    private static final String TAG = "request";
    private static final HashMap<String[], List<WeakReference<SubscribeLisenter>>> mListeners = new HashMap<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private DeviceClock(DeviceStat deviceStat) {
        super(deviceStat);
    }


    // 缓存设备状态数据，每次进入不需要立即更新数据
    private static ArrayList<DeviceClock> DEVICE_CACHE = new ArrayList<DeviceClock>();

    // 先从缓存中获取Device，并更新DeviceStat
    public static synchronized DeviceClock getDevice(DeviceStat deviceStat) {
        for (DeviceClock device : DEVICE_CACHE) {
            if (deviceStat.did.equals(device.getDid())) {
                device.mDeviceStat = deviceStat;
                return device;
            }
        }

        DeviceClock device = new DeviceClock(deviceStat);
        DEVICE_CACHE.add(device);
        return device;
    }

    public void callMethod(String method, JSONObject params, Callback<String> callback) {
        LogUtils.i(TAG, params.toString());
        super.callMethod(method, params, callback, new Parser<String>() {
            @Override
            public String parse(String s) throws JSONException {
                LogUtils.i(TAG, s);
                return s;
            }
        });
    }

    public void callMethod(String method, JSONArray params, Callback<String> callback) {
        LogUtils.i(TAG, params.toString());
        super.callMethod(method, params, callback, new Parser<String>() {
            @Override
            public String parse(String s) throws JSONException {
                LogUtils.i(TAG, s);
                return s;
            }
        });
    }

    // 通过did获取Device
    public static synchronized DeviceClock getDevice(String did) {
        for (DeviceClock device : DEVICE_CACHE) {
            if (did.equals(device.getDid())) {
                return device;
            }
        }
        return null;
    }

    protected HashMap<String, Object> mPropertiesMap = new HashMap<String, Object>();


    // 订阅属性变化，每次只维持3分钟订阅事件
    public void subscribeProperty(String[] props, Callback<Void> callback) {
        ArrayList<String> propList = new ArrayList<String>();
        for (String prop : props) {
            if (prop.startsWith("prop.")) {
                propList.add(prop);
            } else {
                propList.add("prop." + prop);
            }
        }
        XmPluginHostApi.instance().subscribeDevice(getDid(), mDeviceStat.pid, propList, 3, callback);
    }

    // 订阅事件信息，每次只维持3分钟订阅事件
    public void subscribeEvent(SubscribeLisenter lisenter, final String[] events) {
        LogUtils.i(TAG, "   subscribeEvent :");
        List<WeakReference<SubscribeLisenter>> referenceList = mListeners.get(events);
        if (referenceList == null) {
            referenceList = new ArrayList<>();
            mListeners.put(events, referenceList);
            final ArrayList<String> eventList = new ArrayList<String>();
            for (String event : events) {
                if (event.startsWith("event.")) {
                    eventList.add(event);
                } else {
                    eventList.add("event." + event);
                }
            }
            XmPluginHostApi.instance().subscribeDevice(getDid(), mDeviceStat.pid, eventList, 3, new Callback<Void>() {

                public int retryTimes = 0;
                private Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        resubscribe();
                    }
                };

                @Override
                public void onSuccess(Void aVoid) {
                    LogUtils.i(TAG, "   subscribeEvent : onSuccess");
                    mHandler.removeCallbacks(runnable);
                    mHandler.postDelayed(runnable, 1000*170);//3分钟订阅到期，2分50秒订阅一次
                }

                @Override
                public void onFailure(int i, String s) {
                    if (retryTimes < 3) {
                        resubscribe();
                        retryTimes++;
                    }
                    LogUtils.i(TAG, "   subscribeEvent : onFailure   " + i + s);
                }

                private void resubscribe() {
                    List<WeakReference<SubscribeLisenter>> weakReferenceList = mListeners.get(events);
                    LogUtils.i(TAG, "   resubscribe :   " + weakReferenceList);
                    if (weakReferenceList != null) {
                        for (int i = weakReferenceList.size() - 1; i >= 0; i--) {
                            SubscribeLisenter subscribeLisenter = weakReferenceList.get(i).get();
                            if (subscribeLisenter != null) {
                                XmPluginHostApi.instance().subscribeDevice(getDid(), mDeviceStat.pid, eventList, 3, this);
                            } else {
                                weakReferenceList.remove(i);
                            }
                        }
                        if (weakReferenceList.size() == 0) {
                            mListeners.remove(events);
                        }
                    }
                }
            });
        }
        for (WeakReference<SubscribeLisenter> weakReference : referenceList) {
            SubscribeLisenter cache = weakReference.get();
            if (cache != null && cache == lisenter) {//已经包含了，不在重新注册
                return;
            }
        }
        referenceList.add(new WeakReference<SubscribeLisenter>(lisenter));
    }

    // 订阅事件信息，每次只维持3分钟订阅事件
    public void unsubscribeEvent(final String[] events) {
        ArrayList<String> eventList = new ArrayList<String>();
        for (String event : events) {
            if (event.startsWith("event.")) {
                eventList.add(event);
            } else {
                eventList.add("event." + event);
            }
        }
        LogUtils.i(TAG, "unsubscribeEvent :");
        List<WeakReference<SubscribeLisenter>> remove = mListeners.remove(events);
        if (remove != null) {
            XmPluginHostApi.instance().unsubscribeDevice(getDid(), mDeviceStat.pid, eventList, new Callback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    LogUtils.i(TAG, "   unsubscribeEvent : onSuccess");
                }

                @Override
                public void onFailure(int i, String s) {
                    LogUtils.i(TAG, "   unsubscribeEvent : onFailure   " + i + s);
                }
            });
        }
    }


    // 收到订阅的信息
    public void onSubscribeEvent(String data) {
        LogUtils.i(TAG, "onSubscribeEvent :" + data);
        for (Map.Entry<String[], List<WeakReference<SubscribeLisenter>>> entry : mListeners.entrySet()) {
            List<WeakReference<SubscribeLisenter>> value = entry.getValue();
            for (int i = value.size() - 1; i >= 0; i--) {
                SubscribeLisenter subscribeLisenter = value.get(i).get();
                if (subscribeLisenter != null) {
                    subscribeLisenter.onSubscribeData(data);
                } else {
                    value.remove(i);
                }
            }
        }
    }

    public interface SubscribeLisenter {
        void onSubscribeData(String data);
    }
}
