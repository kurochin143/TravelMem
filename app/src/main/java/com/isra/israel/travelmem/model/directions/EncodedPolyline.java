package com.isra.israel.travelmem.model.directions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EncodedPolyline {

    @SerializedName("points")
    @Expose
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
