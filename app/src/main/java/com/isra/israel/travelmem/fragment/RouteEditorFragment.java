package com.isra.israel.travelmem.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.PolyUtil;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.api.GoogleDirectionsAPIDAO;
import com.isra.israel.travelmem.model.directions.GoogleDirectionsResult;
import com.isra.israel.travelmem.model.directions.Leg;
import com.isra.israel.travelmem.model.directions.Route;
import com.isra.israel.travelmem.model.directions.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteEditorFragment extends Fragment {
    private static final String ARG_ROUTE = "route";

    private ArrayList<Route> routes;
    private Route route;
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private GoogleMap googleMap;
    private String origin;
    private String destination;
    private Call<GoogleDirectionsResult> getDirectionCall;

    private OnRouteEditedListener onRouteEditedListener;

    public RouteEditorFragment() {
        // Required empty public constructor
    }

    public static RouteEditorFragment newInstance(Route route) {
        RouteEditorFragment fragment = new RouteEditorFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ROUTE, route);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            route = getArguments().getParcelable(ARG_ROUTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_editor, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.f_route_editor_f_google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                RouteEditorFragment.this.googleMap = googleMap;
            }
        });

        AutocompleteSupportFragment originAutocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.f_route_editor_f_autocomplete_origin);
        originAutocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        originAutocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                origin = place.getName();

                requestDirectionByName();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        AutocompleteSupportFragment destinationAutocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.f_route_editor_f_autocomplete_destination);
        destinationAutocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        destinationAutocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destination = place.getName();

                requestDirectionByName();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        return view;
    }

    private void requestDirectionByName() {
        if (getDirectionCall != null){
            return;
        }

        if (origin == null || destination == null) {
            return;
        }

        getDirectionCall = GoogleDirectionsAPIDAO.apiService.getDirectionByName(getString(R.string.google_maps_api_key), origin, destination);
        getDirectionCall.enqueue(new Callback<GoogleDirectionsResult>() {
            @Override
            public void onResponse(Call<GoogleDirectionsResult> call, Response<GoogleDirectionsResult> response) {
                onGetDirectionByNameCallFinished(response);
            }

            @Override
            public void onFailure(Call<GoogleDirectionsResult> call, Throwable t) {
                onGetDirectionByNameCallFinished(null);

            }
        });
    }

    private void onGetDirectionByNameCallFinished(Response<GoogleDirectionsResult> response) {
        if (getDirectionCall.isCanceled()) {
            return;
        }

        getDirectionCall = null;

        if (response != null && response.isSuccessful()) {
            GoogleDirectionsResult body = response.body();
            if (body != null) {
                setRoutes(body.getRoutes());
            }
        }
    }

    private void setRoutes(ArrayList<Route> routes) {
        if (routes.size() == 0) {
            return;
        }

        this.route = routes.get(0);

        // clear previous polylines
        for (Polyline polyline : polylines) {
            polyline.remove();
        }

        // TODO MEDIUM how to query for multiple routes
        // TODO MEDIUM route picker
        ArrayList<PolylineOptions> polylineOptionsList = new ArrayList<>();
        ArrayList<CircleOptions> circleOptionsList = new ArrayList<>();

        for (Route route : routes) {

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.BLUE);
            polylineOptionsList.add(polylineOptions);
            for (Leg leg : route.getLegs()) {
                for (Step step : leg.getSteps()) {
                    List<LatLng> polylinePoints = PolyUtil.decode(step.getPolyline().getPoints());
                    for (LatLng polylinePoint : polylinePoints) {
                        polylineOptions.add(polylinePoint);
                    }
                }
            }
        }

        for (PolylineOptions polylineOptions : polylineOptionsList) {
            polylines.add(googleMap.addPolyline(polylineOptions));
        }

        for (CircleOptions circleOptions : circleOptionsList) {
            googleMap.addCircle(circleOptions);
        }

        onRouteEditedListener.onRouteEdited(route);
    }

    public void setOnRouteEditedListener(OnRouteEditedListener onRouteEditedListener) {
        this.onRouteEditedListener = onRouteEditedListener;
    }

    public interface OnRouteEditedListener {
        void onRouteEdited(Route route);
    }
}
