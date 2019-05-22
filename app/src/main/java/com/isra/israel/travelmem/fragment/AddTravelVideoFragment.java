package com.isra.israel.travelmem.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.TravelVideo;
import com.isra.israel.travelmem.model.directions.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddTravelVideoFragment extends Fragment {
    private static final String ARG_LOCATION = "location";
    private static final int RC_VIDEO = 0;

    private TravelVideo travelVideo = new TravelVideo();

    private OnTravelVideoAddListener onTravelVideoAddListener;

    private TextView videoTextView;

    public AddTravelVideoFragment() {
        // Required empty public constructor
    }

    public static AddTravelVideoFragment newInstance(Point location) {
        AddTravelVideoFragment fragment = new AddTravelVideoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Point location = getArguments().getParcelable(ARG_LOCATION);
            if (location == null) {
                location = new Point();
                location.setLatLng(new LatLng(0.0, 0.0));
            }
            travelVideo.setLocation(location);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_travel_video, container, false);

        videoTextView = view.findViewById(R.id.f_add_travel_video_t_video);

        view.findViewById(R.id.f_add_travel_video_b_select_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("video/*");

                startActivityForResult(intent, RC_VIDEO);
            }
        });

        EditText latitudeEditText = view.findViewById(R.id.f_add_travel_video_et_latitude);
        latitudeEditText.setText(String.valueOf(travelVideo.getLocation().getLatLng().latitude));
        latitudeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                travelVideo.getLocation().setLatLng(new LatLng(Double.parseDouble(s.toString()), travelVideo.getLocation().getLatLng().longitude));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText longitudeEditText = view.findViewById(R.id.f_add_travel_video_et_longitude);
        longitudeEditText.setText(String.valueOf(travelVideo.getLocation().getLatLng().longitude));
        longitudeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                travelVideo.getLocation().setLatLng(new LatLng(travelVideo.getLocation().getLatLng().latitude, Double.parseDouble(s.toString())));
            }

            @Override
            public void afterTextChanged(Editable s) {
                onTravelVideoAddListener.onTravelVideoAdd(travelVideo);
            }
        });

        view.findViewById(R.id.f_add_travel_video_b_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (travelVideo.getUriStr() != null) {
                    onTravelVideoAddListener.onTravelVideoAdd(travelVideo);

                    close();
                } else {
                    Toast.makeText(getContext(), "Please select a video", Toast.LENGTH_LONG).show();
                }
            }
        });

        view.findViewById(R.id.f_add_travel_video_b_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        // TODO LOW description

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_VIDEO && resultCode == Activity.RESULT_OK && data.getData() != null) {
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
                videoTextView.setText(new File(data.getData().getPath()).getName());

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void close() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(this)
                .commit();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void setOnTravelVideoAddListener(OnTravelVideoAddListener l) {
        onTravelVideoAddListener = l;
    }

    public interface OnTravelVideoAddListener {
        void onTravelVideoAdd(TravelVideo travelVideo);
    }
}
