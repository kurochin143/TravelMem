package com.isra.israel.travelmem.model.directions;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isra.israel.travelmem.gson.TravelMemGson;

import java.lang.reflect.Type;

public class Point {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("latLng")
    @Expose
    private LatLng latLng;

    public Point() {
    }

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

    public static class RetrofitPointDeserializer implements JsonDeserializer<Point> {
        @Override
        public Point deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject pointJson = json.getAsJsonObject();

            // when deserializing from TravelMem api
            if (pointJson.has("name")) { // it's a Point
                return TravelMemGson.gson.fromJson(json, Point.class);
            }

            // when deserializing from google directions api
            // it's a LatLng

            Point point = new Point();
            point.setLatLng(LatLngJson.fromJson(json));

            return point;
        }
    }
}
