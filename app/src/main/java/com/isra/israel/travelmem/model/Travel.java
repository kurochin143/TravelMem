package com.isra.israel.travelmem.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isra.israel.travelmem.model.directions.Route;

import java.io.Serializable;
import java.util.ArrayList;

// TODO MEDIUM parcelable
public class Travel implements Serializable {

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
    private LatLng origin;

    @SerializedName("destination")
    @Expose
    private LatLng destination;

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

    public LatLng getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
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
}
