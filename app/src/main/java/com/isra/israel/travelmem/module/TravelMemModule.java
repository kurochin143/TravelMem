package com.isra.israel.travelmem.module;

import com.isra.israel.travelmem.repository.TravelsRepository;
import com.isra.israel.travelmem.view_model.factory.TravelsVMFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TravelMemModule {

    @Provides
    @Singleton
    public TravelsVMFactory provideTravelsVMFactory(TravelsRepository travelsRepository) {
        return new TravelsVMFactory(travelsRepository);
    }
}
