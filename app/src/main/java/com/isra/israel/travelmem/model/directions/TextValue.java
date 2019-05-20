package com.isra.israel.travelmem.model.directions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TextValue implements Parcelable {

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("value")
    @Expose
    private long value;

    protected TextValue(Parcel in) {
        text = in.readString();
        value = in.readLong();
    }

    public static final Creator<TextValue> CREATOR = new Creator<TextValue>() {
        @Override
        public TextValue createFromParcel(Parcel in) {
            return new TextValue(in);
        }

        @Override
        public TextValue[] newArray(int size) {
            return new TextValue[size];
        }
    };

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeLong(value);
    }
}
