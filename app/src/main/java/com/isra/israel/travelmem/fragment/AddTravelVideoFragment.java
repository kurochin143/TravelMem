package com.isra.israel.travelmem.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.TravelVideo;
import com.isra.israel.travelmem.model.directions.Point;

public class AddTravelVideoFragment extends Fragment {
    private static final String ARG_LOCATION = "location";

    private Point location;

    private OnTravelVideoAddListener onTravelVideoAddListener;

    public AddTravelVideoFragment() {
        // Required empty public constructor
    }

    public static AddTravelVideoFragment newInstance(Point location) {
        AddTravelVideoFragment fragment = new AddTravelVideoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getParcelable(ARG_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_travel_video, container, false);



        return view;
    }

    public void setOnTravelVideoAddListener(OnTravelVideoAddListener l) {
        onTravelVideoAddListener = l;
    }

    public interface OnTravelVideoAddListener {
        void onTravelVideoAdd(TravelVideo travelVideo);
    }
}
