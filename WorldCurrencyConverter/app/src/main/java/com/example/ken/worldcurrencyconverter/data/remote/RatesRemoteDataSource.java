package com.example.ken.worldcurrencyconverter.data.remote;

import com.example.ken.worldcurrencyconverter.data.RatesDataSource;
import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.example.ken.worldcurrencyconverter.webclient.FixerIOApiClient;
import com.example.ken.worldcurrencyconverter.webclient.ApiInterface;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ken on 2017-04-25.
 */

public class RatesRemoteDataSource implements RatesDataSource {
    private static RatesRemoteDataSource INSTANCE;

    // Web Client
    private ApiInterface mApiService;

    // Singleton
    public static RatesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RatesRemoteDataSource();
        }

        return INSTANCE;
    }

    // Prevent direct instantiation
    private RatesRemoteDataSource() {
        // Setup Web Client
        mApiService = FixerIOApiClient.getClient().create(ApiInterface.class);
    }

    @Override
    public Observable<ExchangeRatesResponse> getRates(String baseCurrencyCode) {
        return mApiService.getLatestExchangeRates(baseCurrencyCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
