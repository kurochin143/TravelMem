package com.isra.israel.travelmem.model.directions;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

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

    @NonNull
    @Override
    public String toString() {
        StringBuilder outStrB = new StringBuilder();
        if (name != null) {
            outStrB.append(name);
            outStrB.append(".");
        }

        if (latLng != null) {
            outStrB.append(" ");
            outStrB.append("(");
            outStrB.append(latLng.latitude);
            outStrB.append(", ");
            outStrB.append(latLng.longitude);
            outStrB.append(")");
        }

        return outStrB.toString();
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

        @Override
        public Point deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject pointJson = json.getAsJsonObject();

            // when deserializing from TravelMem api
            boolean hasName = pointJson.has("name");
            boolean hasLatLng = pointJson.has("latLng");
            if (hasName || hasLatLng) { // it's a Point
                Point point = new Point();
                if (hasName) {
                    point.setName(pointJson.get("name").getAsString());
                }
                if (hasLatLng) {
                    point.setLatLng(LatLngJson.fromJson(pointJson.get("latLng")));
                }
                // TODO ? Why does gson.fromJson(json, Point.class) doesn't stringify latLng
                return point;
            }

            // when deserializing from google directions api
            // it's a LatLng

            Point point = new Point();
            point.setLatLng(LatLngJson.fromJson(json));

            return point;
        }
    }
}
