package com.tinymu.clock.base;

import android.content.Intent;

/**
 * Created by dsr on 16/7/15.
 */
public interface StartActivity {
    void startActivityForResult(Intent intent, int requestCode);

    void startActivity(Intent intent);

//    void overridePendingTransition(int in, int out);
}
