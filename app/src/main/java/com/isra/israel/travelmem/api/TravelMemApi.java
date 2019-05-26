package com.isra.israel.travelmem.api;

import com.isra.israel.travelmem.model.FirebasePOSTResponse;
import com.isra.israel.travelmem.model.Travel;

import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TravelMemApi {
    String JSON = ".json";
    String SYNC_DATA = "syncData/";
    String USERS = "users/";
    String TRAVELS = SYNC_DATA + "travels/";

    @GET(JSON)
    Call<ResponseBody> verifyIdToken(@Query(value = "auth") String idToken);

    @GET(TRAVELS + "{uid}/" + JSON)
    Observable<HashMap<String, Travel>> getTravels(@Path(value = "uid") String uid, @Query(value = "auth") String idToken);

    @POST(TRAVELS + "{uid}/" + JSON)
    Observable<FirebasePOSTResponse> addTravel(@Path(value = "uid") String uid, @Query(value = "auth") String idToken, @Body Travel travel);

    @PATCH(TRAVELS + "{uid}/{travelId}" + JSON)
    Observable<ResponseBody> updateTravel(@Path(value = "uid") String uid, @Path(value = "travelId") String travelId, @Query(value = "auth") String idToken, @Body Travel travel);

    @DELETE(TRAVELS + "{uid}/{travelId}" + JSON)
    Observable<ResponseBody> deleteTravel(@Path(value = "uid") String uid, @Path(value = "travelId") String travelId, @Query(value = "auth") String idToken);

    @GET(USERS + "{uid}/" + JSON)
    Call<ResponseBody> getUser(@Path(value = "uid") String uid, @Query(value = "auth") String idToken);
}
