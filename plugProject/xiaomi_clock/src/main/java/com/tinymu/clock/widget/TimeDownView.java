package com.tinymu.clock.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by livy on 16/9/14.
 */
public class TimeDownView extends TextView {
    private boolean start;

    public TimeDownView(Context context) {
        super(context);
        init();
    }


    public TimeDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeDownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setText("已开始");
    }

    public boolean getStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}
