package com.tinymu.clock.alarmclock;

import android.os.Parcel;
import android.os.Parcelable;

import com.tinymu.clock.utils.FormatUtils;
import com.tinymu.clock.utils.ISO8601Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.List;

public class AlarmClockBean implements Parcelable {
    public long datetime;//事件儿触发时间,单位毫秒.
    public long update_datetime;//消息触发的时间戳,即闹钟最近一次更新的时间点儿。UTC时间，单位毫秒
    public String circle;//循环周期举例:1次（once），工作日（workday），周一到周五(montofri)，每天(everyday)，节假日(holiday),周末(weekend), 每周(everyweek),每月(monthly),每年(yearly)。 对于倒计时功能的时候,该字段固定为”once”
    public String reminder;
    public int type; //      alarm.    闹钟 .典型用户query:"十点提醒我抢手机"     reminder. 提醒.典型用户query:"五分钟后提醒我抢手机"    timer.     倒计时，典型的用户query:”倒计时120秒”
    public int id;//这个id是设备端在插入数据库时自动生成的ID。当修改、删除闹钟的时候需要指定该字段，添加闹钟的时候不要指定。
    public String operation;//operation create:新建close：闹铃响时，关闭闹铃   以下只需音箱播放tts，无需其它操作： open:打开 query:查询,查询返回的结构是alarm json array. delete:删除 modify:修改 stop:只有倒计时的用得到，倒计时停止 cancle_delete:取消删除
    public boolean delete = false;//本地字段  保存了是否点击了删除
    public String circle_extra;
    public String disable_datetime;
    public String event;//标记 无 红 黄 蓝 绿 各对应一个index
    public String volume;
    public String ringtone;
    public String status;

    public AlarmClockBean() {
    }

    protected AlarmClockBean(Parcel in) {
        datetime = in.readLong();
        update_datetime = in.readLong();
        circle = in.readString();
        reminder = in.readString();
        type = in.readInt();
        id = in.readInt();
        operation = in.readString();
        delete = in.readByte() != 0;
        circle_extra = in.readString();
        disable_datetime = in.readString();
        event = in.readString();
        volume = in.readString();
        ringtone = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(datetime);
        dest.writeLong(update_datetime);
        dest.writeString(circle);
        dest.writeString(reminder);
        dest.writeInt(type);
        dest.writeInt(id);
        dest.writeString(operation);
        dest.writeByte((byte) (delete ? 1 : 0));
        dest.writeString(circle_extra);
        dest.writeString(disable_datetime);
        dest.writeString(event);
        dest.writeString(volume);
        dest.writeString(ringtone);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlarmClockBean> CREATOR = new Creator<AlarmClockBean>() {
        @Override
        public AlarmClockBean createFromParcel(Parcel in) {
            return new AlarmClockBean(in);
        }

        @Override
        public AlarmClockBean[] newArray(int size) {
            return new AlarmClockBean[size];
        }
    };

    public static void parseList(JSONArray result, List<AlarmClockBean> mList) throws ParseException {
        for (int i = 0; i < result.length(); i++) {
            JSONObject item = result.optJSONObject(i);
            final AlarmClockBean bean = new AlarmClockBean();
            bean.circle = FormatUtils.optString(item, "circle");
            bean.circle_extra = FormatUtils.optString(item, "circle_extra");
            bean.datetime = ISO8601Utils.parse(FormatUtils.optString(item, "datetime"), new ParsePosition(0)).getTime();
            bean.disable_datetime = FormatUtils.optString(item, "disable_datetime");
            bean.event = FormatUtils.optString(item, "event");
            bean.reminder = FormatUtils.optString(item, "reminder");
            bean.status = FormatUtils.optString(item, "status");
            bean.ringtone = FormatUtils.optString(item, "ringtone");
            bean.type = item.optInt("type", 0);
            bean.id = item.optInt("id", -1);
            bean.operation = FormatUtils.optString(item, "operation");
            bean.update_datetime = ISO8601Utils.parse(FormatUtils.optString(item, "update_datetime"), new ParsePosition(0)).getTime();
            bean.volume = FormatUtils.optString(item, "volume");
            if (bean.id != -1) {
                mList.add(bean);
            }
        }
    }
}
