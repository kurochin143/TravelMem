package com.isra.israel.travelmem.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.repository.TravelsRepository;

import java.util.ArrayList;

public class TravelsViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Travel>> travelsLiveData;
    private TravelsRepository travelsRepository;

    public LiveData<ArrayList<Travel>> getTravelsLiveData(Context context, String uid, String token) {
        if (travelsLiveData == null) {
            travelsRepository = new TravelsRepository(context);
            travelsLiveData = travelsRepository.getData(uid, token);
        }

        return travelsLiveData;
    }

    public void reloadData(String uid, String token) {
        travelsRepository.reloadData(uid, token);
    }

    public void addTravel(String uid, String token, Travel travel) {

    }
}
