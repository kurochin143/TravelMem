package com.isra.israel.travelmem.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.libraries.places.api.Places;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.adapter.TravelsAdapter;
import com.isra.israel.travelmem.dao.FirebaseSessionSPDAO;
import com.isra.israel.travelmem.fragment.TravelFragment;
import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.view_model.TravelsViewModel;

import java.util.ArrayList;

public class TravelsActivity extends AppCompatActivity {

    private String uid;
    private String token;
    private TravelsAdapter travelsAdapter;
    private TravelsViewModel travelsViewModel;
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

        travelsViewModel = ViewModelProviders.of(this).get(TravelsViewModel.class);
        uid = FirebaseSessionSPDAO.getUid(this);
        token = FirebaseSessionSPDAO.getIdToken(this);

        // travels observer
        travelsViewModel.getTravelsLiveData(this, uid, token).observe(this, new Observer<ArrayList<Travel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Travel> travels) {
                if (travels == null) {
                    return;
                }

                travelsAdapter.setTravels(travels);
            }
        });

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
                        travelsViewModel.updateTravel(uid, token, travel);
                        travelsAdapter.setTravelAt(travel, openedTravelPosition);
                    }
                });

                // on delete
                travelFragment.setOnTravelDeleteListener(new TravelFragment.OnTravelDeleteListener() {
                    @Override
                    public void onTravelDelete(String id) {
                        travelsViewModel.removeTravel(uid, token, id);
                        travelsAdapter.removeTravel(id);
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

                        travelsViewModel.addTravel(uid, token, travel);
                        travelsAdapter.addTravel(travel);
                    }
                });

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.a_travels_fl_root, travelFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
