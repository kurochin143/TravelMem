package com.isra.israel.travelmem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.libraries.places.api.Places;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.adapter.TravelsAdapter;
import com.isra.israel.travelmem.api.TravelMemAPIDAO;
import com.isra.israel.travelmem.dao.FirebaseSessionSPDAO;
import com.isra.israel.travelmem.fragment.TravelFragment;
import com.isra.israel.travelmem.model.Travel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelsActivity extends AppCompatActivity implements TravelFragment.OnTravelEditedListener {

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

        travelsAdapter.setOnTravelClickedListener(new TravelsAdapter.OnTravelClickedListener() {
            @Override
            public void onTravelClicked(Travel travel, int position) {
                openedTravelPosition = position;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.a_travels_c_root, TravelFragment.newInstance(travel))
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

            travelsAdapter.setTravels(travels);
        }
    }

    @Override
    public void onTravelEdited(Travel travel) {
        travelsAdapter.setTravelAt(travel, openedTravelPosition);
    }
}
