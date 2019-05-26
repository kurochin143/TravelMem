package com.isra.israel.travelmem.module;

import com.google.gson.Gson;
import com.isra.israel.travelmem.api.TravelMemApi;
import com.isra.israel.travelmem.gson.TravelMemGson;
import com.isra.israel.travelmem.scope.TravelMemScope;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class TravelMemNetworkModule {

    private static final String BASE_URL = "https://lambda-project-week-w10-f.firebaseio.com/";

    public static final long READ_TIMEOUT = 10000;
    public static final long CONNECT_TIMEOUT = 10000;

    private Gson gson;

    public TravelMemNetworkModule(Gson gson) {
        this.gson = gson;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public TravelMemApi provideTravelMemApi(Retrofit retrofit) {
        return retrofit.create(TravelMemApi.class);
    }

}
