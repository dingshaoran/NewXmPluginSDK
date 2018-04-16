package com.tinymu.clock.alarmclock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tinymu.clock.DeviceClock;
import com.tinymu.clock.base.BasePluginFragment;
import com.tinymu.clock.base.EViewHolder;
import com.tinymu.clock.base.RecyclerAdapter;
import com.tinymu.clock.main.MainActivity;
import com.tinymu.clock.utils.FormatUtils;
import com.tinymu.clock.utils.LogUtils;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.common.ui.widget.SwitchButton;
import com.zimi.clockmyk.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlarmClockAdapter extends RecyclerAdapter<AlarmClockBean> {

    private static final String TAG = "AlarmClockAdapter";
    private final BasePluginFragment mFragment;
    private boolean mStatusEdit;
    private final SimpleDateFormat mFormatDay;
    private final SimpleDateFormat mFormatHour;

    public AlarmClockAdapter(Activity alarmClockActivity, List<AlarmClockBean> mList, BasePluginFragment acf) {
        super(alarmClockActivity, mList);
        this.mFragment = acf;
        mFormatHour = new SimpleDateFormat("hh:mm", Locale.getDefault());
        mFormatDay = new SimpleDateFormat(mContext.getString(R.string.dataformat_month_day), Locale.getDefault());
    }

    @Override
    protected int onCreateViewLayoutID(int viewType) {
        return R.layout.item_alarmclock;
    }

    public void setStatusEdit(boolean statusEdit) {
        this.mStatusEdit = statusEdit;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSetView(EViewHolder holder, final AlarmClockBean item, final int position) {
        TextView item_left_icon = holder.getViewById(R.id.item_left_icon);
        TextView item_label = holder.getViewById(R.id.item_label);
        TextView item_content = holder.getViewById(R.id.item_content);
        SwitchButton item_switch = holder.getViewById(R.id.item_switch);
        TextView item_divider = holder.getViewById(R.id.item_divider);
        CheckBox cb = holder.getViewById(R.id.cb);
        item_left_icon.setText(mFormatHour.format(new Date(item.datetime)));
        cb.setChecked(item.delete);
        if (DeviceClock.STATUS_ON.equals(item.status)) {
            item_switch.setChecked(true);
            item_label.setText(FormatUtils.afterNow(mContext, item.datetime));
            item_label.setTextColor(mContext.getResources().getColor(R.color.color_5566ff));
        } else {
            item_switch.setChecked(false);
            item_label.setText(mContext.getString(R.string.alarmclock_closestatus));
            item_label.setTextColor(mContext.getResources().getColor(R.color.color_60000000));
        }
        String reminder = item.event;
        if (TextUtils.isEmpty(reminder)) {
            reminder = item.reminder;
        }
        if (TextUtils.isEmpty(reminder)) {
            item_content.setText(FormatUtils.parseCircle(mContext, item.circle, item.circle_extra));
        } else {
            item_content.setText(reminder + " | " + FormatUtils.parseCircle(mContext, item.circle, item.circle_extra));
        }
        if (mStatusEdit) {
            cb.setVisibility(View.VISIBLE);
            item_switch.setVisibility(View.INVISIBLE);
        } else {
            cb.setVisibility(View.INVISIBLE);
            item_switch.setVisibility(View.VISIBLE);
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
        item_switch.setOnPerformCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                if (b) {
                    ClockPresenter.requestStatusClock(mFragment.xmPluginActivity(), item, true, AlarmClockAdapter.this);
                } else {
                    String extra = "";
                    long now = System.currentTimeMillis();
                    long daySecond = 3600000 * 24;
                    long dayTime = now - (now + 8 * 3600000) % daySecond;//今天凌晨 0点的时间
                    if (item.datetime - dayTime < daySecond) {
                        extra = mContext.getString(R.string.alarmclock_close_today);
                    } else if (item.datetime - dayTime < daySecond * 2) {
                        extra = mContext.getString(R.string.alarmclock_close_tomorrow);
                    }
                    new MLAlertDialog.Builder(mContext).setTitle(R.string.alarmclock_close_title)
                            .setItems(new CharSequence[]{mContext.getString(R.string.alarmclock_close_once, mFormatDay.format(new Date(item.datetime)) + extra)
                                    , mContext.getString(R.string.alarmclock_close_all)}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {//关闭一次
                                        ClockPresenter.requestCloseOnce(mFragment.xmPluginActivity(), item, AlarmClockAdapter.this);
                                    } else {
                                        ClockPresenter.requestStatusClock(mFragment.xmPluginActivity(), item, false, AlarmClockAdapter.this);
                                    }
                                }
                            }).setDismissCallBack(new MLAlertDialog.DismissCallBack() {
                        @Override
                        public void beforeDismissCallBack() {
                        }

                        @Override
                        public void afterDismissCallBack() {
                            notifyItemChanged(position);//取消了关闭，重新置位打开的状态
                        }
                    }).setNeutralButton(R.string.alarmclock_close_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (dialogInterface != null) {
                                dialogInterface.dismiss();
                            }
                        }
                    }).show();
                }
            }
        });
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragment.startActivityForResult(new Intent(mContext, AlarmCreateActivity.class).putExtra(AlarmCreateActivity.ID, item), AlarmClockFragment.REQUESTCODE_CREATE);
            }
        });
    }
}
