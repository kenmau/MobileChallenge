package com.example.ken.worldcurrencyconverter.ui;

import com.example.ken.worldcurrencyconverter.BasePresenter;
import com.example.ken.worldcurrencyconverter.BaseView;

import java.util.Map;

/**
 * Created by ken on 2017-04-25.
 */

public interface ExchangeRatesContract {

    interface View extends BaseView<Presenter> {

        void clearRates();
        void setRates(Map<String, Double> rates, Double amount);

        void setLastUpdated(String lastUpdated);
        void clearLastUpdated();


        void showProgressBar();
        void hideProgressBar();

        void showInvalidInputDollarsEditText();
        void showInvalidInputCentsEditText();
        void showConnectionError();

    }

    interface Presenter extends BasePresenter {

        void setDollars(String dollarsAmount);
        void setCents(String centsAmount);
        void setBaseCurrency(String baseCurrency);

        void fetchRates();

    }
}
