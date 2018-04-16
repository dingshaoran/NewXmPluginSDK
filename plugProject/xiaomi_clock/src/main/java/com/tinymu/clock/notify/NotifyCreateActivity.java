package com.tinymu.clock.notify;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tinymu.clock.DeviceClock;
import com.tinymu.clock.alarmclock.AlarmClockBean;
import com.tinymu.clock.base.BasePluginActivity;
import com.tinymu.clock.utils.FormatUtils;
import com.tinymu.clock.utils.LogUtils;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.common.ui.widget.SwitchButton;
import com.xiaomi.smarthome.common.ui.widget.TimePicker;
import com.xiaomi.smarthome.device.api.Callback;
import com.zimi.clockmyk.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class NotifyCreateActivity extends BasePluginActivity implements View.OnClickListener, TimePicker.OnTimeChangedListener, TextWatcher, CompoundButton.OnCheckedChangeListener {
    public static final String ID = "id";
    private static final String SHAREPREFER_KEY = "hide_sleep";
    private static final String TAG = "NotifyCreateActivity";
    private TimePicker timePicker;
    private TextView tvTimeTip;
    private View rlRepeat;
    private SwitchButton itemSitch;
    private View rlRing;
    private View rlContinue;
    private View rlDesc;
    private TextView tvRepeat;
    private TextView tvRing;
    private TextView tvDesc;
    private ArrayList<String> mRepeatType = new ArrayList<String>(5);
    private ArrayList<String> mRepeatText = new ArrayList<String>(5);
    private AlarmClockBean mBean;
    private SwitchButton isAutoDelete;
    private View rlAutoDelete;
    private boolean[] mChoseWeek = new boolean[7];//一周7天 周一到周日选中了那天
    private View rlName;
    private View rlTime;
    private TextView tvTime;
    private EditText tvName;
    private View rlRepeatClose;
    private TextView tvRepeatClose;
    private View vRight;
    private CharSequence[] items;
    private View mDateView;

    @Override
    public int getcontentView() {
        return R.layout.activity_notifycreate;
    }

    @Override
    public void injectView(View contentView) {
        mHostActivity.setTitleBarPadding(findViewById(R.id.title_bar));
        timePicker = (TimePicker) findViewById(R.id.time_picker);
        itemSitch = (SwitchButton) findViewById(R.id.item_switch);
        isAutoDelete = (SwitchButton) findViewById(R.id.isAutoDelete);
        rlName = findViewById(R.id.rlName);
        rlTime = findViewById(R.id.rlTime);
        rlRepeat = findViewById(R.id.rlRepeat);
        rlRepeatClose = findViewById(R.id.rlRepeatClose);
        rlAutoDelete = findViewById(R.id.rlAutoDelete);
        rlRing = findViewById(R.id.rlRing);
        rlContinue = findViewById(R.id.rlContinue);
        vRight = findViewById(R.id.title_bar_more);
        rlDesc = findViewById(R.id.rlDesc);
        tvName = (EditText) findViewById(R.id.tvName);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvTimeTip = (TextView) findViewById(R.id.time_tip);
        tvRepeat = (TextView) findViewById(R.id.tvRepeat);
        tvRepeatClose = (TextView) findViewById(R.id.tvRepeatClose);
        tvRing = (TextView) findViewById(R.id.tvRing);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        rlName.setOnClickListener(this);
        rlTime.setOnClickListener(this);
        rlRepeat.setOnClickListener(this);
        rlRepeatClose.setOnClickListener(this);
        rlRing.setOnClickListener(this);
        rlContinue.setOnClickListener(this);
        vRight.setOnClickListener(this);
        rlDesc.setOnClickListener(this);
//        timePicker.setOnTimeChangedListener(this);
        tvRepeat.addTextChangedListener(this);
        itemSitch.setOnPerformCheckedChangeListener(this);
        isAutoDelete.setChecked(true);
        rlRing.setVisibility(View.GONE);
        rlAutoDelete.setVisibility(View.GONE);
        rlContinue.setVisibility(View.GONE);
    }

    @Override
    public void afterInjectView(View view) {
        mBean = getIntent().getParcelableExtra(ID);
        initData();
        if (mBean != null) {
            tvDesc.setText(items[FormatUtils.parseInt(mBean.event, 0)]);
            int index = mRepeatType.indexOf(mBean.circle);
            if (index >= 0) {
                tvRepeat.setText(mRepeatText.get(index));
            }
            if (mBean.reminder != null) {
                tvName.setText(mBean.reminder);
                tvName.setSelection(mBean.reminder.length());
            }
            Date date = new Date(mBean.datetime);
            tvTime.setText(getString(R.string.notifycreate_hour_minute, date.getHours(), date.getMinutes()));
        } else {
            mBean = new AlarmClockBean();
            mBean.id = -1;
        }
    }

    private void initData() {
        mRepeatText.clear();
        mRepeatType.clear();
        mRepeatText.add(getString(R.string.notifycreate_repeat_once));
        mRepeatType.add(DeviceClock.CIRCLE_ONCE);
        mRepeatText.add(getString(R.string.notifycreate_repeat_everyday));
        mRepeatType.add(DeviceClock.CIRCLE_EVERYDAY);
        mRepeatText.add(getString(R.string.notifycreate_repeat_everyweek));
        mRepeatType.add(DeviceClock.CIRCLE_EVERYWEEK);
        mRepeatText.add(getString(R.string.notifycreate_repeat_twoweek));
        mRepeatType.add(DeviceClock.CIRCLE_TWOWEEK);
        mRepeatText.add(getString(R.string.notifycreate_repeat_everymonth));
        mRepeatType.add(DeviceClock.CIRCLE_MONTHLY);
        mRepeatText.add(getString(R.string.notifycreate_repeat_everyyear));
        mRepeatType.add(DeviceClock.CIRCLE_YEARLY);
        mRepeatText.add(getString(R.string.notifycreate_repeat_custom));
        mRepeatType.add(DeviceClock.CIRCLE_CUSTOM);
        SpannableString red = new SpannableString("0");
        red.setSpan(new ImageSpan(this, R.drawable.oval_red_10), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableString yellow = new SpannableString("0");
        yellow.setSpan(new ImageSpan(this, R.drawable.oval_yellow_10), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableString blue = new SpannableString("0");
        blue.setSpan(new ImageSpan(this, R.drawable.oval_blue_10), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableString green = new SpannableString("0");
        green.setSpan(new ImageSpan(this, R.drawable.oval_green_10), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        items = new CharSequence[]{getString(R.string.notifycreate_desc_none), red, yellow, blue, green};
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.rlRepeat:
                showRepeatDialog();
                break;
            case R.id.rlRepeatClose:
                showRepeatCloseDialog();
                break;
            case R.id.rlRing:
                break;
            case R.id.rlContinue:
                break;
            case R.id.title_bar_more:
                requestCommit();
                break;
            case R.id.rlTime:
                showTimeDialog();
                break;
            case R.id.rlDesc:
                showDescDialog();
                break;
        }
    }

    private void showRepeatCloseDialog() {
        MLAlertDialog.Builder builder = new MLAlertDialog.Builder(activity());
        View view = View.inflate(activity(), R.layout.dialog_date_select, null);
        builder.setTitle(getString(R.string.notifycreate_reqpestclose_title)).setView(view)
                .setNegativeButton(R.string.alarmcreate_desc_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                }).setPositiveButton(R.string.alarmcreate_desc_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                mBean.hint = i;
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        }).show();
    }

    private void showTimeDialog() {
        MLAlertDialog.Builder builder = new MLAlertDialog.Builder(activity());
        if (mDateView == null) {
            mDateView = View.inflate(activity(), R.layout.dialog_date_select, null);
            timePicker = (TimePicker) mDateView.findViewById(R.id.tp);
            if (mBean.id == -1) {
                Date date = new Date();
                timePicker.setCurrentHour(date.getHours());
                timePicker.setCurrentMinute(date.getMinutes());
            } else {
                Date date = new Date(mBean.datetime);
                timePicker.setCurrentHour(date.getHours());
                timePicker.setCurrentMinute(date.getMinutes());
            }
        }
        builder.setTitle(getString(R.string.notifycreate_reqpestclose_title)).setView(mDateView)
                .setNegativeButton(R.string.alarmcreate_desc_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                }).setPositiveButton(R.string.alarmcreate_desc_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvTime.setText(getString(R.string.notifycreate_hour_minute, timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        }).show();
    }

    private void requestCommit() {
        try {
            mBean.reminder = tvName.getText().toString();
            if (TextUtils.isEmpty(mBean.circle)) {
                Toast.makeText(activity(), R.string.alarmclock_close_circle_hint, Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(mBean.reminder)) {
                Toast.makeText(activity(), R.string.alarmclock_close_desc_hint, Toast.LENGTH_LONG).show();
            } else if (timePicker == null) {
                Toast.makeText(activity(), R.string.alarmclock_close_time_hint, Toast.LENGTH_LONG).show();
            } else {
                mBean.circle_extra = FormatUtils.cron(mBean.circle, timePicker, mChoseWeek);
                JSONObject params = new JSONObject();
                JSONArray array = new JSONArray();
                JSONObject data = new JSONObject();
                if (mBean.id == -1) {
                    params.put("operation", DeviceClock.OPERATION_CREATE);
                } else {
                    params.put("operation", DeviceClock.OPERATION_MODIFY);
                    data.put("id", mBean.id);
                }
                data.put("type", DeviceClock.TYPE_REMINDER);
                data.put("event", mBean.event);
                data.put("reminder", mBean.reminder);
                data.put("ringtone", mBean.ringtone);
                data.put("reminder_audio", mBean.ringtone);
                data.put("volume", 100);
                data.put("circle", mBean.circle);
                data.put("circle_extra", mBean.circle_extra);
                data.put("event_timestamp", mBean.datetime);
                array.put(data);
                params.put("data", array);
                params.put("parser_timestamp", System.currentTimeMillis());
                DeviceClock.getDevice(getDeviceStat()).callMethod("set_alarm", params, new Callback<String>() {

                    @Override
                    public void onSuccess(String ss) {
                        try {
                            JSONObject jsonObject = new JSONObject(ss);
                            if (jsonObject.optInt("code") == Callback.SUCCESS) {
                                mBean.status = DeviceClock.STATUS_ON;
                                setResult0(RESULT_OK, getIntent().putExtra(ID, mBean));
                                onBackPressed();
                            }
                        } catch (Exception e) {
                            Log.i(TAG, "", e);
                            onError(ERROR_JSON);
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.i(TAG, i + s);
                        if (i == ERROR_TIMEOUT) {
                        } else {
                            onError(i);
                        }
                    }
                });
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    private void showDescDialog() {
        MLAlertDialog.Builder builder = new MLAlertDialog.Builder(activity());
        final ParsePosition parsePosition = new ParsePosition(FormatUtils.parseInt(mBean.event, 0));
        builder.setTitle(getString(R.string.notifycreate_desc_title)).setSingleChoiceItems(items, parsePosition.getIndex(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                parsePosition.setIndex(i);

            }
        }).setNegativeButton(R.string.alarmcreate_desc_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        }).setPositiveButton(R.string.alarmcreate_desc_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mBean.event = String.valueOf(parsePosition.getIndex());
                tvDesc.setText(items[parsePosition.getIndex()]);
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        }).show();
    }

    private void showRepeatDialog() {
        new MLAlertDialog.Builder(this)
                .setSingleChoiceItems(mRepeatText.toArray(new String[mRepeatText.size()]), mRepeatType.indexOf(mBean.circle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                        final String typeChose = mRepeatType.get(i);
                        final String choseText = mRepeatText.get(i);
                        if (DeviceClock.CIRCLE_CUSTOM.equals(typeChose)) {
                            String[] arrayWeek = new String[]{getString(R.string.alarmclock_monday), getString(R.string.alarmclock_tuesday), getString(R.string.alarmclock_wednesday),
                                    getString(R.string.alarmclock_thursday), getString(R.string.alarmclock_friday), getString(R.string.alarmclock_saturday), getString(R.string.alarmclock_sunday),};
                            final boolean[] booleans = Arrays.copyOf(mChoseWeek, mChoseWeek.length);
                            new MLAlertDialog.Builder(activity()).setTitle(getString(R.string.alarmcreate_custom_title))
                                    .setMultiChoiceItems(arrayWeek, booleans, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                            booleans[i] = b;
                                        }
                                    }).setNegativeButton(getString(R.string.alarmcreate_custom_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (dialogInterface != null) {
                                        dialogInterface.dismiss();
                                    }
                                }
                            }).setPositiveButton(getString(R.string.alarmcreate_custom_confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mChoseWeek = booleans;//确认保存数据到全局变量
                                    mBean.circle = typeChose;
                                    tvRepeat.setText(choseText);
                                    if (dialogInterface != null) {
                                        dialogInterface.dismiss();
                                    }
                                }
                            }).show();
                        } else {
                            mBean.circle = typeChose;
                            tvRepeat.setText(choseText);
                        }
                    }
                }).show();
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//        tvTimeTip.setText(FormatUtils.);//TODO
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (DeviceClock.CIRCLE_ONCE.equals(mBean.circle)) {
            rlRepeatClose.setVisibility(View.GONE);
        } else {
            rlRepeatClose.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        final SharedPreferences sharedPreferences = activity().getSharedPreferences("alarm_create_activity", Context.MODE_PRIVATE);
        if (b && !sharedPreferences.getBoolean(SHAREPREFER_KEY, false)) {
            View view = View.inflate(activity(), R.layout.dialog_sleep_hint, null);
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
            new MLAlertDialog.Builder(activity()).setTitle(getString(R.string.alarmcreate_continue_sleep)).setView(view)
                    .setPositiveButton(R.string.alarmcreate_sleep_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sharedPreferences.edit().putBoolean(SHAREPREFER_KEY, true).apply();
                            if (dialogInterface != null) {
                                dialogInterface.dismiss();
                            }
                        }
                    }).show();
        }
    }
}
