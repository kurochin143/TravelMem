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
import com.isra.israel.travelmem.model.directions.Point;
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
    private Point origin = new Point();
    private Point destination = new Point();
    private Call<GoogleDirectionsResult> getDirectionCall;

    private OnRouteEditListener onRouteEditListener;

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

                drawRoute(route);
            }
        });

        AutocompleteSupportFragment originAutocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.f_route_editor_f_autocomplete_origin);
        originAutocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG));
        originAutocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                origin.setName(place.getAddress());
                origin.setLatLng(place.getLatLng());

                requestDirectionByName();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        AutocompleteSupportFragment destinationAutocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.f_route_editor_f_autocomplete_destination);
        destinationAutocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG));
        destinationAutocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destination.setName(place.getAddress());
                destination.setLatLng(place.getLatLng());

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

        if (origin.getLatLng() == null || destination.getLatLng() == null) {
            return;
        }

        getDirectionCall = GoogleDirectionsAPIDAO.apiService.getDirection(
                getString(R.string.google_maps_api_key),
                origin.getLatLng().latitude + "," + origin.getLatLng().longitude,
                destination.getLatLng().latitude + "," + destination.getLatLng().longitude
        );
        getDirectionCall.enqueue(new Callback<GoogleDirectionsResult>() {
            @Override
            public void onResponse(Call<GoogleDirectionsResult> call, Response<GoogleDirectionsResult> response) {
                onGetDirectionCallFinished(response);
            }

            @Override
            public void onFailure(Call<GoogleDirectionsResult> call, Throwable t) {
                onGetDirectionCallFinished(null);

            }
        });
    }

    private void onGetDirectionCallFinished(Response<GoogleDirectionsResult> response) {
        if (getDirectionCall.isCanceled()) {
            return;
        }

        getDirectionCall = null;

        if (response != null && response.isSuccessful()) {
            GoogleDirectionsResult body = response.body();
            if (body != null) {
                drawRoutes(body.getRoutes());
            }
        }
    }

    private void drawRoutes(ArrayList<Route> routes) {
        // clear previous polylines
        for (Polyline polyline : polylines) {
            polyline.remove();
        }

        if (routes.size() == 0) {
            return;
        }

        this.route = routes.get(0);

        // TODO MEDIUM how to query for multiple routes
        // TODO MEDIUM route selector
        ArrayList<PolylineOptions> polylineOptionsList = new ArrayList<>();

        for (Route route : routes) {
            drawRoute(route);
        }

        onRouteEditListener.onRouteEdit(route, origin, destination);
    }

    private void drawRoute(Route route) {

        // TODO HIGH draw circle
        // TODO CRITICAL waypoints
        ArrayList<CircleOptions> circleOptionsList = new ArrayList<>();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        for (Leg leg : route.getLegs()) {
            for (Step step : leg.getSteps()) {
                List<LatLng> polylinePoints = PolyUtil.decode(step.getPolyline().getPoints());
                for (LatLng polylinePoint : polylinePoints) {
                    polylineOptions.add(polylinePoint);
                }
            }
        }

        for (CircleOptions circleOptions : circleOptionsList) {
            googleMap.addCircle(circleOptions);
        }

        polylines.add(googleMap.addPolyline(polylineOptions));
    }

    public void setOnRouteEditListener(OnRouteEditListener onRouteEditedListener) {
        this.onRouteEditListener = onRouteEditedListener;
    }

    public interface OnRouteEditListener {
        void onRouteEdit(Route route, Point origin, Point destination);
    }
}
