package com.isra.israel.travelmem;

import android.app.Application;

import com.isra.israel.travelmem.component.DaggerMainComponent;
import com.isra.israel.travelmem.component.MainComponent;
import com.isra.israel.travelmem.component.TravelMemComponent;
import com.isra.israel.travelmem.gson.TravelMemGson;
import com.isra.israel.travelmem.module.TravelMemNetworkModule;

public class TravelMemApp extends Application {

    private MainComponent mainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mainComponent = DaggerMainComponent.builder()
                .build();
    }

    public TravelMemComponent createTravelMemSubComponent() {
        return mainComponent.plus(new TravelMemNetworkModule(TravelMemGson.gson));
    }
}
