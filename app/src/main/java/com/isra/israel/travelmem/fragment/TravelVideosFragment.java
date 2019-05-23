package com.isra.israel.travelmem.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.adapter.TravelVideosAdapter;
import com.isra.israel.travelmem.model.TravelVideo;

import java.io.File;
import java.util.ArrayList;

public class TravelVideosFragment extends Fragment {
    private static final String ARG_TRAVEL_VIDEOS = "travel_videos";
    private static final int SPAN_COUNT = 2;

    private TravelVideosAdapter travelVideosAdapter;
    private int openedTravelVideoPosition;

    private OnTravelVideosEditListener onTravelVideosEditListener;

    public TravelVideosFragment() {
        // Required empty public constructor
    }

    public static TravelVideosFragment newInstance(ArrayList<TravelVideo> travelVideos) {
        TravelVideosFragment fragment = new TravelVideosFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_TRAVEL_VIDEOS, travelVideos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travel_videos, container, false);

        ArrayList<TravelVideo> travelVideos = getArguments().getParcelableArrayList(ARG_TRAVEL_VIDEOS);
        if (travelVideos == null) {
            travelVideos = new ArrayList<>();
        }

        // check for non existing files
        boolean somethingRemoved = false;
        for (int i = travelVideos.size() - 1; i > -1; --i) {
            TravelVideo travelVideo = travelVideos.get(i);

            File file = new File(travelVideo.getUriStr());
            if (!file.exists()) {
                somethingRemoved = true;
                // remove it
                travelVideos.remove(i);
            }
        }
        if (somethingRemoved) {
            onTravelVideosEditListener.onTravelVideosEditListener(travelVideos);
        }

        // videos adapter
        travelVideosAdapter = new TravelVideosAdapter();
        travelVideosAdapter.setOnTravelVideoClickListener(new TravelVideosAdapter.OnTravelVideoClickListener() {
            @Override
            public void onTravelVideoClick(TravelVideo travelVideo, int position) {
                openedTravelVideoPosition = position;

                // open travel video fragment
                TravelVideoFragment travelVideoFragment = TravelVideoFragment.newInstance(travelVideo);
                travelVideoFragment.setOnTravelVideoEditListener(new TravelVideoFragment.OnTravelVideoEditListener() {
                    @Override
                    public void onTravelVideoEdit(TravelVideo travelVideo) {
                        if (travelVideo == null) { // removed
                            travelVideosAdapter.removeTravelVideo(openedTravelVideoPosition);
                        } else {
                            travelVideosAdapter.setTravelVideoAt(travelVideo, openedTravelVideoPosition);
                        }

                        onTravelVideosEditListener.onTravelVideosEditListener(travelVideosAdapter.getTravelVideos());
                    }
                });

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left,0, 0, android.R.anim.slide_out_right)
                        .add(R.id.f_travel_videos_fl_root, travelVideoFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        travelVideosAdapter.setTravelVideos(travelVideos);

        // videos recycler
        RecyclerView recyclerView = view.findViewById(R.id.f_travel_videos_r_videos);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
        recyclerView.setAdapter(travelVideosAdapter);

        // add video button
        view.findViewById(R.id.f_travel_videos_b_add_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTravelVideoFragment addTravelVideoFragment = AddTravelVideoFragment.newInstance(null);
                addTravelVideoFragment.setOnTravelVideoAddListener(new AddTravelVideoFragment.OnTravelVideoAddListener() {
                    @Override
                    public void onTravelVideoAdd(TravelVideo travelVideo) {
                        if (travelVideo != null) {
                            travelVideosAdapter.addTravelVideo(travelVideo);

                            onTravelVideosEditListener.onTravelVideosEditListener(travelVideosAdapter.getTravelVideos());
                        }
                    }
                });

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left,0, 0, android.R.anim.slide_out_right)
                        .add(R.id.f_travel_videos_fl_root, addTravelVideoFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    public void setOnTravelVideosEditListener(OnTravelVideosEditListener l) {
        onTravelVideosEditListener = l;
    }

    public interface OnTravelVideosEditListener {
        void onTravelVideosEditListener(ArrayList<TravelVideo> travelVideos);
    }
}
