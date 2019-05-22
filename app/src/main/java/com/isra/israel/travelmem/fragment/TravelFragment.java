package com.isra.israel.travelmem.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.model.TravelImage;
import com.isra.israel.travelmem.model.TravelVideo;
import com.isra.israel.travelmem.model.directions.Point;
import com.isra.israel.travelmem.model.directions.Route;

import java.util.ArrayList;

public class TravelFragment extends Fragment {

    private static final String ARG_TRAVEL = "travel";
    private static final String ARG_IS_CREATING = "is_creating";

    private Travel travel;
    private boolean isCreating;

    private OnTravelEditListener onTravelEditListener;
    private OnTravelCreateListener onTravelCreateListener;
    private OnTravelDeleteListener onTravelDeleteListener;

    public TravelFragment() {
        // Required empty public constructor
    }

    public static TravelFragment newInstance(Travel travel, boolean isCreating) {
        TravelFragment fragment = new TravelFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRAVEL, travel);
        args.putBoolean(ARG_IS_CREATING, isCreating);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            travel = getArguments().getParcelable(ARG_TRAVEL);
            isCreating = getArguments().getBoolean(ARG_IS_CREATING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travel, container, false);

        // map
        Button viewOnMapButton = view.findViewById(R.id.f_travel_b_view_on_map);
        if (isCreating) {
            viewOnMapButton.setVisibility(View.GONE);
        } else {
            viewOnMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open travel map fragment
                    TravelMapViewFragment travelMapViewFragment = TravelMapViewFragment.newInstance(travel);
                    travelMapViewFragment.setOnTravelEditListener(new TravelMapViewFragment.OnTravelEditListener() {
                        @Override
                        public void onTravelEdit(Travel travel) {
                            TravelFragment.this.travel = travel;
                            onTravelEditListener.onTravelEdit(TravelFragment.this.travel);
                        }
                    });
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.f_travel_fl_root, travelMapViewFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        // delete button
        Button deleteButton = view.findViewById(R.id.f_travel_b_delete);
        if (isCreating) {
            deleteButton.setVisibility(View.GONE);
        } else {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTravelDeleteListener.onTravelDelete(travel.getId());

                    // close fragment
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(TravelFragment.this)
                            .commit();
                }
            });
        }

        // creating save button
        Button creatingSaveButton = view.findViewById(R.id.f_travel_b_creating_save);
        if (isCreating) {
            creatingSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // edit
                    onTravelCreateListener.onTravelCreate(travel);

                    // close fragment
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(TravelFragment.this)
                            .commit();
                }
            });
        } else {
            creatingSaveButton.setVisibility(View.GONE);
        }

        // creating cancel button
        Button creatingCancelButton = view.findViewById(R.id.f_travel_b_creating_cancel);
        if (isCreating) {
            creatingCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // close fragment
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(TravelFragment.this)
                            .commit();
                }
            });
        } else {
            creatingCancelButton.setVisibility(View.GONE);
        }

        // name
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

                        if (onTravelEditListener != null) {
                            onTravelEditListener.onTravelEdit(travel);
                        }

                        return true;
                    }
                });

                ViewGroup viewGroup = (ViewGroup)nameTextView.getParent();
                viewGroup.addView(nameEditText, viewGroup.indexOfChild(nameTextView));
            }
        });

        // TODO edit date
        // start date
        TextView startDateTextView = view.findViewById(R.id.f_travel_t_start_date);
        startDateTextView.setText(travel.getStartDate());

        // end date
        TextView endDateTextView = view.findViewById(R.id.f_travel_t_end_date);
        endDateTextView.setText(travel.getEndDate());

        // origin
        final TextView originTextView = view.findViewById(R.id.f_travel_t_origin);
        if (travel.getOrigin() != null) {
            originTextView.setText(travel.getOrigin().getName());
        }

        // destination
        final TextView destinationTextView = view.findViewById(R.id.f_travel_t_destination);
        if (travel.getDestination() != null) {
            destinationTextView.setText(travel.getDestination().getName());
        }

        // route fragment
        view.findViewById(R.id.f_travel_b_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteEditorFragment routeEditorFragment = RouteEditorFragment.newInstance(travel.getRoute());
                routeEditorFragment.setOnRouteEditListener(new RouteEditorFragment.OnRouteEditListener() {
                    @Override
                    public void onRouteEdit(Route route, Point origin, Point destination) {
                        travel.setRoute(route);
                        travel.setOrigin(origin);
                        travel.setDestination(destination);

                        originTextView.setText(origin.getName());
                        destinationTextView.setText(destination.getName());

                        if (onTravelEditListener != null) {
                            onTravelEditListener.onTravelEdit(travel);
                        }
                    }
                });

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.f_travel_fl_root, routeEditorFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // images fragment
        view.findViewById(R.id.f_travel_b_view_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelImagesFragment travelImagesFragment = TravelImagesFragment.newInstance(travel.getImages());
                travelImagesFragment.setOnTravelImagesEditListener(new TravelImagesFragment.OnTravelImagesEditListener() {
                    @Override
                    public void onTravelImagesEditedListener(ArrayList<TravelImage> travelImages) {
                        travel.setImages(travelImages);

                        if (onTravelEditListener != null) {
                            onTravelEditListener.onTravelEdit(travel);
                        }
                    }
                });

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.f_travel_fl_root, travelImagesFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // videos fragment
        view.findViewById(R.id.f_travel_b_view_videos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelVideosFragment travelVideosFragment = TravelVideosFragment.newInstance(travel.getVideos());
                travelVideosFragment.setOnTravelVideosEditListener(new TravelVideosFragment.OnTravelVideosEditListener() {
                    @Override
                    public void onTravelVideosEditListener(ArrayList<TravelVideo> travelVideos) {
                        travel.setVideos(travelVideos);

                        if (onTravelEditListener != null) {
                            onTravelEditListener.onTravelEdit(travel);
                        }
                    }
                });

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.f_travel_fl_root, travelVideosFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    public void setOnTravelEditListener(OnTravelEditListener l) {
        onTravelEditListener = l;
    }

    public void setOnTravelCreateListener(OnTravelCreateListener l) {
        onTravelCreateListener = l;
    }

    public void setOnTravelDeleteListener(OnTravelDeleteListener l) {
        onTravelDeleteListener = l;
    }

    public interface OnTravelEditListener {
        void onTravelEdit(Travel travel);
    }

    public interface OnTravelCreateListener {
        void onTravelCreate(Travel travel);
    }

    public interface OnTravelDeleteListener {
        void onTravelDelete(String id);
    }
}
