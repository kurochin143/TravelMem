package com.isra.israel.travelmem.view_model.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.isra.israel.travelmem.repository.TravelsRepository;
import com.isra.israel.travelmem.view_model.TravelsViewModel;

public class TravelsVMFactory implements ViewModelProvider.Factory {

    private TravelsRepository travelsRepository;

    public TravelsVMFactory(TravelsRepository travelsRepository) {
        this.travelsRepository = travelsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TravelsViewModel(travelsRepository);
    }
}
