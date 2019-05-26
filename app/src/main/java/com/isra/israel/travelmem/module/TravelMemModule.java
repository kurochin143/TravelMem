package com.isra.israel.travelmem.module;

import com.isra.israel.travelmem.repository.TravelsRepository;
import com.isra.israel.travelmem.scope.TravelMemScope;
import com.isra.israel.travelmem.view_model.factory.TravelsVMFactory;

import dagger.Module;
import dagger.Provides;

@Module
@TravelMemScope
public class TravelMemModule {

    @Provides
    public TravelsVMFactory provideTravelsVMFactory(TravelsRepository travelsRepository) {
        return new TravelsVMFactory(travelsRepository);
    }
}
