package com.isra.israel.travelmem.model.directions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bounds implements Parcelable {

    @SerializedName("northeast")
    @Expose
    private LatLng northEast;

    @SerializedName("southwest")
    @Expose
    private LatLng southWest;

    protected Bounds(Parcel in) {
        northEast = in.readParcelable(LatLng.class.getClassLoader());
        southWest = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<Bounds> CREATOR = new Creator<Bounds>() {
        @Override
        public Bounds createFromParcel(Parcel in) {
            return new Bounds(in);
        }

        @Override
        public Bounds[] newArray(int size) {
            return new Bounds[size];
        }
    };

    public LatLng getNorthEast() {
        return northEast;
    }

    public void setNorthEast(LatLng northEast) {
        this.northEast = northEast;
    }

    public LatLng getSouthWest() {
        return southWest;
    }

    public void setSouthWest(LatLng southWest) {
        this.southWest = southWest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(northEast, flags);
        dest.writeParcelable(southWest, flags);
    }
}
