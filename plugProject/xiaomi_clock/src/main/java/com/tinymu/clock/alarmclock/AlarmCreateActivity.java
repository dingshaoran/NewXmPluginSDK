package com.tinymu.clock.alarmclock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tinymu.clock.DeviceClock;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class AlarmCreateActivity extends BasePluginActivity implements View.OnClickListener, TimePicker.OnTimeChangedListener, TextWatcher, CompoundButton.OnCheckedChangeListener {
    public static final String ID = "id";
    private static final String SHAREPREFER_KEY = "hide_sleep";
    private static final String TAG = "AlarmCreateActivity";
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
    private String mCronMonth;
    private String mCronHour;
    private boolean[] mChoseWeek = new boolean[7];//一周7天 周一到周日选中了那天
    private TextView tvTitle;
    private View vRight;

    @Override
    public int getcontentView() {
        return R.layout.activity_alarmcreate;
    }

    @Override
    public void injectView(View contentView) {
        mHostActivity.setTitleBarPadding(findViewById(R.id.title_bar));
        timePicker = (TimePicker) findViewById(R.id.time_picker);
        itemSitch = (SwitchButton) findViewById(R.id.item_switch);
        isAutoDelete = (SwitchButton) findViewById(R.id.isAutoDelete);
        vRight = findViewById(R.id.title_bar_more);
        rlRepeat = findViewById(R.id.rlRepeat);
        rlAutoDelete = findViewById(R.id.rlAutoDelete);
        rlRing = findViewById(R.id.rlRing);
        rlContinue = findViewById(R.id.rlContinue);
        rlDesc = findViewById(R.id.rlDesc);
        tvTitle = (TextView) findViewById(R.id.title_bar_title);
        tvTimeTip = (TextView) findViewById(R.id.time_tip);
        tvRepeat = (TextView) findViewById(R.id.tvRepeat);
        tvRing = (TextView) findViewById(R.id.tvRing);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        rlRepeat.setOnClickListener(this);
        rlRing.setOnClickListener(this);
        rlContinue.setOnClickListener(this);
        rlDesc.setOnClickListener(this);
        vRight.setOnClickListener(this);
        timePicker.setOnTimeChangedListener(this);
        tvRepeat.addTextChangedListener(this);
        itemSitch.setOnPerformCheckedChangeListener(this);
        isAutoDelete.setChecked(true);
        rlAutoDelete.setVisibility(View.GONE);
    }

    @Override
    public void afterInjectView(View view) {
        mBean = getIntent().getParcelableExtra(ID);
        tvTitle.setText(R.string.alarmcreate_title);
        initData();
        if (mBean != null) {
            Date date = new Date(mBean.datetime);
            timePicker.setCurrentHour(date.getHours());
            timePicker.setCurrentMinute(date.getMinutes());
            int index = mRepeatType.indexOf(mBean.circle);
            if (index >= 0) {
                tvRepeat.setText(mRepeatText.get(index));
            }
            tvDesc.setText(mBean.event);
        } else {
            mBean = new AlarmClockBean();
            mBean.id = -1;
            Date date = new Date();
            timePicker.setCurrentHour(date.getHours());
            timePicker.setCurrentMinute(date.getMinutes());
        }
    }

    private void initData() {
        mRepeatText.clear();
        mRepeatType.clear();
        mRepeatText.add(getString(R.string.alarmcreate_repeat_once));
        mRepeatType.add(DeviceClock.CIRCLE_ONCE);
        mRepeatText.add(getString(R.string.alarmcreate_repeat_everyday));
        mRepeatType.add(DeviceClock.CIRCLE_EVERYDAY);
        mRepeatText.add(getString(R.string.alarmcreate_repeat_workday));
        mRepeatType.add(DeviceClock.CIRCLE_WORKDAY);
        mRepeatText.add(getString(R.string.alarmcreate_repeat_holiday));
        mRepeatType.add(DeviceClock.CIRCLE_HOLIDAY);
        mRepeatText.add(getString(R.string.alarmcreate_repeat_custom));
        mRepeatType.add(DeviceClock.CIRCLE_CUSTOM);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.rlRepeat:
                showRepeatDialog();
                break;
            case R.id.rlRing:
                break;
            case R.id.rlContinue:
                break;
            case R.id.title_bar_more:
                requestCommit();
                break;
            case R.id.rlDesc:
                MLAlertDialog.Builder builder = new MLAlertDialog.Builder(activity());
                builder.setTitle(getString(R.string.alarmcreate_desc_title)).setInputView(getString(R.string.alarmcreate_desc_hint), true);
                final EditText inputView = builder.getInputView();
                builder.setNegativeButton(R.string.alarmcreate_desc_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                }).setPositiveButton(R.string.alarmcreate_desc_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mBean.reminder = inputView.getText().toString();
                        tvDesc.setText(mBean.reminder);
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                }).create().show();
                break;
        }
    }

    private void requestCommit() {
        try {
            mBean.event = tvDesc.getText().toString();
            if (TextUtils.isEmpty(mBean.circle)) {
                Toast.makeText(activity(), R.string.alarmclock_close_circle_hint, Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(mBean.event)) {
                Toast.makeText(activity(), R.string.alarmclock_close_desc_hint, Toast.LENGTH_LONG).show();
            } else {
                mBean.circle_extra = FormatUtils.cron(mBean.circle, timePicker, mChoseWeek);
                mBean.datetime = FormatUtils.targetTime(mBean.circle, timePicker, mChoseWeek);
                JSONObject params = new JSONObject();
                JSONArray array = new JSONArray();
                JSONObject data = new JSONObject();
                if (mBean.id == -1) {
                    params.put("operation", DeviceClock.OPERATION_CREATE);
                } else {
                    params.put("operation", DeviceClock.OPERATION_MODIFY);
                    data.put("id", mBean.id);
                }
                data.put("type", DeviceClock.TYPE_ALARM);
                data.put("event", mBean.event);
                data.put("reminder", "");
                data.put("ringtone", mBean.ringtone);
                data.put("reminder_audio", mBean.ringtone);
                data.put("volume", 100);
                data.put("circle", mBean.circle);
                data.put("circle_extra", mBean.circle_extra);
                data.put("event_timestamp", mBean.datetime);
                array.put(data);
                params.put("data", array);
                params.put("parser_timestamp", System.currentTimeMillis());
                DeviceClock.getDevice(getDeviceStat()).callMethod(DeviceClock.METHORD_ALARM_OPS, params, new Callback<String>() {

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
                            LogUtils.e(TAG, e);
                            onError(ERROR_JSON);
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        LogUtils.i(TAG, i + s);
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
                                    for (boolean aBoolean : booleans) {
                                        if (aBoolean) {//必须至少选一个才保存数据
                                            mChoseWeek = booleans;//确认保存数据到全局变量
                                            mBean.circle = typeChose;
                                            tvRepeat.setText(choseText);
                                            if (dialogInterface != null) {
                                                dialogInterface.dismiss();
                                            }
                                        }
                                    }
                                }
                            }).create().show();
                        } else {
                            mBean.circle = typeChose;
                            tvRepeat.setText(choseText);
                        }
                    }
                }).create().show();
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
            rlAutoDelete.setVisibility(View.VISIBLE);
        } else {
            rlAutoDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        final SharedPreferences sharedPreferences = activity().getSharedPreferences(DeviceClock.SHAREPREFERENCE, Context.MODE_PRIVATE);
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
                    }).create().show();
        }
    }
}
