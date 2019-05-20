package com.isra.israel.travelmem.model.directions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Route {

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
}
