package com.isra.israel.travelmem.component;

import com.isra.israel.travelmem.activity.TravelsActivity;
import com.isra.israel.travelmem.module.TravelMemDataModule;
import com.isra.israel.travelmem.module.TravelMemModule;
import com.isra.israel.travelmem.module.TravelMemNetworkModule;
import com.isra.israel.travelmem.scope.TravelMemScope;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Subcomponent(modules = {TravelMemNetworkModule.class, TravelMemDataModule.class, TravelMemModule.class})
@Singleton
@TravelMemScope
public interface TravelMemComponent {
    void inject(TravelsActivity travelsActivity);
}
