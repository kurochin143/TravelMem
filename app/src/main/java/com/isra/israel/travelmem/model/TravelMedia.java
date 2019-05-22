package com.isra.israel.travelmem.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isra.israel.travelmem.model.directions.Point;

public abstract class TravelMedia implements Parcelable {

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    protected int type;

    @SerializedName("uriStr")
    @Expose
    private String uriStr;

    @SerializedName("location")
    @Expose
    private Point location;

    @SerializedName("description")
    @Expose
    private String description;

    public TravelMedia() {

    }

    protected TravelMedia(Parcel in) {
        type = in.readInt();
        uriStr = in.readString();
        location = in.readParcelable(Point.class.getClassLoader());
        description = in.readString();
    }

    public int getType() {
        return type;
    }

    public Uri getUri() {
        return Uri.parse(uriStr);
    }

    public String getUriStr() {
        return uriStr;
    }

    public void setUriStr(String uriStr) {
        this.uriStr = uriStr;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
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
        dest.writeString(uriStr);
        dest.writeParcelable(location, flags);
        dest.writeString(description);
    }
}
