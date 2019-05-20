package com.isra.israel.travelmem.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.model.directions.Route;

public class TravelFragment extends Fragment {

    private static final String ARG_TRAVEL = "travel";

    private Travel travel;

    private OnTravelEditedListener onTravelEditedListener;

    public TravelFragment() {
        // Required empty public constructor
    }

    public static TravelFragment newInstance(Travel travel) {
        TravelFragment fragment = new TravelFragment();
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
        View view = inflater.inflate(R.layout.fragment_travel, container, false);

        TextView nameTextView = view.findViewById(R.id.f_travel_t_name);
        nameTextView.setText(travel.getName());
        TextView startDateTextView = view.findViewById(R.id.f_travel_t_start_date);
        startDateTextView.setText(travel.getStartDate());
        TextView endDateTextView = view.findViewById(R.id.f_travel_t_end_date);
        endDateTextView.setText(travel.getEndDate());

        view.findViewById(R.id.f_travel_b_edit_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO NOW open route editor

                RouteEditorFragment routeEditorFragment = RouteEditorFragment.newInstance(travel.getRoute());
                routeEditorFragment.setOnRouteEditedListener(new RouteEditorFragment.OnRouteEditedListener() {
                    @Override
                    public void onRouteEdited(Route route) {
                        travel.setRoute(route);
                        onTravelEditedListener.onTravelEdited(travel);
                    }
                });

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.f_travel_c_root, routeEditorFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTravelEditedListener) {
            onTravelEditedListener = (OnTravelEditedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTravelEditedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onTravelEditedListener = null;
    }

    public interface OnTravelEditedListener {
        void onTravelEdited(Travel travel);
    }
}
