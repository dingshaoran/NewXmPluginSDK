package com.tinymu.clock.timecount;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tinymu.clock.alarmclock.AlarmClockBean;
import com.tinymu.clock.base.BasePluginFragment;
import com.tinymu.clock.base.EViewHolder;
import com.tinymu.clock.base.RecyclerAdapter;
import com.tinymu.clock.main.MainActivity;
import com.tinymu.clock.utils.LogUtils;
import com.xiaomi.smarthome.device.api.BaseFragment;
import com.zimi.clockmyk.R;

import java.util.List;

public class TimeCountAdapter extends RecyclerAdapter<AlarmClockBean> {


    private static final String TAG = "TimeCountAdapter";
    private boolean mStatusEdit;
    private BaseFragment mFragment;

    public TimeCountAdapter(Activity activity, List<AlarmClockBean> list, BasePluginFragment fragment) {
        super(activity, list);
        mFragment = fragment;
    }

    @Override
    protected int onCreateViewLayoutID(int viewType) {
        return R.layout.item_timecount;
    }

    @Override
    public void onSetView(EViewHolder holder, final AlarmClockBean item, int position) {
        TextView item_label = holder.getViewById(R.id.item_label);
        CheckBox cb = holder.getViewById(R.id.cb);
        cb.setChecked(item.delete);
        item_label.setText(mContext.getString(R.string.timecount_history_item, item.datetime / 60, item.datetime % 60));
        if (mStatusEdit) {
            cb.setVisibility(View.VISIBLE);
        } else {
            cb.setVisibility(View.INVISIBLE);
        }
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.delete = b;
                int count = 0;
                int size = mList.size();
                for (int i = 0; i < size; i++) {
                    if (mList.get(i).delete) {
                        count++;
                    }
                }
                LogUtils.i(TAG, count + "   " + size);
                if (size == count) {
                    ((MainActivity) mFragment.xmPluginActivity()).onFragmentCall(MainActivity.SELECT_NONE);
                } else {
                    ((MainActivity) mFragment.xmPluginActivity()).onFragmentCall(MainActivity.SELECT_ALL);
                }
            }
        });
    }

    public void setStatusEdit(boolean statusEdit) {
        this.mStatusEdit = statusEdit;
    }
}
