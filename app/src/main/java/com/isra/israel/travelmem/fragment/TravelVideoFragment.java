package com.isra.israel.travelmem.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.TravelVideo;

public class TravelVideoFragment extends Fragment {
    private static final String ARG_TRAVEL_VIDEO = "travel_video";

    private TravelVideo travelVideo;

    private OnTravelVideoEditListener onTravelVideoEditListener;

    public TravelVideoFragment() {
        // Required empty public constructor
    }

    public static TravelVideoFragment newInstance(TravelVideo travelVideo) {
        TravelVideoFragment fragment = new TravelVideoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRAVEL_VIDEO, travelVideo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            travelVideo = getArguments().getParcelable(ARG_TRAVEL_VIDEO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travel_video, container, false);

        VideoView videoView = view.findViewById(R.id.f_travel_video_vv_video);
        if (travelVideo.getUriStr() != null) {
            videoView.setVideoURI(travelVideo.getUri());
            videoView.start();
        }

        return view;
    }

    public void setOnTravelVideoEditListener(OnTravelVideoEditListener onTravelVideoEditListener) {
        this.onTravelVideoEditListener = onTravelVideoEditListener;
    }

    public interface OnTravelVideoEditListener {
        void onTravelVideoEdit(TravelVideo travelVideo);
    }
}
