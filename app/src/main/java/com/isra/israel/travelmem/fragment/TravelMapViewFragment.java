package com.isra.israel.travelmem.fragment;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.model.TravelImage;
import com.isra.israel.travelmem.model.TravelVideo;
import com.isra.israel.travelmem.model.directions.Leg;
import com.isra.israel.travelmem.model.directions.Route;
import com.isra.israel.travelmem.model.directions.Step;
import com.isra.israel.travelmem.static_helpers.BitmapStaticHelper;
import com.isra.israel.travelmem.static_helpers.VideoStaticHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TravelMapViewFragment extends Fragment {

    private static final String ARG_TRAVEL = "travel";

    private Travel travel;

    private GoogleMap googleMap;

    private OnTravelEditListener onTravelEditListener;

    private ArrayList<Marker> markers = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_travel_map_view, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_travels_map_google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                TravelMapViewFragment.this.googleMap = googleMap;

                Route route = travel.getRoute();

                // TODO MEDIUM do this async

                // draw route
                if (route != null) {
                    // TODO MEDIUM lock and move/zoom camera to route
//                    googleMap.setLatLngBoundsForCameraTarget(LatLngBounds.builder()
//                            .include(route.getBounds().getNorthEast())
//                            .include(route.getBounds().getSouthWest())
//                            .build());

                    if (route.getBounds() != null && route.getBounds().getSouthWest() != null && route.getBounds().getNorthEast() != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                                new LatLngBounds(route.getBounds().getSouthWest(), route.getBounds().getNorthEast()), 0));
                    }

                    ArrayList<PolylineOptions> polylineOptionsList = new ArrayList<>();
                    ArrayList<CircleOptions> circleOptionsList = new ArrayList<>();

                    if (route.getLegs() != null) {
                        if (route.getLegs().size() != 0) {
                            Leg leg0 = route.getLegs().get(0);
                            LatLng origin = leg0.getStartLocation();
                            Leg legLast = route.getLegs().get(route.getLegs().size() - 1);
                            LatLng destination = legLast.getEndLocation();

                            Bitmap originMarkerBitmap = BitmapStaticHelper.getBitmap((VectorDrawable) getContext().getResources().getDrawable(R.drawable.ic_place_32dp));

                            googleMap.addMarker(new MarkerOptions()
                                    .position(origin)
                                    .icon(BitmapDescriptorFactory.fromBitmap(originMarkerBitmap))
                            );

                            Bitmap destinationMarkerBitmap = BitmapStaticHelper.getBitmap((VectorDrawable) getContext().getResources().getDrawable(R.drawable.ic_flag_32dp));

                            googleMap.addMarker(new MarkerOptions()
                                    .position(destination)
                                    .icon(BitmapDescriptorFactory.fromBitmap(destinationMarkerBitmap))
                            );
                        }

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
                    }

                    for (PolylineOptions polylineOptions : polylineOptionsList) {
                        googleMap.addPolyline(polylineOptions);
                    }

                    for (CircleOptions circleOptions : circleOptionsList) {
                        googleMap.addCircle(circleOptions);
                    }
                }

                // TODO HIGH remove non-existent file

                // add travel image marker
                if (travel.getImages() != null) {
                    for (TravelImage travelImage : travel.getImages()) {
                        if (travelImage.getUriStr() != null && travelImage.getLocation() != null && travelImage.getLocation().getLatLng() != null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), travelImage.getUri());
                                googleMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                        .position(travelImage.getLocation().getLatLng())
                                );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                // add travel video marker
                if (travel.getVideos() != null) {
                    for (final TravelVideo travelVideo : travel.getVideos()) {
                        if (travelVideo.getUriStr() != null && travelVideo.getLocation() != null && travelVideo.getLocation().getLatLng() != null) {
                            Bitmap bitmap = VideoStaticHelper.getFrameAtHalf(getContext(), travelVideo.getUri());
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                            final Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                                    .position(travelVideo.getLocation().getLatLng())
                            );

                            marker.setTag(travelVideo);

                            markers.add(marker);
                        }
                    }
                }

                // add origin end marker


                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        for (int i = 0; i < markers.size(); ++i) {
                            final Marker myMarker = markers.get(i);
                            if (myMarker.getId().equals(marker.getId())) {
                                TravelVideo markerTravelVideo = (TravelVideo) myMarker.getTag();

                                // find the travel video
                                for (int j = 0; j < travel.getVideos().size(); ++j) {
                                    TravelVideo arrTravelVideo = travel.getVideos().get(j);
                                    if (arrTravelVideo == markerTravelVideo) {
                                        final int openedTravelVideoPosition = j;

                                        // open travel video fragment
                                        TravelVideoFragment travelVideoFragment = TravelVideoFragment.newInstance(markerTravelVideo);
                                        travelVideoFragment.setOnTravelVideoEditListener(new TravelVideoFragment.OnTravelVideoEditListener() {
                                            @Override
                                            public void onTravelVideoEdit(TravelVideo travelVideo) {
                                                if (travelVideo == null) { // removed
                                                    myMarker.remove();

                                                    travel.getVideos().remove(openedTravelVideoPosition);
                                                } else { // edited
                                                    travel.getVideos().set(openedTravelVideoPosition, travelVideo);
                                                }

                                                onTravelEditListener.onTravelEdit(travel);
                                            }
                                        });
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .add(R.id.f_travel_map_view_c_root, travelVideoFragment)
                                                .addToBackStack(null)
                                                .commit();

                                        break;
                                    }
                                }

                                break;
                            }
                        }

                        return true;
                    }
                });
            }
        });

        return view;
    }

    public void setOnTravelEditListener(OnTravelEditListener l) {
        onTravelEditListener = l;
    }

    public interface OnTravelEditListener {
        void onTravelEdit(Travel travel);
    }

}
