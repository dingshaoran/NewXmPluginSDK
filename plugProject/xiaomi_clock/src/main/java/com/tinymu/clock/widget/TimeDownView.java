package com.tinymu.clock.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.zimi.clockmyk.R;

/**
 * Created by livy on 16/9/14.
 */
public class TimeDownView extends TextView {
    private boolean start;
    private long timeEvent;
    private OnTimeDownListener listener;
    private Runnable countDown = new Runnable() {
        @Override
        public void run() {
            long lastTime = timeEvent - System.currentTimeMillis();
            if (lastTime > 0) {
                setText(getContext().getString(R.string.timecount_history_item, lastTime / 60000, (lastTime % 60000) / 1000));
                if (start) {
                    postDelayed(this, 1000);
                }
            } else {
                if (listener != null) {
                    listener.onFinish();
                }
                setText("00:00");
            }
        }
    };

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

    public void setOnTimeDownListener(OnTimeDownListener listener) {
        this.listener = listener;
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setTextSize(30);
        setTextColor(0xff000000);
    }

    public boolean getStart() {
        return start;
    }

    public void setStart(boolean start, long timeEvent) {
        this.start = start;
        this.timeEvent = timeEvent;
        removeCallbacks(countDown);
        post(countDown);
    }

    public interface OnTimeDownListener {
        void onFinish();
    }
}
