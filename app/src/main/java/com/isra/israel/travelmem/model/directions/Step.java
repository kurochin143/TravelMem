package com.isra.israel.travelmem.model.directions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step implements Parcelable {

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

    protected Step(Parcel in) {
        htmlInstructions = in.readString();
        distance = in.readParcelable(TextValue.class.getClassLoader());
        duration = in.readParcelable(TextValue.class.getClassLoader());
        start = in.readParcelable(Point.class.getClassLoader());
        end = in.readParcelable(Point.class.getClassLoader());
        travelMode = in.readString();
        polyline = in.readParcelable(EncodedPolyline.class.getClassLoader());
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(htmlInstructions);
        dest.writeParcelable(distance, flags);
        dest.writeParcelable(duration, flags);
        dest.writeParcelable(start, flags);
        dest.writeParcelable(end, flags);
        dest.writeString(travelMode);
        dest.writeParcelable(polyline, flags);
    }
}
