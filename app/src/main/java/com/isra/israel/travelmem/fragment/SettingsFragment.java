package com.isra.israel.travelmem.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ViewSwitcher;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.dao.SettingsSPDAO;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch notificationSwitch = view.findViewById(R.id.f_settings_s_notification);
        notificationSwitch.setChecked(SettingsSPDAO.isNotificationEnabled(getContext()));
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsSPDAO.setNotificationEnabled(getContext(), isChecked);
            }
        });

        return view;
    }

}
