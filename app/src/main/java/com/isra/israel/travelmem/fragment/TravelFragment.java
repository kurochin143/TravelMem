package com.isra.israel.travelmem.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
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
        ImageView viewOnMapButton = view.findViewById(R.id.f_travel_i_view_on_map);
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
                    close();
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
                    close();
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
                    close();
                }
            });
        } else {
            creatingCancelButton.setVisibility(View.GONE);
        }

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

                    if (onTravelEditListener != null) {
                        onTravelEditListener.onTravelEdit(travel);
                    }
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
        final TextView originTextView = view.findViewById(R.id.f_travel_t_origin);
        if (travel.getOrigin() != null) {
            String originStr = "From: ";
            if (travel.getOrigin().getName() != null) {
                originStr += travel.getOrigin().getName();
            }
            originTextView.setText(originStr);
        }

        // destination
        final TextView destinationTextView = view.findViewById(R.id.f_travel_t_destination);
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
        view.findViewById(R.id.f_travel_b_images).setOnClickListener(new View.OnClickListener() {
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
        Button travelVideosButton = view.findViewById(R.id.f_travel_b_videos);
        if (isCreating) {
            travelVideosButton.setVisibility(View.GONE);
        } else {
            travelVideosButton.setOnClickListener(new View.OnClickListener() {
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
        }

        // notify
        Switch notifySwitch = view.findViewById(R.id.f_travel_s_notify);
        notifySwitch.setChecked(travel.shouldNotify() == 1);
        notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                travel.setShouldNotify(isChecked ? 1 : 0);

                if (onTravelEditListener != null) {
                    onTravelEditListener.onTravelEdit(travel);
                }
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

                    if (onTravelEditListener != null) {
                        onTravelEditListener.onTravelEdit(travel);
                    }
                }

                return true;
            }
        });

        return view;
    }

    private void close() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(this)
                .commit();
        getActivity().getSupportFragmentManager().popBackStack();
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
