package com.example.ken.worldcurrencyconverter;

import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.example.ken.worldcurrencyconverter.model.datasource.ExchangeRatesRepository;
import com.example.ken.worldcurrencyconverter.ui.ExchangeRatesContract;
import com.example.ken.worldcurrencyconverter.ui.ExchangeRatesPresenter;
import com.example.ken.worldcurrencyconverter.webclient.ApiInterface;
import com.example.ken.worldcurrencyconverter.webclient.FixerIOApiClient;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRatesPresenterTest {

    @Mock
    private ExchangeRatesContract.View mView;

    @Mock
    ApiInterface mApiService;

    private ExchangeRatesPresenter mPresenter;

    @Before
    public void setup() {
        mPresenter = new ExchangeRatesPresenter(mView);
    }

    @Test
    public void setDollars_IsCorrectValue() {
        mPresenter.setDollars("1");

        assertEquals(mPresenter.getmDollarsAmount().intValue(), 1);
    }

    @Test
    public void setCents1Digit_IsCorrectValue() {
        mPresenter.setCents("1");

        assertEquals(mPresenter.getmCentsAmount().intValue(), 10);
    }

    @Test
    public void setCents2Digit_IsCorrectValue() {
        mPresenter.setCents("51");

        assertEquals(mPresenter.getmCentsAmount().intValue(), 51);
    }

    @Test
    public void setDollarsCents_IsCorrectValue() {
        mPresenter.setDollars("204");
        mPresenter.setCents("20");

        assertEquals(mPresenter.getmDollarsAmount().intValue(), 204);
        assertEquals(mPresenter.getmCentsAmount().intValue(), 20);
    }

    @Test
    public void setBaseCurrency_IsCorrectValue() {
        mPresenter.setBaseCurrency("CAD");
        assertEquals(mPresenter.getmBaseCurrency(), "CAD");
    }
}