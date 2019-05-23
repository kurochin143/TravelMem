package com.isra.israel.travelmem.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isra.israel.travelmem.model.directions.Point;
import com.isra.israel.travelmem.model.directions.Route;

import java.util.ArrayList;

public class Travel implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("startDate")
    @Expose
    private String startDate;

    @SerializedName("endDate")
    @Expose
    private String endDate;

    @SerializedName("origin")
    @Expose
    private Point origin;

    @SerializedName("destination")
    @Expose
    private Point destination;

    @SerializedName("route")
    @Expose
    private Route route;

    @SerializedName("images")
    @Expose
    private ArrayList<TravelImage> images;

    @SerializedName("videos")
    @Expose
    private ArrayList<TravelVideo> videos;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("creationTime")
    @Expose
    private long creationTime;

    @SerializedName("lastUpdatedTime")
    @Expose
    private long lastUpdatedTime;

    @SerializedName("shouldNotify")
    @Expose
    private int shouldNotify;

    public Travel() {

    }

    protected Travel(Parcel in) {
        id = in.readString();
        name = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        origin = in.readParcelable(Point.class.getClassLoader());
        destination = in.readParcelable(Point.class.getClassLoader());
        route = in.readParcelable(Route.class.getClassLoader());
        images = in.createTypedArrayList(TravelImage.CREATOR);
        videos = in.createTypedArrayList(TravelVideo.CREATOR);
        description = in.readString();
        creationTime = in.readLong();
        lastUpdatedTime = in.readLong();
        shouldNotify = in.readInt();
    }

    public static final Creator<Travel> CREATOR = new Creator<Travel>() {
        @Override
        public Travel createFromParcel(Parcel in) {
            return new Travel(in);
        }

        @Override
        public Travel[] newArray(int size) {
            return new Travel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Point getOrigin() {
        return origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public ArrayList<TravelImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<TravelImage> images) {
        this.images = images;
    }

    public ArrayList<TravelVideo> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<TravelVideo> videos) {
        this.videos = videos;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public int shouldNotify() {
        return shouldNotify;
    }

    public void setShouldNotify(int shouldNotify) {
        this.shouldNotify = shouldNotify;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeParcelable(origin, flags);
        dest.writeParcelable(destination, flags);
        dest.writeParcelable(route, flags);
        dest.writeTypedList(images);
        dest.writeTypedList(videos);
        dest.writeString(description);
        dest.writeLong(creationTime);
        dest.writeLong(lastUpdatedTime);
        dest.writeInt(shouldNotify);
    }
}
