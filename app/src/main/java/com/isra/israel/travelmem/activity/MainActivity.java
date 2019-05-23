package com.isra.israel.travelmem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.dao.TravelMemLocalCacheDAO;
import com.isra.israel.travelmem.model.Travel;

import java.util.ArrayList;

// TODO LOW a map where the user can just create Route and save it as a Travel
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Travel travel = new Travel();
//        travel.setId("testTravelIdLocal");
//        travel.setName("test travel name");
//        TravelMemLocalCacheDAO.addTravel(this, travel);
//
//        ArrayList<Travel> travels = TravelMemLocalCacheDAO.getTravels(this);
//
//        travel.setName("test travel name update");
//        TravelMemLocalCacheDAO.updateTravel(this, travel);
//
//        travels = TravelMemLocalCacheDAO.getTravels(this);
//
//        TravelMemLocalCacheDAO.deleteTravel(this, travel.getId());
//
//        travels = TravelMemLocalCacheDAO.getTravels(this);



        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
