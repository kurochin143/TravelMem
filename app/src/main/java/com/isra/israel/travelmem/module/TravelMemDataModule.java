package com.isra.israel.travelmem.module;

import com.isra.israel.travelmem.api.TravelMemApi;
import com.isra.israel.travelmem.repository.TravelsRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TravelMemDataModule {

    @Provides
    @Singleton
    public TravelsRepository provideTravelsRepository(TravelMemApi travelMemApi) {
        return new TravelsRepository(travelMemApi);
    }

}
