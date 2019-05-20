package com.isra.israel.travelmem.model.directions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Leg implements Parcelable {

    @SerializedName("steps")
    @Expose
    private ArrayList<Step> steps;

    @SerializedName("distance")
    @Expose
    private TextValue distance; // meters

    @SerializedName("duration")
    @Expose
    private TextValue duration; // seconds

    @SerializedName("start_address")
    @Expose
    private String startAddress;

    @SerializedName("end_address")
    @Expose
    private String endAddress;

    @SerializedName("start_location")
    @Expose
    private LatLng startLocation;

    @SerializedName("end_location")
    @Expose
    private LatLng endLocation;

    protected Leg(Parcel in) {
        steps = in.createTypedArrayList(Step.CREATOR);
        distance = in.readParcelable(TextValue.class.getClassLoader());
        duration = in.readParcelable(TextValue.class.getClassLoader());
        startAddress = in.readString();
        endAddress = in.readString();
        startLocation = in.readParcelable(LatLng.class.getClassLoader());
        endLocation = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<Leg> CREATOR = new Creator<Leg>() {
        @Override
        public Leg createFromParcel(Parcel in) {
            return new Leg(in);
        }

        @Override
        public Leg[] newArray(int size) {
            return new Leg[size];
        }
    };

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public TextValue getDistance() {
        return distance;
    }

    public void setDistance(TextValue distance) {
        this.distance = distance;
    }

    public TextValue getDuration() {
        return duration;
    }

    public void setDuration(TextValue duration) {
        this.duration = duration;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(steps);
        dest.writeParcelable(distance, flags);
        dest.writeParcelable(duration, flags);
        dest.writeString(startAddress);
        dest.writeString(endAddress);
        dest.writeParcelable(startLocation, flags);
        dest.writeParcelable(endLocation, flags);
    }
}
