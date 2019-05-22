package com.isra.israel.travelmem.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.TravelVideo;

import java.util.Timer;
import java.util.TimerTask;

public class TravelVideoFragment extends Fragment {
    private static final String ARG_TRAVEL_VIDEO = "travel_video";

    private TravelVideo travelVideo;

    private OnTravelVideoEditListener onTravelVideoEditListener;

    private VideoView videoView;
    private ImageView playPauseImageView;
    private int videoDuration;
    private Timer videoSeekBarUpdateTimer;

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
        final View view = inflater.inflate(R.layout.fragment_travel_video, container, false);

        videoView = view.findViewById(R.id.f_travel_video_vv_video);

        if (travelVideo.getUriStr() != null) {
            videoView.setVideoURI(travelVideo.getUri());
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoDuration = videoView.getDuration();

                    final SeekBar videoPositionSeekBar = view.findViewById(R.id.f_travel_video_sb_video_position);
                    videoPositionSeekBar.setMax(videoDuration);
                    videoPositionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                videoView.seekTo(progress);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    videoSeekBarUpdateTimer = new Timer();
                    videoSeekBarUpdateTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int currentPosition = videoView.getCurrentPosition();

                                    if (currentPosition >= videoDuration) {
                                        videoPositionSeekBar.setProgress(videoDuration);
                                        pause();
                                    } else {
                                        videoPositionSeekBar.setProgress(currentPosition);
                                    }
                                }
                            });
                        }
                    }, 0, 100);

                    playPauseImageView = view.findViewById(R.id.f_travel_video_i_play_pause);
                    playPauseImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (videoView.isPlaying()) {
                                pause();
                            } else {
                                play();
                            }
                        }
                    });
                }
            });
        }

        view.findViewById(R.id.f_travel_video_b_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO delete actual file. only do this when unique file name generation is implemented

                onTravelVideoEditListener.onTravelVideoEdit(null);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(TravelVideoFragment.this)
                        .commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        TextView nameTextView = view.findViewById(R.id.f_travel_video_t_location_name);
        TextView latTextView = view.findViewById(R.id.f_travel_video_t_location_lat);
        TextView lngTextView = view.findViewById(R.id.f_travel_video_t_location_lng);
        if (travelVideo.getLocation() != null) {
            if (travelVideo.getLocation().getName() != null) {
                nameTextView.setText(travelVideo.getLocation().getName());
            } else {
                nameTextView.setVisibility(View.GONE);
            }
            if (travelVideo.getLocation().getLatLng() != null) {
                latTextView.setText(String.valueOf(travelVideo.getLocation().getLatLng().latitude));
                lngTextView.setText(String.valueOf(travelVideo.getLocation().getLatLng().longitude));
            }
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (videoSeekBarUpdateTimer != null) {
            videoSeekBarUpdateTimer.cancel();
        }
    }

    private void play() {
        if (videoView.isPlaying()) {
            return;
        }

        // TODO HIGH ANIMATION
        playPauseImageView.setImageResource(R.drawable.ic_pause_50dp);

        videoView.start();
    }

    private void pause() {
        if (!videoView.isPlaying()) {
            return;
        }

        playPauseImageView.setImageResource(R.drawable.ic_play_arrow_50dp);

        videoView.pause();
    }

    public void setOnTravelVideoEditListener(OnTravelVideoEditListener onTravelVideoEditListener) {
        this.onTravelVideoEditListener = onTravelVideoEditListener;
    }

    public interface OnTravelVideoEditListener {
        void onTravelVideoEdit(TravelVideo travelVideo);
    }

}
