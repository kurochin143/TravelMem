package com.isra.israel.travelmem.module;

import com.isra.israel.travelmem.api.TravelMemApi;
import com.isra.israel.travelmem.repository.TravelsRepository;
import com.isra.israel.travelmem.scope.TravelMemScope;

import dagger.Module;
import dagger.Provides;

@Module
@TravelMemScope
public class TravelMemDataModule {

    @Provides
    public TravelsRepository provideTravelsRepository(TravelMemApi travelMemApi) {
        return new TravelsRepository(travelMemApi);
    }

}
