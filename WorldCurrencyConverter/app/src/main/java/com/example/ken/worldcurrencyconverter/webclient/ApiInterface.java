package com.example.ken.worldcurrencyconverter.webclient;

import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ken on 2017-04-23.
 */

public interface ApiInterface {
    @GET("latest")
    Observable<ExchangeRatesResponse> getLatestExchangeRates(@Query("base") String baseCurrency);
}
