package com.isra.israel.travelmem.repository;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.isra.israel.travelmem.api.TravelMemAPIDAO;
import com.isra.israel.travelmem.dao.FirebaseSessionSPDAO;
import com.isra.israel.travelmem.dao.TravelMemLocalCacheDAO;
import com.isra.israel.travelmem.model.Travel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelsRepository {

    private Context context;
    private MutableLiveData<ArrayList<Travel>> liveData;

    public TravelsRepository(Context context) {
        this.context = context;
    }

    public MutableLiveData<ArrayList<Travel>> getData(final String uid, String token) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        }

        TravelMemAPIDAO.apiService.getTravels(uid, token).enqueue(new Callback<HashMap<String, Travel>>() {
            @Override
            public void onResponse(Call<HashMap<String, Travel>> call, Response<HashMap<String, Travel>> response) {
                if (response.isSuccessful()) {
                    HashMap<String, Travel> body = response.body();

                    ArrayList<Travel> travels = new ArrayList<>();
                    for (Map.Entry<String, Travel> entry : body.entrySet()) {
                        Travel travel = entry.getValue();
                        travel.setId(entry.getKey());
                        travels.add(travel);
                    }

                    // NOTE: this is async. Lock user from loading data multiple times at the same time
                    ArrayList<Travel> updatedTravels = TravelMemLocalCacheDAO.updateTravels(context, uid, travels);

                    liveData.postValue(updatedTravels);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Travel>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return liveData;
    }

    public void reloadData(String uid, String token) {
        getData(uid, token);
    }

}
