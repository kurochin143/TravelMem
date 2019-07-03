package com.isra.israel.travelmem.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.model.TravelImage;
import com.isra.israel.travelmem.model.TravelVideo;
import com.isra.israel.travelmem.model.directions.Point;
import com.isra.israel.travelmem.model.directions.Route;
import com.isra.israel.travelmem.static_helpers.KeyboardStaticHelper;

import java.util.ArrayList;

import icepick.Icepick;
import icepick.State;

public class TravelFragment extends Fragment
        implements RouteEditorFragment.OnRouteEditListener,
        TravelMapViewFragment.OnTravelEditListener,
        TravelVideosFragment.OnTravelVideosEditListener {

    private static final String ARG_TRAVEL = "travel";

    @State
    Travel travel;

    private TextView originTextView;
    private TextView destinationTextView;

    private OnTravelDeleteListener onTravelDeleteListener;
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
        return inflater.inflate(R.layout.fragment_travel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        // map
        ImageView viewOnMapButton = view.findViewById(R.id.f_travel_i_view_on_map);
        viewOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open travel map fragment
                TravelMapViewFragment travelMapViewFragment = TravelMapViewFragment.newInstance(travel);
                getChildFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left,0, 0, android.R.anim.slide_out_right)
                        .add(R.id.f_travel_fl_root, travelMapViewFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // delete button
        Button deleteButton = view.findViewById(R.id.f_travel_b_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTravelDeleteListener.onTravelDelete(travel.getId());

                popFromBackStack();
            }
        });

        // name
        final ViewSwitcher nameViewSwitcher = view.findViewById(R.id.f_travel_vs_name);
        final TextView nameTextView = view.findViewById(R.id.f_travel_t_name);
        nameTextView.setText(travel.getName());
        final EditText nameEditText = view.findViewById(R.id.f_travel_et_name);
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameViewSwitcher.showNext();
                nameEditText.setText(nameTextView.getText());
                nameEditText.requestFocus();
            }
        });
        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    nameViewSwitcher.showPrevious();
                    nameTextView.setText(nameEditText.getText().toString());

                    travel.setName(nameTextView.getText().toString());

                    onTravelEditListener.onTravelEdit(travel);
                }

                return true;
            }
        });

        // TODO ON HOLD date, may be unimplemented
        // TODO edit date with that timer spinning thing
        // start date
        TextView startDateTextView = view.findViewById(R.id.f_travel_t_start_date);
        startDateTextView.setText(travel.getStartDate());

        // end date
        TextView endDateTextView = view.findViewById(R.id.f_travel_t_end_date);
        endDateTextView.setText(travel.getEndDate());

        // origin
        originTextView = view.findViewById(R.id.f_travel_t_origin);
        if (travel.getOrigin() != null) {
            String originStr = "From: ";
            if (travel.getOrigin().getName() != null) {
                originStr += travel.getOrigin().getName();
            }
            originTextView.setText(originStr);
        }

        // destination
        destinationTextView = view.findViewById(R.id.f_travel_t_destination);
        if (travel.getDestination() != null) {
            String destinationStr = "To: ";
            if (travel.getDestination().getName() != null) {
                destinationStr += travel.getDestination().getName();
            }
            destinationTextView.setText(destinationStr);
        }

        // route fragment
        view.findViewById(R.id.f_travel_b_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteEditorFragment routeEditorFragment = RouteEditorFragment.newInstance(travel.getRoute(), travel.getOrigin(), travel.getDestination());
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.f_travel_fl_root, routeEditorFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        // images fragment
        view.findViewById(R.id.f_travel_b_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelImagesFragment travelImagesFragment = TravelImagesFragment.newInstance(travel.getImages());
                travelImagesFragment.setOnTravelImagesEditListener(new TravelImagesFragment.OnTravelImagesEditListener() {
                    @Override
                    public void onTravelImagesEditedListener(ArrayList<TravelImage> travelImages) {
                        travel.setImages(travelImages);

                        onTravelEditListener.onTravelEdit(travel);
                    }
                });

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left,0, 0, android.R.anim.slide_out_right)
                        .add(R.id.f_travel_fl_root, travelImagesFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // videos fragment
        Button travelVideosButton = view.findViewById(R.id.f_travel_b_videos);
        travelVideosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelVideosFragment travelVideosFragment = TravelVideosFragment.newInstance(travel.getVideos());
                getChildFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left,0, 0, android.R.anim.slide_out_right)
                        .add(R.id.f_travel_fl_root, travelVideosFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // notify
        Switch notifySwitch = view.findViewById(R.id.f_travel_s_notify);
        notifySwitch.setChecked(travel.shouldNotify() == 1);
        notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                travel.setShouldNotify(isChecked ? 1 : 0);
                onTravelEditListener.onTravelEdit(travel);
            }
        });

        // description
        final ViewSwitcher descriptionViewSwitcher = view.findViewById(R.id.f_travel_vs_description);
        final TextView descriptionTextView = view.findViewById(R.id.f_travel_t_description);
        descriptionTextView.setText(travel.getDescription());
        final EditText descriptionEditText = view.findViewById(R.id.f_travel_et_description);
        descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionViewSwitcher.showNext();
                descriptionEditText.setText(descriptionTextView.getText());
                descriptionEditText.requestFocus();
            }
        });
        descriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    descriptionViewSwitcher.showPrevious();
                    descriptionTextView.setText(descriptionEditText.getText().toString());

                    travel.setDescription(descriptionTextView.getText().toString());
                    onTravelEditListener.onTravelEdit(travel);

                    KeyboardStaticHelper.hideKeyboard(getActivity());
                }

                return true;
            }
        });
    }

    private void popFromBackStack() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnTravelDeleteListener &&
                context instanceof OnTravelEditListener) {
            onTravelDeleteListener = (OnTravelDeleteListener) context;
            onTravelEditListener = (OnTravelEditListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        onTravelDeleteListener = null;
        onTravelEditListener = null;
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

        if (travel.getOrigin() != null) {
            String originStr = "From: ";
            if (travel.getOrigin().getName() != null) {
                originStr += travel.getOrigin().getName();
            }
            originTextView.setText(originStr);
        } else {
            originTextView.setText("From: ");
        }
        if (travel.getDestination() != null) {
            String destinationStr = "To: ";
            if (travel.getDestination().getName() != null) {
                destinationStr += travel.getDestination().getName();
            }
            destinationTextView.setText(destinationStr);
        } else {
            destinationTextView.setText("To: ");
        }

        onTravelEditListener.onTravelEdit(travel);
    }

    @Override
    public void onTravelEdit(Travel travel) {
        this.travel = travel;
        onTravelEditListener.onTravelEdit(travel);
    }

    @Override
    public void onTravelVideosEditListener(ArrayList<TravelVideo> travelVideos) {
        travel.setVideos(travelVideos);
        onTravelEditListener.onTravelEdit(travel);
    }

    public interface OnTravelEditListener {
        void onTravelEdit(Travel travel);
    }

    public interface OnTravelDeleteListener {
        void onTravelDelete(String id);
    }
}
