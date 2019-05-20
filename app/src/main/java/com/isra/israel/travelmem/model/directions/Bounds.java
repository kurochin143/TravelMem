package com.isra.israel.travelmem.model.directions;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bounds {

    @SerializedName("northeast")
    @Expose
    private LatLng northEast;

    @SerializedName("southwest")
    @Expose
    private LatLng southWest;

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
}
