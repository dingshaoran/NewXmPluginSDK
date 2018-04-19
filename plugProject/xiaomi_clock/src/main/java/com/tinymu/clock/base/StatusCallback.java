package com.tinymu.clock.base;

import android.app.Activity;
import android.text.TextUtils;

import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public abstract class StatusCallback implements Callback<String> {
    private final WeakReference<Activity> mAct;
    WeakReference<StatusHandle> sh;

    public StatusCallback(StatusHandle sh, Activity act) {
        this.sh = new WeakReference<StatusHandle>(sh);
        this.mAct = new WeakReference<Activity>(act);
    }

    @Override
    public void onSuccess(String s) {
        StatusHandle statusHandle = sh.get();
        if (statusHandle != null) {
            statusHandle.onLoadingFinish(true);
        }
        Activity activity = mAct.get();
        if (activity != null && !activity.isFinishing()) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String result = jsonObject.optString("result");
                if (TextUtils.isEmpty(result)) {
                    onData(result);
                } else {
                    onError(ERROR_RESPONSE_JSON_FAIL, jsonObject.optString("error"));
                }
            } catch (JSONException e) {
                onError(ERROR_JSON_PARSER_EXCEPTION, e.getMessage());
            }
        }
    }

    public void onError(int i, String error) {
        StatusHandle statusHandle = sh.get();
        if (statusHandle != null) {
            if (i == ERROR_NETWORK_ERROR) {
                statusHandle.onNoNetwork();
            } else {
                statusHandle.onError(i);
            }
        }
    }

    public abstract void onData(String result);

    @Override
    public void onFailure(int i, String s) {
        StatusHandle statusHandle = sh.get();
        if (statusHandle != null) {
            statusHandle.onLoadingFinish(true);
        }
        Activity activity = mAct.get();
        if (activity != null && !activity.isFinishing()) {
            onError(i, s);
        }
    }
}
