package com.isra.israel.travelmem.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class TravelMedia implements Parcelable {

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

    public TravelMedia() {

    }

    protected TravelMedia(Parcel in) {
        type = in.readInt();
        uri = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        description = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(uri);
        dest.writeParcelable(latLng, flags);
        dest.writeString(description);
    }
}
