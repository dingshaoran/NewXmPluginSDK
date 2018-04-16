package com.tinymu.clock.notify;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tinymu.clock.alarmclock.AlarmClockBean;
import com.tinymu.clock.alarmclock.AlarmClockFragment;
import com.tinymu.clock.base.BasePluginFragment;
import com.tinymu.clock.base.EViewHolder;
import com.tinymu.clock.base.RecyclerAdapter;
import com.tinymu.clock.main.MainActivity;
import com.tinymu.clock.utils.FormatUtils;
import com.tinymu.clock.utils.LogUtils;
import com.zimi.clockmyk.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotifyAdapter extends RecyclerAdapter<AlarmClockBean> {

    private static final String TAG = "AlarmClockAdapter";
    private final BasePluginFragment mFragment;
    private final CharSequence[] items;
    private boolean mStatusEdit;
    private final SimpleDateFormat mFormatHour;

    public NotifyAdapter(Activity alarmClockActivity, List<AlarmClockBean> mList, BasePluginFragment acf) {
        super(alarmClockActivity, mList);
        this.mFragment = acf;
        mFormatHour = new SimpleDateFormat("hh:mm", Locale.getDefault());
        SpannableString red = new SpannableString("0");
        red.setSpan(new ImageSpan(alarmClockActivity, R.drawable.oval_red_10), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableString yellow = new SpannableString("0");
        yellow.setSpan(new ImageSpan(alarmClockActivity, R.drawable.oval_yellow_10), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableString blue = new SpannableString("0");
        blue.setSpan(new ImageSpan(alarmClockActivity, R.drawable.oval_blue_10), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableString green = new SpannableString("0");
        green.setSpan(new ImageSpan(alarmClockActivity, R.drawable.oval_green_10), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        items = new CharSequence[]{alarmClockActivity.getString(R.string.notifycreate_desc_none), red, yellow, blue, green};
    }

    @Override
    protected int onCreateViewLayoutID(int viewType) {
        return R.layout.item_notify;
    }

    public void setStatusEdit(boolean statusEdit) {
        this.mStatusEdit = statusEdit;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSetView(EViewHolder holder, final AlarmClockBean item, final int position) {
        TextView item_left_icon = holder.getViewById(R.id.item_left_icon);
        TextView item_label = holder.getViewById(R.id.item_label);
        TextView tvIndex = holder.getViewById(R.id.tvIndex);
        TextView item_content = holder.getViewById(R.id.item_content);
        TextView item_divider = holder.getViewById(R.id.item_divider);
        CheckBox cb = holder.getViewById(R.id.cb);
        item_left_icon.setText(mFormatHour.format(new Date(item.datetime)));
        item_label.setText(item.reminder);
        tvIndex.setText(items[FormatUtils.parseInt(item.event, 0)]);
        cb.setChecked(item.delete);
        item_content.setText(FormatUtils.parseCircle(mContext, item.circle, item.circle_extra));
        if (mStatusEdit) {
            cb.setVisibility(View.VISIBLE);
            item_left_icon.setVisibility(View.INVISIBLE);
        } else {
            item_left_icon.setVisibility(View.VISIBLE);
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
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragment.startActivityForResult(new Intent(mContext, NotifyCreateActivity.class).putExtra(NotifyCreateActivity.ID, item), AlarmClockFragment.REQUESTCODE_CREATE);
            }
        });
    }
}
