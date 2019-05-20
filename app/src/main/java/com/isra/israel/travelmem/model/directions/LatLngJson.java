package com.isra.israel.travelmem.model.directions;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class LatLngJson {

    public static LatLng fromJson(JsonElement json) {
        JsonObject latLngJson = json.getAsJsonObject();

        return new LatLng(latLngJson.get("lat").getAsNumber().doubleValue(), latLngJson.get("lng").getAsNumber().doubleValue());
    }

    public static class RetrofitLatLngSerializer implements JsonSerializer<LatLng> {
        @Override
        public JsonElement serialize(LatLng src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }

            JsonObject latLngJson = new JsonObject();
            latLngJson.add("lat", new JsonPrimitive(src.latitude));
            latLngJson.add("lng", new JsonPrimitive(src.longitude));
            return latLngJson;
        }
    }

    public static class RetrofitLatLngDeserializer implements JsonDeserializer<LatLng> {

        @Override
        public LatLng deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return fromJson(json);
        }
    }
}
