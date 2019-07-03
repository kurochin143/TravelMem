package com.isra.israel.travelmem.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.model.directions.Point;
import com.isra.israel.travelmem.model.directions.Route;

import icepick.Icepick;
import icepick.State;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTravelFragment extends Fragment implements RouteEditorFragment.OnRouteEditListener {

    @State
    Travel travel;

    private OnTravelAddListener onTravelAddListener;

    public AddTravelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        travel = new Travel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_travel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        // name
        EditText nameEditText = view.findViewById(R.id.f_add_travel_et_name);
        nameEditText.setText(travel.getDescription());
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                travel.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // route
        view.findViewById(R.id.f_add_travel_b_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteEditorFragment routeEditorFragment = RouteEditorFragment.newInstance(travel.getRoute(), travel.getOrigin(), travel.getDestination());

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.f_add_travel_fl_root, routeEditorFragment)
                        .commit();
            }
        });

        // notify
        Switch notifySwitch = view.findViewById(R.id.f_add_travel_s_notify);
        notifySwitch.setChecked(travel.shouldNotify() == 1);
        notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                travel.setShouldNotify(isChecked ? 1 : 0);
            }
        });

        // description
        final EditText descriptionEditText = view.findViewById(R.id.f_add_travel_et_description);
        descriptionEditText.setText(travel.getDescription());
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                travel.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // save
        view.findViewById(R.id.f_add_travel_b_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTravelAddListener.onAddTravel(travel);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // cancel
        view.findViewById(R.id.f_add_travel_b_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnTravelAddListener) {
            onTravelAddListener = (OnTravelAddListener) context;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onTravelAddListener = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);

    }

    @Override
    public void onRouteEdit(Route route, Point origin, Point destination) {
        travel.setRoute(route);
        travel.setOrigin(origin);
        travel.setDestination(destination);
    }

    public interface OnTravelAddListener {
        void onAddTravel(Travel travel);
    }
}
