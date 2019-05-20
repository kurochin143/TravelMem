package com.isra.israel.travelmem.gson;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.isra.israel.travelmem.model.directions.LatLngJson;
import com.isra.israel.travelmem.model.directions.Point;

public class TravelMemGson {

    public static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(LatLng.class, new LatLngJson.RetrofitLatLngSerializer())
            .registerTypeAdapter(LatLng.class, new LatLngJson.RetrofitLatLngDeserializer())
            .registerTypeAdapter(Point.class, new Point.RetrofitPointDeserializer())
            .create();

}
