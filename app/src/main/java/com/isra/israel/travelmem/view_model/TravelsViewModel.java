package com.isra.israel.travelmem.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.repository.TravelsRepository;

import java.util.ArrayList;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TravelsViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TravelsRepository travelsRepository;

    private MutableLiveData<ArrayList<Travel>> travelsLiveData = new MutableLiveData<>();
    private MutableLiveData<Travel> addedTravelLiveData = new MutableLiveData<>();
    private MutableLiveData<String> removedTravelLiveData = new MutableLiveData<>();
    private MutableLiveData<Travel> updatedTravelLiveData = new MutableLiveData<>();

    public TravelsViewModel(TravelsRepository travelsRepository) {
        this.travelsRepository = travelsRepository;
    }

    public LiveData<ArrayList<Travel>> getTravelsLiveData() {
        return travelsLiveData;
    }

    public void getTravels(String uid, String token) {
        compositeDisposable.add(travelsRepository.getTravels(uid, token).subscribe(new Consumer<ArrayList<Travel>>() {
            @Override
            public void accept(ArrayList<Travel> travels) throws Exception {
                travelsLiveData.postValue(travels);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                travelsLiveData.postValue(null);
            }
        }));
    }

    public LiveData<Travel> getAddedTravelLiveData() {
        return addedTravelLiveData;
    }

    public void addTravel(String uid, String token, Travel travel) {
        compositeDisposable.add(travelsRepository.addTravel(uid, token, travel).subscribe(new Consumer<Travel>() {
            @Override
            public void accept(Travel travel) throws Exception {
                addedTravelLiveData.postValue(travel);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                addedTravelLiveData.postValue(null);
            }
        }));
    }

    public LiveData<String> getRemovedTravelLiveData() {
        return removedTravelLiveData;
    }

    public void removeTravel(String uid, String id, String token) {
        compositeDisposable.add(travelsRepository.removeTravel(uid, id, token).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                removedTravelLiveData.postValue(s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                removedTravelLiveData.postValue(null);
            }
        }));
    }

    public LiveData<Travel> getUpdatedTravelLiveData() {
        return updatedTravelLiveData;
    }

    public void updateTravel(String uid, String token, Travel travel) {
        compositeDisposable.add(travelsRepository.updateTravel(uid, token, travel).subscribe(new Consumer<Travel>() {
            @Override
            public void accept(Travel travel) throws Exception {
                updatedTravelLiveData.postValue(travel);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                updatedTravelLiveData.postValue(null);
            }
        }));
    }

//
//    public void updateTravel(String uid, String token, Travel travel) {
//        travelsRepository.updateTravel(uid, token, travel);
//    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
