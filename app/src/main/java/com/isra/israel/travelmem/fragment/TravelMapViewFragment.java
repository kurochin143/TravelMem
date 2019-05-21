package com.isra.israel.travelmem.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.model.directions.Leg;
import com.isra.israel.travelmem.model.directions.Route;
import com.isra.israel.travelmem.model.directions.Step;

import java.util.ArrayList;
import java.util.List;

public class TravelMapViewFragment extends Fragment {

    private static final String ARG_TRAVEL = "travel";

    private Travel travel;

    private View fragmentView;
    private GoogleMap googleMap;

    public TravelMapViewFragment() {
        // Required empty public constructor
    }

    public static TravelMapViewFragment newInstance(Travel travel) {
        TravelMapViewFragment fragment = new TravelMapViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRAVEL, travel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            travel = getArguments().getParcelable(ARG_TRAVEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_travel_map_view, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_travels_map_google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                TravelMapViewFragment.this.googleMap = googleMap;

                Route route = travel.getRoute();

                ArrayList<PolylineOptions> polylineOptionsList = new ArrayList<>();
                ArrayList<CircleOptions> circleOptionsList = new ArrayList<>();

                for (Leg leg : route.getLegs()) {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.BLUE);
                    polylineOptionsList.add(polylineOptions);

                    for (Step step : leg.getSteps()) {
                        List<LatLng> polylinePoints = PolyUtil.decode(step.getPolyline().getPoints());
                        for (LatLng polylinePoint : polylinePoints) {
                            polylineOptions.add(polylinePoint);
                        }
                    }
                }

                for (PolylineOptions polylineOptions : polylineOptionsList) {
                    googleMap.addPolyline(polylineOptions);
                }

                for (CircleOptions circleOptions : circleOptionsList) {
                    googleMap.addCircle(circleOptions);
                }
            }
        });

        return fragmentView;
    }

}
