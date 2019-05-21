package com.isra.israel.travelmem.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.TravelImage;

public class TravelImageFragment extends Fragment {
    private static final String ARG_TRAVEL_IMAGE = "travel_image";

    private TravelImage travelImage;

    private OnTravelImageEditListener onTravelImageEditListener;

    public TravelImageFragment() {
        // Required empty public constructor
    }

    public static TravelImageFragment newInstance(TravelImage travelImage) {
        TravelImageFragment fragment = new TravelImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRAVEL_IMAGE, travelImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            travelImage = getArguments().getParcelable(ARG_TRAVEL_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travel_image, container, false);
        ImageView imageView = view.findViewById(R.id.f_travel_image_i_image);
        if (travelImage.getUriStr() != null) {
            imageView.setImageURI(travelImage.getUri());
        }

        final ViewSwitcher viewSwitcher = view.findViewById(R.id.f_travel_image_vs_description);
        final TextView descriptionTextView = view.findViewById(R.id.f_travel_image_t_description);
        descriptionTextView.setText(travelImage.getDescription());
        final EditText descriptionEditText = view.findViewById(R.id.f_travel_image_et_description);

        descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSwitcher.showNext();
                descriptionEditText.setText(descriptionTextView.getText());
                descriptionEditText.requestFocus();

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(descriptionEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        descriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    viewSwitcher.showPrevious();
                    travelImage.setDescription(descriptionEditText.getText().toString());
                    onTravelImageEditListener.onTravelImageEdit(travelImage);

                    descriptionTextView.setText(descriptionEditText.getText());
                }

                return true;
            }
        });

        return view;
    }

    public void setOnTravelImageEditListener(OnTravelImageEditListener l) {
        onTravelImageEditListener = l;
    }

    public interface OnTravelImageEditListener {
        void onTravelImageEdit(TravelImage travelImage);
    }
}
