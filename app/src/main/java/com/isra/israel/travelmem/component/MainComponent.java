package com.isra.israel.travelmem.component;

import com.isra.israel.travelmem.module.TravelMemNetworkModule;

import dagger.Component;

@Component()
public interface MainComponent {
    TravelMemComponent plus(TravelMemNetworkModule travelMemNetworkModule);
}
