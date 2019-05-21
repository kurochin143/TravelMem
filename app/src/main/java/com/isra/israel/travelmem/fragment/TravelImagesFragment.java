package com.isra.israel.travelmem.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.adapter.TravelImagesAdapter;
import com.isra.israel.travelmem.model.TravelImage;

import java.util.ArrayList;

public class TravelImagesFragment extends Fragment {
    private static final String ARG_TRAVEL_IMAGES = "travel_images";
    private static final int RC_IMAGE = 0;

    private OnTravelImagesEditedListener onTravelImagesEditedListener;
    private int openedTravelImagePosition;

    private TravelImagesAdapter travelImagesAdapter;

    public TravelImagesFragment() {
        // Required empty public constructor
    }

    public static TravelImagesFragment newInstance(ArrayList<TravelImage> travelImages) {
        TravelImagesFragment fragment = new TravelImagesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_TRAVEL_IMAGES, travelImages);
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

        ArrayList<TravelImage> travelImages = getArguments().getParcelableArrayList(ARG_TRAVEL_IMAGES);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travel_images, container, false);

        travelImagesAdapter = new TravelImagesAdapter();
        travelImagesAdapter.setTravelImages(travelImages);
        travelImagesAdapter.setOnTravelImageClickListener(new TravelImagesAdapter.OnTravelImageClickListener() {
            @Override
            public void onTravelImageClick(TravelImage travelImage, int position) {
                openedTravelImagePosition = position;

                TravelImageFragment travelImageFragment = TravelImageFragment.newInstance(travelImage);
                travelImageFragment.setOnTravelImageEditListener(new TravelImageFragment.OnTravelImageEditListener() {
                    @Override
                    public void onTravelImageEdit(TravelImage travelImage) {
                        if (travelImage == null) { // deleted
                            travelImagesAdapter.removeTravelImage(openedTravelImagePosition);
                        } else {
                            travelImagesAdapter.setTravelImageAt(travelImage, openedTravelImagePosition);
                        }

                        onTravelImagesEditedListener.onTravelImagesEditedListener(travelImagesAdapter.getTravelImages());
                    }
                });

                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.f_travel_images_fl_root, travelImageFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.f_travel_images_r_images);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(travelImagesAdapter);

        view.findViewById(R.id.f_travel_images_b_add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");

                startActivityForResult(intent, RC_IMAGE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE && resultCode == Activity.RESULT_OK && data.getData() != null) {
            TravelImage travelImage = new TravelImage();
            travelImage.setUri(data.getData().toString());

            travelImagesAdapter.addTravelImage(travelImage);

            onTravelImagesEditedListener.onTravelImagesEditedListener(travelImagesAdapter.getTravelImages());
        }
    }

    public void setOnTravelImagesEditedListener(OnTravelImagesEditedListener l) {
        onTravelImagesEditedListener = l;
    }

    public interface OnTravelImagesEditedListener {
        void onTravelImagesEditedListener(ArrayList<TravelImage> travelImages);
    }
}
