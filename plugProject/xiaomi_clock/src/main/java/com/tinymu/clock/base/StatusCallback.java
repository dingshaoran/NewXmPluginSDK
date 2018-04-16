package com.tinymu.clock.base;

import com.xiaomi.smarthome.device.api.Callback;

public class StatusCallback implements Callback<String> {
    StatusHandle sh;

    public StatusCallback(StatusHandle sh) {
        this.sh = sh;
    }

    @Override
    public void onSuccess(String s) {
        if (sh != null) {
            sh.onLoadingFinish(true);
        }
    }

    @Override
    public void onFailure(int i, String s) {
        if (sh != null) {
            sh.onLoadingFinish(true);
        }
    }
}
