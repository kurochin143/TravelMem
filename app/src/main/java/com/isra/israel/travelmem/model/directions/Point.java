package com.isra.israel.travelmem.model.directions;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

public class Point implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("latLng")
    @Expose
    private LatLng latLng;

    public Point() {
    }

    protected Point(Parcel in) {
        name = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(latLng, flags);
    }

    public static class RetrofitPointDeserializer implements JsonDeserializer<Point> {
        private static final Gson gson = new Gson();

        @Override
        public Point deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject pointJson = json.getAsJsonObject();

            // when deserializing from TravelMem api
            if (pointJson.has("name") || pointJson.has("latLng")) { // it's a Point
                return gson.fromJson(json, Point.class);
            }

            // when deserializing from google directions api
            // it's a LatLng

            Point point = new Point();
            point.setLatLng(LatLngJson.fromJson(json));

            return point;
        }
    }
}
