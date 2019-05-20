package com.isra.israel.travelmem.model.directions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step {

    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions;

    @SerializedName("distance")
    @Expose
    private TextValue distance; // meters

    @SerializedName("duration")
    @Expose
    private TextValue duration; // seconds

    @SerializedName("start_location")
    @Expose
    private Point start;

    @SerializedName("end_location")
    @Expose
    private Point end;

    @SerializedName("travel_mode")
    @Expose
    private String travelMode;

    @SerializedName("polyline")
    @Expose
    private EncodedPolyline polyline;

    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
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

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public EncodedPolyline getPolyline() {
        return polyline;
    }

    public void setPolyline(EncodedPolyline polyline) {
        this.polyline = polyline;
    }
}
