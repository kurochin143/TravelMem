package com.isra.israel.travelmem.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.internal.ee;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.api.GoogleDirectionsAPIDAO;
import com.isra.israel.travelmem.model.directions.GoogleDirectionsResult;
import com.isra.israel.travelmem.model.directions.Leg;
import com.isra.israel.travelmem.model.directions.Point;
import com.isra.israel.travelmem.model.directions.Route;
import com.isra.israel.travelmem.model.directions.Step;
import com.isra.israel.travelmem.static_helpers.BitmapStaticHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO MEDIUM progress bar
public class RouteEditorFragment extends Fragment {
    private static final String ARG_ROUTE = "route";
    private static final String ARG_ORIGIN = "origin";
    private static final String ARG_DESTINATION = "destination";
    public static final int RC_SEARCH_ORIGIN = 1;
    public static final int RC_SEARCH_DESTINATION = 2;

    private ArrayList<Route> routes;
    private Route route;
    private GoogleMap googleMap;
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private Marker originMarker;
    private Marker destinationMarker;
    private Point origin;
    private Point destination;
    private Call<GoogleDirectionsResult> getDirectionCall;
    private static final List<Place.Field> fields = new ArrayList<>();
    static {
        fields.add(Place.Field.ADDRESS);
        fields.add(Place.Field.LAT_LNG);
    }

    private OnRouteEditListener onRouteEditListener;

    public RouteEditorFragment() {
        // Required empty public constructor
    }

    public static RouteEditorFragment newInstance(Route route, Point origin, Point destination) {
        RouteEditorFragment fragment = new RouteEditorFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ROUTE, route);
        args.putParcelable(ARG_ORIGIN, origin);
        args.putParcelable(ARG_DESTINATION, destination);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            route = getArguments().getParcelable(ARG_ROUTE);
            origin = getArguments().getParcelable(ARG_ORIGIN);
            if (origin == null) {
                origin = new Point();
            }
            destination = getArguments().getParcelable(ARG_DESTINATION);
            if (destination == null) {
                destination = new Point();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_editor, container, false);

        // google map
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.f_route_editor_f_google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                RouteEditorFragment.this.googleMap = googleMap;

                if (route != null) {
                    drawRoute(route);
                }

            }
        });

        // origin google places
        AutocompleteSupportFragment originAutocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.f_route_editor_f_autocomplete_origin);
        originAutocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG));
        originAutocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                origin.setName(place.getAddress());
                origin.setLatLng(place.getLatLng());

                requestDirection();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        // destination google places
        AutocompleteSupportFragment destinationAutocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.f_route_editor_f_autocomplete_destination);
        destinationAutocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG));
        destinationAutocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destination.setName(place.getAddress());
                destination.setLatLng(place.getLatLng());

                requestDirection();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        // origin search
        view.findViewById(R.id.f_route_editor_ib_search_origin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = (new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)).a(ee.a).build(getActivity());
                startActivityForResult(intent, RC_SEARCH_ORIGIN);
            }
        });

        // destination search
        view.findViewById(R.id.f_route_editor_ib_search_destination).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = (new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)).a(ee.a).build(getActivity());
                startActivityForResult(intent, RC_SEARCH_DESTINATION);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == -1) {
            if (requestCode == RC_SEARCH_ORIGIN) {
                Place place = Autocomplete.getPlaceFromIntent(intent);
                origin.setName(place.getAddress());
                origin.setLatLng(place.getLatLng());

                requestDirection();
            } else if (requestCode == RC_SEARCH_DESTINATION) {
                Place place = Autocomplete.getPlaceFromIntent(intent);

                destination.setName(place.getAddress());
                destination.setLatLng(place.getLatLng());

                requestDirection();
            }
        }

    }

    private void requestDirection() {
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

        // clear origin marker
        if (originMarker != null) {
            originMarker.remove();
        }

        // clear destination marker
        if (destinationMarker != null) {
            destinationMarker.remove();
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

    private void drawRoute(@NonNull Route route) {

        // move/zoom map
        if (route.getBounds() != null && route.getBounds().getSouthWest() != null && route.getBounds().getNorthEast() != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                    new LatLngBounds(route.getBounds().getSouthWest(), route.getBounds().getNorthEast()), 0));
        }

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

        if (route.getLegs().size() != 0) {
            Leg leg0 = route.getLegs().get(0);
            LatLng origin = leg0.getStartLocation();
            Leg legLast = route.getLegs().get(route.getLegs().size() - 1);
            LatLng destination = legLast.getEndLocation();

            Bitmap originMarkerBitmap = BitmapStaticHelper.getBitmap((VectorDrawable) getContext().getResources().getDrawable(R.drawable.ic_place_32dp));

            originMarker = googleMap.addMarker(new MarkerOptions()
                    .position(origin)
                    .icon(BitmapDescriptorFactory.fromBitmap(originMarkerBitmap))
            );

            Bitmap destinationMarkerBitmap = BitmapStaticHelper.getBitmap((VectorDrawable) getContext().getResources().getDrawable(R.drawable.ic_flag_32dp));

            destinationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .icon(BitmapDescriptorFactory.fromBitmap(destinationMarkerBitmap))
            );
        }
    }

    public void setOnRouteEditListener(OnRouteEditListener onRouteEditedListener) {
        this.onRouteEditListener = onRouteEditedListener;
    }

    public interface OnRouteEditListener {
        void onRouteEdit(Route route, Point origin, Point destination);
    }
}
