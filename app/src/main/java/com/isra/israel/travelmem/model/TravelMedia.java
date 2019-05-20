package com.isra.israel.travelmem.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TravelMedia {

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    protected int type;

    @SerializedName("uri")
    @Expose
    private String uri;

    @SerializedName("location")
    @Expose
    private LatLng latLng;

    @SerializedName("description")
    @Expose
    private String description;

    public int getType() {
        return type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
