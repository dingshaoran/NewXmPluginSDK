package com.tinymu.clock.utils;

import android.content.Context;

import com.tinymu.clock.DeviceClock;
import com.xiaomi.smarthome.common.ui.widget.TimePicker;
import com.zimi.clockmyk.R;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FormatUtils {

    public static String afterNow(Context mContext, String time) {

        if (time != null) {
            String[] split = time.split(":");
            if (split.length == 2) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.set(Calendar.HOUR_OF_DAY, parseInt(split[0], 0));
                calendar.set(Calendar.MINUTE, parseInt(split[1], 0));
                calendar.set(Calendar.ZONE_OFFSET, 4);
                long l = System.currentTimeMillis() - calendar.getTimeInMillis();
                if (l < 0) {
                    l += 3600000 * 24;
                }
                int hour = (int) (l / 3600000);
                int minute = (int) ((l % 3600000) / 60000);
                return mContext.getString(R.string.time_after_hint_hour, String.valueOf(hour), String.valueOf(minute));
            }
        }
        return mContext.getString(R.string.time_after_hint_hour, "-", "-");
    }

    public static String afterNow(Context mContext, long time) {
        Calendar calendarAlarm = Calendar.getInstance(TimeZone.getDefault());
        Calendar calendarNow = Calendar.getInstance(TimeZone.getDefault());
        calendarAlarm.setTime(new Date(time));
        calendarNow.setTime(new Date());
        int minuteAlarm = calendarAlarm.get(Calendar.MINUTE);
        int minuteNow = calendarNow.get(Calendar.MINUTE);
        int hourAlarm = calendarAlarm.get(Calendar.HOUR_OF_DAY);
        int hourNow = calendarNow.get(Calendar.HOUR_OF_DAY);
        int dayAlarm = calendarAlarm.get(Calendar.DAY_OF_YEAR);
        int dayNow = calendarNow.get(Calendar.DAY_OF_YEAR);
        int dMinute = minuteAlarm - minuteNow;
        int dHour = hourAlarm - hourNow;
        int dDay = dayAlarm - dayNow;
        if (dMinute < 0) {
            dMinute += 60;
            dHour -= 1;
        }
        if (dHour < 0) {
            dHour += 24;
            dDay -= 1;
        }
        if (dDay > 0) {
            return mContext.getString(R.string.time_after_hint_day, String.valueOf(dDay), String.valueOf(dHour), String.valueOf(dMinute));
        } else if (dHour > 0) {
            return mContext.getString(R.string.time_after_hint_hour, String.valueOf(dHour), String.valueOf(dMinute));
        } else {
            return mContext.getString(R.string.time_after_hint_minute, String.valueOf(dMinute));
        }
    }

    public static int parseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long parseLong(String s, int defaultValue) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String parseCircle(Context mContext, String type, String circle_extra) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case DeviceClock.CIRCLE_ONCE:
                return mContext.getString(R.string.circle_once);
            case DeviceClock.CIRCLE_WORKDAY:
                return mContext.getString(R.string.circle_workday);
            case DeviceClock.CIRCLE_MONTOFRI:
                return mContext.getString(R.string.circle_montofri);
            case DeviceClock.CIRCLE_EVERYDAY:
                return mContext.getString(R.string.circle_oeveryday);
            case DeviceClock.CIRCLE_HOLIDAY:
                return mContext.getString(R.string.circle_holiday);
            case DeviceClock.CIRCLE_WEEKEND:
                return mContext.getString(R.string.circle_weekend);
            case DeviceClock.CIRCLE_EVERYWEEK:
                return mContext.getString(R.string.circle_everyweek);
            case DeviceClock.CIRCLE_MONTHLY:
                return mContext.getString(R.string.circle_monthly);
            case DeviceClock.CIRCLE_YEARLY:
                return mContext.getString(R.string.circle_yearly);
            case DeviceClock.CIRCLE_CUSTOM:
                StringBuilder stringBuilder = new StringBuilder();
                if (circle_extra != null) {
                    String[] split = circle_extra.split(" ");
                    if (split.length > 5) {
                        String[] week = split[5].split(",");
                        for (String s : week) {
                            switch (s) {
                                case "1":
                                    stringBuilder.append(mContext.getString(R.string.alarmclock_monday_list));
                                    break;
                                case "2":
                                    if (stringBuilder.length() != 0) {
                                        stringBuilder.append(",");
                                    }
                                    stringBuilder.append(mContext.getString(R.string.alarmclock_tuesday_list));
                                    break;
                                case "3":
                                    if (stringBuilder.length() != 0) {
                                        stringBuilder.append(",");
                                    }
                                    stringBuilder.append(mContext.getString(R.string.alarmclock_wednesday_list));
                                    break;
                                case "4":
                                    if (stringBuilder.length() != 0) {
                                        stringBuilder.append(",");
                                    }
                                    stringBuilder.append(mContext.getString(R.string.alarmclock_thursday_list));
                                    break;
                                case "5":
                                    if (stringBuilder.length() != 0) {
                                        stringBuilder.append(",");
                                    }
                                    stringBuilder.append(mContext.getString(R.string.alarmclock_friday_list));
                                    break;
                                case "6":
                                    if (stringBuilder.length() != 0) {
                                        stringBuilder.append(",");
                                    }
                                    stringBuilder.append(mContext.getString(R.string.alarmclock_saturday_list));
                                    break;
                                case "7":
                                    if (stringBuilder.length() != 0) {
                                        stringBuilder.append(",");
                                    }
                                    stringBuilder.append(mContext.getString(R.string.alarmclock_sunday_list));
                                    break;
                            }
                        }
                    }
                }
                return stringBuilder.toString();
            default:
                return "";
        }
    }

    public static String optString(JSONObject item, String reminder) {
        if (item.isNull(reminder)) {
            return null;
        }
        return item.optString(reminder);
    }

    public static String cron(String circle, TimePicker timePicker, boolean[] mChoseWeek) {
        StringBuilder week = new StringBuilder();//工作日（workday），周一到周五(montofri)，每天(everyday)，节假日(holiday),周末(weekend), 每周(everyweek),每月(monthly),每年(yearly)。
        if (DeviceClock.CIRCLE_WEEKEND.equals(circle)) {
            week.append("6,7");
        } else if (DeviceClock.CIRCLE_MONTOFRI.equals(circle)) {
            week.append("1-5");
        } else if (DeviceClock.CIRCLE_EVERYWEEK.equals(circle)) {
            week.append("0/1");
        } else if (DeviceClock.CIRCLE_TWOWEEK.equals(circle)) {
            week.append("0/2");
        } else if (DeviceClock.CIRCLE_CUSTOM.equals(circle)) {
            for (int i = 0; i < mChoseWeek.length; i++) {
                if (mChoseWeek[i]) {
                    if (week.length() == 0) {
                        week.append(i + 1);//index 0 是星期一
                    } else {
                        week.append(",").append(i + 1);
                    }
                }
            }
        }
        return "0 " + timePicker.getCurrentMinute() + " " + timePicker.getCurrentHour()
                + (DeviceClock.CIRCLE_EVERYDAY.equals(circle) ? " 0/1 " : " ? ")
                + (DeviceClock.CIRCLE_MONTHLY.equals(circle) ? " 0/1 " : "? ")
                + week + (DeviceClock.CIRCLE_YEARLY.equals(circle) ? "  0/1" : " ?");
    }

    public static long targetTime(String circle, TimePicker timePicker, boolean[] mChoseWeek) {
        Date date = new Date();
        Integer currentHour = timePicker.getCurrentHour();
        Integer currentMinute = timePicker.getCurrentMinute();
        int day = date.getDay();
        switch (circle) {
            case DeviceClock.CIRCLE_ONCE:
            case DeviceClock.CIRCLE_EVERYDAY:
                addDay(date, currentHour, currentMinute, 1);
                break;
            case DeviceClock.CIRCLE_WORKDAY:
                if (day == Calendar.SUNDAY) {
                    date.setDate(date.getDate() + 1);
                } else if (day == Calendar.SATURDAY) {
                    date.setDate(date.getDate() + 2);
                } else if (day == Calendar.FRIDAY) {
                    addDay(date, currentHour, currentMinute, 3);
                } else {
                    addDay(date, currentHour, currentMinute, 1);
                }
                break;
            case DeviceClock.CIRCLE_MONTOFRI:
                if (day == Calendar.SUNDAY) {
                    date.setDate(date.getDate() + 1);
                } else if (day == Calendar.SATURDAY) {
                    date.setDate(date.getDate() + 2);
                } else if (day == Calendar.FRIDAY) {
                    addDay(date, currentHour, currentMinute, 3);
                } else {
                    addDay(date, currentHour, currentMinute, 1);
                }
                break;
            case DeviceClock.CIRCLE_HOLIDAY:
                break;
            case DeviceClock.CIRCLE_WEEKEND:
                if (day == Calendar.MONDAY) {
                    date.setDate(date.getDate() + 5);
                } else if (day == Calendar.TUESDAY) {
                    date.setDate(date.getDate() + 4);
                } else if (day == Calendar.WEDNESDAY) {
                    date.setDate(date.getDate() + 3);
                } else if (day == Calendar.THURSDAY) {
                    date.setDate(date.getDate() + 2);
                } else if (day == Calendar.FRIDAY) {
                    date.setDate(date.getDate() + 1);
                } else if (day == Calendar.SUNDAY) {
                    addDay(date, currentHour, currentMinute, 6);
                } else {
                    addDay(date, currentHour, currentMinute, 1);
                }
                break;
            case DeviceClock.CIRCLE_EVERYWEEK:
                addDay(date, currentHour, currentMinute, 7);
                break;
            case DeviceClock.CIRCLE_TWOWEEK:
                addDay(date, currentHour, currentMinute, 14);
                break;
            case DeviceClock.CIRCLE_MONTHLY:
                if (date.getHours() > currentHour) {
                    date.setMonth(date.getMonth() + 1);
                } else if (date.getHours() == currentHour) {//今天这个时间点过去了
                    if (date.getMinutes() >= currentMinute) {
                        date.setDate(date.getDate() + 1);
                    }
                }
                break;
            case DeviceClock.CIRCLE_YEARLY:
                if (date.getHours() > currentHour) {
                    date.setYear(date.getYear() + 1);
                } else if (date.getHours() == currentHour) {//今天这个时间点过去了
                    if (date.getMinutes() >= currentMinute) {
                        date.setYear(date.getYear() + 1);
                    }
                }
                break;
            case DeviceClock.CIRCLE_CUSTOM:
                int dayToIndex = 0;//当前是星期几 转换为index
                switch (day) {
                    case Calendar.MONDAY:
                    case Calendar.TUESDAY:
                    case Calendar.WEDNESDAY:
                    case Calendar.THURSDAY:
                    case Calendar.FRIDAY:
                    case Calendar.SATURDAY:
                        dayToIndex = day - 2;
                        break;
                    case Calendar.SUNDAY:
                        dayToIndex = mChoseWeek.length - 1;//最后一个是周日
                        break;
                }
                int dayFure = dayToIndex + 1;
                while (!mChoseWeek[dayFure % mChoseWeek.length]) {//如果今天这个响铃的时间点过去了，那么要在哪天响铃
                    dayFure++;
                }
                addDay(date, currentHour, currentMinute, dayFure - dayToIndex);
                break;
        }
        date.setHours(currentHour);
        date.setMinutes(currentMinute);
        date.setSeconds(0);
        return date.getTime();
    }

    private static void addDay(Date date, int currentHour, int currentMinute, int add) {
        if (date.getHours() > currentHour) {
            date.setDate(date.getDate() + add);
        } else if (date.getHours() == currentHour) {//今天这个时间点过去了
            if (date.getMinutes() >= currentMinute) {
                date.setDate(date.getDate() + add);
            }
        }
    }
}
