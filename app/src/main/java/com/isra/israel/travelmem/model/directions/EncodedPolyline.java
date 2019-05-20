package com.isra.israel.travelmem.model.directions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EncodedPolyline implements Parcelable {

    @SerializedName("points")
    @Expose
    private String points;

    protected EncodedPolyline(Parcel in) {
        points = in.readString();
    }

    public static final Creator<EncodedPolyline> CREATOR = new Creator<EncodedPolyline>() {
        @Override
        public EncodedPolyline createFromParcel(Parcel in) {
            return new EncodedPolyline(in);
        }

        @Override
        public EncodedPolyline[] newArray(int size) {
            return new EncodedPolyline[size];
        }
    };

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(points);
    }
}
