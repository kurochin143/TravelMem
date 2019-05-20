package com.isra.israel.travelmem.model.directions;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Leg {

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
}
