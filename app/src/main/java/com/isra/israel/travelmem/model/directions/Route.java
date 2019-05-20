package com.isra.israel.travelmem.model.directions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Route implements Parcelable {

    @SerializedName("legs")
    @Expose
    private ArrayList<Leg> legs;

    @SerializedName("overview_polyline")
    @Expose
    private EncodedPolyline overviewPolyline;

    @SerializedName("bounds")
    @Expose
    private Bounds bounds;

    @SerializedName("copyrights")
    @Expose
    private String copyrights;

    protected Route(Parcel in) {
        legs = in.createTypedArrayList(Leg.CREATOR);
        overviewPolyline = in.readParcelable(EncodedPolyline.class.getClassLoader());
        bounds = in.readParcelable(Bounds.class.getClassLoader());
        copyrights = in.readString();
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    public ArrayList<Leg> getLegs() {
        return legs;
    }

    public void setLegs(ArrayList<Leg> legs) {
        this.legs = legs;
    }

    public EncodedPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(EncodedPolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(legs);
        dest.writeParcelable(overviewPolyline, flags);
        dest.writeParcelable(bounds, flags);
        dest.writeString(copyrights);
    }
}
