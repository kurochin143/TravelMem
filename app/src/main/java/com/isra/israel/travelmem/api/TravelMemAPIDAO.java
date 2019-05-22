package com.isra.israel.travelmem.api;

import android.util.Pair;

import com.isra.israel.travelmem.gson.TravelMemGson;
import com.isra.israel.travelmem.model.FirebasePOSTResponse;
import com.isra.israel.travelmem.model.Travel;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class TravelMemAPIDAO {

    private static final String BASE_URL = "https://lambda-project-week-w10-f.firebaseio.com/";
    private static final String JSON = ".json";
    private static final String SYNC_DATA = "syncData/";
    private static final String USERS = "users/";
    private static final String TRAVELS = SYNC_DATA + "travels/";

    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .build();

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(TravelMemGson.gson))
            .client(okHttpClient)
            .build();
    public static TravelMemAPIInterface apiService = retrofit.create(TravelMemAPIInterface.class);

    public interface TravelMemAPIInterface {
        @GET(JSON)
        Call<ResponseBody> verifyIdToken(@Query(value = "auth") String idToken);

        @GET(TRAVELS + "{uid}/" + JSON)
        Call<HashMap<String, Travel>> getTravels(@Path(value = "uid") String uid, @Query(value = "auth") String idToken);

        @POST(TRAVELS + "{uid}/" + JSON)
        Call<FirebasePOSTResponse> addTravel(@Path(value = "uid") String uid, @Query(value = "auth") String idToken, @Body Travel travel);

        @PATCH(TRAVELS + "{uid}/{travelId}" + JSON)
        Call<ResponseBody> updateTravel(@Path(value = "uid") String uid, @Path(value = "travelId") String travelId, @Query(value = "auth") String idToken, @Body Travel travel);

        @DELETE(TRAVELS + "{uid}/{travelId}" + JSON)
        Call<ResponseBody> deleteTravel(@Path(value = "uid") String uid, @Path(value = "travelId") String travelId, @Query(value = "auth") String idToken);

        @GET(USERS + "{uid}/" + JSON)
        Call<ResponseBody> getUser(@Path(value = "uid") String uid, @Query(value = "auth") String idToken);
    }

}
