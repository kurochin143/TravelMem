package com.isra.israel.travelmem.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.internal.ee;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.android.PolyUtil;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.api.GoogleDirectionsAPIDAO;
import com.isra.israel.travelmem.model.directions.GoogleDirectionsResult;
import com.isra.israel.travelmem.model.directions.Leg;
import com.isra.israel.travelmem.model.directions.Point;
import com.isra.israel.travelmem.model.directions.Route;
import com.isra.israel.travelmem.model.directions.Step;
import com.isra.israel.travelmem.static_helpers.BitmapStaticHelper;

import java.util.ArrayList;
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
    private ArrayList<String> waypoints = new ArrayList<>();
    private ArrayList<Marker> waypointMarkers = new ArrayList<>();
    private Marker selectedWaypointMarker;
    private ImageButton deleteWaypointButton;
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
                    if (route.getLegs().size() > 1) {
                        for (Leg leg : route.getLegs()) {
                            LatLng endLocation = leg.getEndLocation();
                            waypoints.add(endLocation.latitude + "," + endLocation.longitude);
                        }
                        waypoints.remove(waypoints.size() - 1); // remove last one
                    }

                    drawRoute(route);
                }

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

        deleteWaypointButton = view.findViewById(R.id.f_route_editor_ib_delete_waypoint);
        deleteWaypointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int)selectedWaypointMarker.getTag();
                waypoints.remove(index);

                requestDirection();
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

                waypoints.clear(); // clear waypoints

                requestDirection();
            } else if (requestCode == RC_SEARCH_DESTINATION) {
                Place place = Autocomplete.getPlaceFromIntent(intent);

                destination.setName(place.getAddress());
                destination.setLatLng(place.getLatLng());

                waypoints.clear(); // clear waypoints

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

        String waypointsStr = null;
        if (waypoints.size() != 0) {
            StringBuilder waypointsStrB = new StringBuilder();
            for (String waypoint : waypoints) {
                waypointsStrB.append(waypoint);
                waypointsStrB.append("|");
            }
            waypointsStrB.setLength(waypointsStrB.length() - 1);
            waypointsStr = waypointsStrB.toString();
        }

        getDirectionCall = GoogleDirectionsAPIDAO.apiService.getDirection(
                getString(R.string.google_maps_api_key),
                origin.getLatLng().latitude + "," + origin.getLatLng().longitude,
                destination.getLatLng().latitude + "," + destination.getLatLng().longitude,
                waypointsStr
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

        // clear waypoint markers
        for (Marker marker : waypointMarkers) {
            marker.remove();
        }

        // forget old selected waypoint marker
        setSelectedWaypointMarker(null);

        if (routes.size() == 0) {
            return;
        }

        this.route = routes.get(0);

        // TODO LOW how to query for multiple routes
        // TODO LOW route selector
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
        ArrayList<MarkerOptions> waypointMarkerOptions = new ArrayList<>();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        for (int i = 0; i < route.getLegs().size(); ++i) {
            Leg leg = route.getLegs().get(i);
            waypointMarkerOptions.add(new MarkerOptions()
                    .position(leg.getEndLocation())
                    .title(i + ": " + leg.getEndAddress())
            );

            for (Step step : leg.getSteps()) {
                List<LatLng> polylinePoints = PolyUtil.decode(step.getPolyline().getPoints());
                for (LatLng polylinePoint : polylinePoints) {
                    polylineOptions.add(polylinePoint);
                }
            }
        }

        // do not add marker for the last one
        for (int i = 0; i < waypointMarkerOptions.size() - 1; ++i) {
            MarkerOptions markerOptions = waypointMarkerOptions.get(i);

            // create marker
            Marker waypointMarker = googleMap.addMarker(markerOptions);
            waypointMarker.setTag(i);
            waypointMarkers.add(waypointMarker);
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

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (Marker waypointMarker : waypointMarkers) {
                    if (waypointMarker.getId().equals(marker.getId())) {
                        setSelectedWaypointMarker(waypointMarker);
                        waypointMarker.showInfoWindow();
                        return true;
                    }
                }

                return false; // FIXME this should click map instead? if not then unselect waypoint here
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setSelectedWaypointMarker(null);
            }
        });

        // long click
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String latLngStr = latLng.latitude + "," + latLng.longitude;
                if (selectedWaypointMarker != null) {
                    // insert waypoint after the selected waypointMarker
                    int index = (int)selectedWaypointMarker.getTag();

                    waypoints.add(index + 1, latLngStr);
                } else {
                    // add waypoint after the last point(including origin) except destination
                    waypoints.add(latLngStr);
                }

                requestDirection();
            }
        });
    }

    private void setSelectedWaypointMarker(Marker marker) {
        if (marker == null) {
            deleteWaypointButton.setVisibility(View.GONE);
        } else {
            deleteWaypointButton.setVisibility(View.VISIBLE);
        }

        selectedWaypointMarker = marker;
    }

    public void setOnRouteEditListener(OnRouteEditListener onRouteEditedListener) {
        this.onRouteEditListener = onRouteEditedListener;
    }

    public interface OnRouteEditListener {
        void onRouteEdit(Route route, Point origin, Point destination);
    }
}
