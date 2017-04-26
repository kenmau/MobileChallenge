package com.example.ken.worldcurrencyconverter.model.datasource.remote;

import com.example.ken.worldcurrencyconverter.model.datasource.ExchangeRatesDataSource;
import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.example.ken.worldcurrencyconverter.model.datasource.local.ExchangeRatesLocalDataSource;
import com.example.ken.worldcurrencyconverter.webclient.FixerIOApiClient;
import com.example.ken.worldcurrencyconverter.webclient.ApiInterface;

import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ken on 2017-04-25.
 */

public class ExchangeRatesRemoteDataSource implements ExchangeRatesDataSource.Remote {
    private static ExchangeRatesRemoteDataSource INSTANCE;

    // Web Client
    private ApiInterface mApiService;

    private static Calendar mLastRemotelyRefreshed;

    // Singleton
    public static ExchangeRatesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExchangeRatesRemoteDataSource();
        }

        return INSTANCE;
    }

    // Prevent direct instantiation
    private ExchangeRatesRemoteDataSource() {
        // Setup Web Client
        mApiService = FixerIOApiClient.getClient().create(ApiInterface.class);
    }

    @Override
    public Observable<ExchangeRatesResponse> getRates(String baseCurrencyCode) {
        mLastRemotelyRefreshed = Calendar.getInstance();

        return mApiService.getLatestExchangeRates(baseCurrencyCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Calendar lastRemotelyRefreshed() {
        return mLastRemotelyRefreshed;
    }
}
