package com.isra.israel.travelmem.repository;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.isra.israel.travelmem.activity.TravelsActivity;
import com.isra.israel.travelmem.api.TravelMemAPIDAO;
import com.isra.israel.travelmem.api.TravelMemApi;
import com.isra.israel.travelmem.dao.FirebaseSessionSPDAO;
import com.isra.israel.travelmem.dao.TravelMemLocalCacheDAO;
import com.isra.israel.travelmem.model.FirebasePOSTResponse;
import com.isra.israel.travelmem.model.Travel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelsRepository {

    private TravelMemApi travelMemApi;

    public TravelsRepository(TravelMemApi travelMemApi) {
        this.travelMemApi = travelMemApi;
    }

    public Observable<ArrayList<Travel>> getTravels(final String uid, final String token) {
        return travelMemApi.getTravels(uid, token)
                .subscribeOn(Schedulers.io())
                .map(new Function<HashMap<String, Travel>, ArrayList<Travel>>() {
                    @Override
                    public ArrayList<Travel> apply(HashMap<String, Travel> idTravelMap) throws Exception {
                        ArrayList<Travel> travels = new ArrayList<>();
                        for (Map.Entry<String, Travel> entry : idTravelMap.entrySet()) {
                            Travel travel = entry.getValue();
                            travel.setId(entry.getKey());
                            travels.add(travel);
                        }

                return travels;
            }
        });
    }

    public Observable<Travel> addTravel(String uid, String token, final Travel travel) {
        return travelMemApi.addTravel(uid, token, travel)
                .subscribeOn(Schedulers.io())
                .map(new Function<FirebasePOSTResponse, Travel>() {
                    @Override
                    public Travel apply(FirebasePOSTResponse firebasePOSTResponse) throws Exception {
                        travel.setId(firebasePOSTResponse.name);
                        return travel;
                    }
                });
    }

    public Observable<String> removeTravel(String uid, final String id, String token) {
        return travelMemApi.deleteTravel(uid, id, token)
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) throws Exception {
                        return id;
                    }
                });
    }

    public Observable<Travel> updateTravel(String uid, String token, final Travel travel) {
        return travelMemApi.updateTravel(uid, travel.getId(), token, travel)
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, Travel>() {
                    @Override
                    public Travel apply(ResponseBody responseBody) throws Exception {
                        return travel;
                    }
                });
    }

//    public MutableLiveData<ArrayList<Travel>> getData(final String uid, String token) {
//        if (liveData == null) {
//            liveData = new MutableLiveData<>();
//            liveData.setValue(new ArrayList<Travel>());
//        }
//
//        TravelMemAPIDAO.apiService.getTravels(uid, token).enqueue(new Callback<HashMap<String, Travel>>() {
//            @Override
//            public void onResponse(Call<HashMap<String, Travel>> call, Response<HashMap<String, Travel>> response) {
//                if (response.isSuccessful()) {
//                    HashMap<String, Travel> body = response.body();
//
//                    if (body == null) {
//                        return;
//                    }
//
//                    ArrayList<Travel> travels = new ArrayList<>();
//                    for (Map.Entry<String, Travel> entry : body.entrySet()) {
//                        Travel travel = entry.getValue();
//                        travel.setId(entry.getKey());
//                        travels.add(travel);
//                    }
//
//                    ArrayList<Travel> updatedTravels = TravelMemLocalCacheDAO.updateTravels(context, uid, travels);
//
//                    liveData.setValue(updatedTravels);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<HashMap<String, Travel>> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//
//        return liveData;
//    }
//
//    public void reloadData(String uid, String token) {
//        getData(uid, token);
//    }
//
//    public void addTravel(final String uid, String token, final Travel travel) {
//        TravelMemAPIDAO.apiService.addTravel(
//                uid,
//                token,
//                travel
//        ).enqueue(new Callback<FirebasePOSTResponse>() {
//                @Override
//                public void onResponse(Call<FirebasePOSTResponse> call, Response<FirebasePOSTResponse> response) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        travel.setId(response.body().name);
//
//                        // update travel local cache
//                        TravelMemLocalCacheDAO.addTravel(context, uid, travel);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<FirebasePOSTResponse> call, Throwable t) {
//
//                }
//            });
//
//            //can't update cache because there's still no id
//    }
//
//    public void removeTravel(String uid, String token, String id) {
//        TravelMemAPIDAO.apiService.deleteTravel(
//                uid,
//                id,
//                token
//        ).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
//
//        TravelMemLocalCacheDAO.deleteTravel(context, uid, id);
//    }
//
//    public void updateTravel(String uid, String token, Travel travel) {
//        TravelMemAPIDAO.apiService.updateTravel(
//                uid,
//                travel.getId(),
//                token,
//                travel
//        ).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
//
//        // update travel local cache
//        TravelMemLocalCacheDAO.updateTravel(context, uid, travel);
//    }

}
