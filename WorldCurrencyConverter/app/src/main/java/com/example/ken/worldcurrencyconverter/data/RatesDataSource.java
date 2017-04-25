package com.example.ken.worldcurrencyconverter.data;

import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;

import io.reactivex.Observable;

/**
 * Created by ken on 2017-04-25.
 */

public interface RatesDataSource {
    Observable<ExchangeRatesResponse> getRates(String baseCurrencyCode);
}
