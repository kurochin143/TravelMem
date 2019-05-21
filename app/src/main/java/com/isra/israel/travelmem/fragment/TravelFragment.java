package com.isra.israel.travelmem.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.model.TravelImage;
import com.isra.israel.travelmem.model.directions.Route;

import java.util.ArrayList;

public class TravelFragment extends Fragment {

    private static final String ARG_TRAVEL = "travel";

    private Travel travel;

    private OnTravelEditListener onTravelEditListener;

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

        final TextView nameTextView = view.findViewById(R.id.f_travel_t_name);
        nameTextView.setText(travel.getName());
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTextView.setVisibility(View.GONE);
                final EditText nameEditText = new EditText(getContext());
                nameEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, nameTextView.getTextSize());
                nameEditText.setLayoutParams(nameTextView.getLayoutParams());
                nameEditText.setMaxLines(1);
                nameEditText.setImeActionLabel("enter", KeyEvent.KEYCODE_ENTER);
                nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        ((ViewGroup)nameEditText.getParent()).removeView(nameEditText);
                        nameTextView.setVisibility(View.VISIBLE);
                        nameTextView.setText(nameEditText.getText());
                        travel.setName(nameTextView.getText().toString());

                        onTravelEditListener.onTravelEdit(travel);

                        return true;
                    }
                });

                ViewGroup viewGroup = (ViewGroup)nameTextView.getParent();
                viewGroup.addView(nameEditText, viewGroup.indexOfChild(nameTextView));
            }
        });

        TextView startDateTextView = view.findViewById(R.id.f_travel_t_start_date);
        startDateTextView.setText(travel.getStartDate());

        TextView endDateTextView = view.findViewById(R.id.f_travel_t_end_date);
        endDateTextView.setText(travel.getEndDate());

        view.findViewById(R.id.f_travel_b_edit_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteEditorFragment routeEditorFragment = RouteEditorFragment.newInstance(travel.getRoute());
                routeEditorFragment.setOnRouteEditListener(new RouteEditorFragment.OnRouteEditListener() {
                    @Override
                    public void onRouteEdit(Route route) {
                        travel.setRoute(route);
                        onTravelEditListener.onTravelEdit(travel);
                    }
                });

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.f_travel_c_root, routeEditorFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        view.findViewById(R.id.f_travel_b_view_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelImagesFragment travelImagesFragment = TravelImagesFragment.newInstance(travel.getImages());
                travelImagesFragment.setOnTravelImagesEditedListener(new TravelImagesFragment.OnTravelImagesEditedListener() {
                    @Override
                    public void onTravelImagesEditedListener(ArrayList<TravelImage> travelImages) {
                        travel.setImages(travelImages);

                        onTravelEditListener.onTravelEdit(travel);
                    }
                });

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.f_travel_c_root, travelImagesFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTravelEditListener) {
            onTravelEditListener = (OnTravelEditListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTravelEditedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onTravelEditListener = null;
    }

    public interface OnTravelEditListener {
        void onTravelEdit(Travel travel);
    }
}
