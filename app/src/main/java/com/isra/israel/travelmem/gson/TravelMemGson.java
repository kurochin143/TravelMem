package com.isra.israel.travelmem.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TravelMemGson {

    public static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

}
