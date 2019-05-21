package com.isra.israel.travelmem.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.adapter.TravelVideosAdapter;
import com.isra.israel.travelmem.model.TravelVideo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class TravelVideosFragment extends Fragment {
    private static final String ARG_TRAVEL_VIDEOS = "travel_videos";
    private static final int RC_VIDEO = 0;

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
                travelVideos.remove(i);
            }
        }
        if (somethingRemoved) {
            onTravelVideosEditListener.onTravelVideosEditListener(travelVideos);
        }

        travelVideosAdapter = new TravelVideosAdapter();
        travelVideosAdapter.setOnTravelVideoClickListener(new TravelVideosAdapter.OnTravelVideoClickListener() {
            @Override
            public void onTravelVideoClick(TravelVideo travelVideo, int position) {
                openedTravelVideoPosition = position;

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
                        .add(R.id.f_travel_videos_fl_root, travelVideoFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        travelVideosAdapter.setTravelVideos(travelVideos);

        RecyclerView recyclerView = view.findViewById(R.id.f_travel_videos_r_videos);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(travelVideosAdapter);

        view.findViewById(R.id.f_travel_videos_b_add_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("video/*");

                startActivityForResult(intent, RC_VIDEO);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_VIDEO && resultCode == Activity.RESULT_OK && data.getData() != null) {
            TravelVideo travelVideo = new TravelVideo();
            // TODO VERY LOW unique file name generator or just check if file already exists and increment i
            try { // save external file internally because Uri access expires
                // TODO EXPERIMENT store 'new File(data.getData).getPath()' and see if it'll work tomorrow?
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());

                File uriFile = new File(data.getData().getPath());
                String outputFileStr = getContext().getFilesDir().getPath() + "/" + uriFile.getName() + ".tv";
                File outputFile = new File(outputFileStr);

                byte[] buffer = new byte[1024 * 1024];

                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                }

                inputStream.close();
                fileOutputStream.close();

                travelVideo.setUriStr(outputFileStr);

                travelVideosAdapter.addTravelVideo(travelVideo);

                onTravelVideosEditListener.onTravelVideosEditListener(travelVideosAdapter.getTravelVideos());

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load media", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void setOnTravelVideosEditListener(OnTravelVideosEditListener l) {
        onTravelVideosEditListener = l;
    }

    public interface OnTravelVideosEditListener {
        void onTravelVideosEditListener(ArrayList<TravelVideo> travelVideos);
    }
}
