package com.isra.israel.travelmem.api;

import com.isra.israel.travelmem.gson.TravelMemGson;
import com.isra.israel.travelmem.model.directions.GoogleDirectionsResult;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class GoogleDirectionsAPIDAO {

    private static final int URL_LEN_LIMIT = 8192;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";

    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            //.addInterceptor(interceptor)
            .build();

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(TravelMemGson.gson))
            .client(okHttpClient)
            .build();
    public static GoogleDirectionsInterface apiService = retrofit.create(GoogleDirectionsInterface.class);

    public interface GoogleDirectionsInterface {
        @GET("json")
        Call<GoogleDirectionsResult> getDirection(
                @Query(value = "key") String apiKey,
                @Query(value = "origin") String origin,
                @Query(value = "destination") String destination,
                @Query(value = "waypoints") String waypoints
        );

    }
}
