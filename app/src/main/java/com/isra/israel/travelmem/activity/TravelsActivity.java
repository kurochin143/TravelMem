package com.isra.israel.travelmem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.libraries.places.api.Places;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.adapter.TravelsAdapter;
import com.isra.israel.travelmem.api.TravelMemAPIDAO;
import com.isra.israel.travelmem.dao.FirebaseSessionSPDAO;
import com.isra.israel.travelmem.dao.TravelMemLocalCacheDAO;
import com.isra.israel.travelmem.fragment.TravelFragment;
import com.isra.israel.travelmem.model.Travel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelsActivity extends AppCompatActivity implements TravelFragment.OnTravelEditListener {

    private TravelsAdapter travelsAdapter;
    private Call<HashMap<String, Travel>> getTravelsCall;
    private int openedTravelPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travels);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));

        // setup recycler view
        RecyclerView recyclerView = findViewById(R.id.a_travels_r_travels);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        travelsAdapter = new TravelsAdapter();
        recyclerView.setAdapter(travelsAdapter);

        // adapter
        travelsAdapter.setOnTravelClickListener(new TravelsAdapter.OnTravelClickListener() {
            @Override
            public void onTravelClick(Travel travel, int position) {
                openedTravelPosition = position;

                // open travel fragment
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.a_travels_fl_root, TravelFragment.newInstance(travel))
                        .addToBackStack(null)
                        .commit();
            }
        });

        // add travel button
        findViewById(R.id.a_travels_b_add_travel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        requestTravels();

    }

    private void requestTravels() {
        if (getTravelsCall != null) {
            return;
        }

        String uid = FirebaseSessionSPDAO.getUid(this);
        if (uid == null) {
            return;
        }

        String token = FirebaseSessionSPDAO.getIdToken(this);
        if (token == null) {
            return;
        }

        getTravelsCall = TravelMemAPIDAO.apiService.getTravels(uid, token);
        getTravelsCall.enqueue(new Callback<HashMap<String, Travel>>() {
            @Override
            public void onResponse(Call<HashMap<String, Travel>> call, Response<HashMap<String, Travel>> response) {
                onGetTravelsCallFinished(response);
            }

            @Override
            public void onFailure(Call<HashMap<String, Travel>> call, Throwable t) {
                t.printStackTrace();
                onGetTravelsCallFinished(null);
            }
        });
    }

    private void onGetTravelsCallFinished(Response<HashMap<String, Travel>> response) {
        if (getTravelsCall.isCanceled()) {
            return;
        }

        getTravelsCall = null;

        if (response != null && response.isSuccessful()) {
            HashMap<String, Travel> body = response.body();

            ArrayList<Travel> travels = new ArrayList<>();
            for (Map.Entry<String, Travel> entry : body.entrySet()) {
                Travel travel = entry.getValue();
                travel.setId(entry.getKey());
                travels.add(travel);
            }

            ArrayList<Travel> updatedTravels = TravelMemLocalCacheDAO.updateTravels(this, travels);

            travelsAdapter.setTravels(updatedTravels);
        }
    }

    @Override
    public void onTravelEdit(Travel travel) {
        travel.setLastUpdatedTime(System.currentTimeMillis());
        travelsAdapter.setTravelAt(travel, openedTravelPosition);

        // update travel at TravelMemAPI
        final Call<ResponseBody> updateTravelCall = TravelMemAPIDAO.apiService.updateTravel(
                FirebaseSessionSPDAO.getUid(this),
                travel.getId(),
                FirebaseSessionSPDAO.getIdToken(this),
                travel
        );

        // NOTE: if this fails. SyncService will take care of it
        updateTravelCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        // update travel locally
        TravelMemLocalCacheDAO.updateTravel(this, travel);

    }
}
