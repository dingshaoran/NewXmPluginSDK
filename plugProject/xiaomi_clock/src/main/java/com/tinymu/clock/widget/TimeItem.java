package com.tinymu.clock.widget;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by livy on 16/9/14.
 */
public class TimeItem implements Parcelable, Comparable<TimeItem> {
    public long startTime;//单位为毫秒
    public long duration;
    public long endTime;
    public int isSaveFile;//是否是永久保存文件0否 1 是

    public TimeItem(long startTime, long duration, int isSaveFile) {
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime + duration;
        this.isSaveFile = isSaveFile;
    }

    protected TimeItem(Parcel in) {
        startTime = in.readLong();
        duration = in.readLong();
        endTime = in.readLong();
        isSaveFile = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startTime);
        dest.writeLong(duration);
        dest.writeLong(endTime);
        dest.writeInt(isSaveFile);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TimeItem> CREATOR = new Creator<TimeItem>() {
        @Override
        public TimeItem createFromParcel(Parcel in) {
            return new TimeItem(in);
        }

        @Override
        public TimeItem[] newArray(int size) {
            return new TimeItem[size];
        }
    };


    public static int timeItemLen() {
        return 4;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean contains(long time) {
        return time >= startTime && time < endTime;
    }

    @Override
    public int compareTo(TimeItem another) {
        return (int) (this.startTime - another.startTime);
    }

    @Override
    public int hashCode() {
        return (int) (this.startTime/1000);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TimeItem)) {
            return false;
        }
        return this.startTime == ((TimeItem) o).startTime;
    }
}
