package com.lerenard.counter3;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lerenard on 16-Aug-16.
 */
public class Count implements Parcelable {
    private String name;
    private int count;

    protected Count(Count count) {
        copyFrom(count);
    }

    protected Count(Parcel in) {
        name = in.readString();
        count = in.readInt();
    }

    public static final Creator<Count> CREATOR = new Creator<Count>() {
        @Override
        public Count createFromParcel(Parcel in) {
            return new Count(in);
        }

        @Override
        public Count[] newArray(int size) {
            return new Count[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    Count(String n, int c) {
        name = n;
        count = c;
    }

    Count(String n) {
        this(n, 0);
    }

    Count() {this("");}

    public String toString() {
        return "<Count(\"" + name + "\", " + count + ")>";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(count);
    }

    public void copyFrom(Count count) {
        name = count.name;
        this.count = count.count;
    }
}
