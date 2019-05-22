package com.isra.israel.travelmem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.google.android.libraries.places.api.Places;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.adapter.TravelsAdapter;
import com.isra.israel.travelmem.api.TravelMemAPIDAO;
import com.isra.israel.travelmem.dao.FirebaseSessionSPDAO;
import com.isra.israel.travelmem.dao.TravelMemLocalCacheDAO;
import com.isra.israel.travelmem.fragment.TravelFragment;
import com.isra.israel.travelmem.model.FirebasePOSTResponse;
import com.isra.israel.travelmem.model.Travel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelsActivity extends AppCompatActivity {

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

                // open travel fragment for viewing/editing
                TravelFragment travelFragment = TravelFragment.newInstance(travel, false);

                // on edit
                travelFragment.setOnTravelEditListener(new TravelFragment.OnTravelEditListener() {
                    @Override
                    public void onTravelEdit(Travel travel) {
                        travel.setLastUpdatedTime(System.currentTimeMillis());
                        travelsAdapter.setTravelAt(travel, openedTravelPosition);

                        // update travel at TravelMemAPI
                        Call<ResponseBody> updateTravelCall = TravelMemAPIDAO.apiService.updateTravel(
                                FirebaseSessionSPDAO.getUid(TravelsActivity.this),
                                travel.getId(),
                                FirebaseSessionSPDAO.getIdToken(TravelsActivity.this),
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

                        String uid = FirebaseSessionSPDAO.getUid(TravelsActivity.this);

                        // update travel local cache
                        TravelMemLocalCacheDAO.updateTravel(TravelsActivity.this, uid, travel);
                    }
                });

                // on delete
                travelFragment.setOnTravelDeleteListener(new TravelFragment.OnTravelDeleteListener() {
                    @Override
                    public void onTravelDelete(String id) {
                        travelsAdapter.removeTravel(id);

                        Call<ResponseBody> deleteTravelCall = TravelMemAPIDAO.apiService.deleteTravel(
                                FirebaseSessionSPDAO.getUid(TravelsActivity.this),
                                id,
                                FirebaseSessionSPDAO.getIdToken(TravelsActivity.this)
                        );

                        // NOTE: if this fails. SyncService will take care of it
                        deleteTravelCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });

                        String uid = FirebaseSessionSPDAO.getUid(TravelsActivity.this);

                        TravelMemLocalCacheDAO.deleteTravel(TravelsActivity.this, uid, id);
                    }
                });

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.a_travels_fl_root, travelFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        // add travel button
        findViewById(R.id.a_travels_b_add_travel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open travel fragment for creation
                TravelFragment travelFragment = TravelFragment.newInstance(new Travel(), true);

                // on create
                travelFragment.setOnTravelCreateListener(new TravelFragment.OnTravelCreateListener() {
                    @Override
                    public void onTravelCreate(final Travel travel) {
                        travel.setCreationTime(System.currentTimeMillis());
                        travelsAdapter.addTravel(travel);

                        Call<FirebasePOSTResponse> addTravelCall = TravelMemAPIDAO.apiService.addTravel(
                                FirebaseSessionSPDAO.getUid(TravelsActivity.this),
                                FirebaseSessionSPDAO.getIdToken(TravelsActivity.this),
                                travel
                        );

                        // NOTE: if this fails. SyncService will take care of it
                        addTravelCall.enqueue(new Callback<FirebasePOSTResponse>() {
                            @Override
                            public void onResponse(Call<FirebasePOSTResponse> call, Response<FirebasePOSTResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    travel.setId(response.body().name);

                                    String uid = FirebaseSessionSPDAO.getUid(TravelsActivity.this);

                                    // update travel local cache
                                    TravelMemLocalCacheDAO.addTravel(TravelsActivity.this, uid, travel);
                                }
                            }

                            @Override
                            public void onFailure(Call<FirebasePOSTResponse> call, Throwable t) {

                            }
                        });

                        // can't update cache because there's still no id
                    }
                });

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.a_travels_fl_root, travelFragment)
                        .addToBackStack(null)
                        .commit();
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

            String uid = FirebaseSessionSPDAO.getUid(this);

            ArrayList<Travel> updatedTravels = TravelMemLocalCacheDAO.updateTravels(this, uid, travels);

            travelsAdapter.setTravels(updatedTravels);
        }
    }

}
