package com.example.ken.worldcurrencyconverter;

import com.example.ken.worldcurrencyconverter.ui.ExchangeRatesContract;
import com.example.ken.worldcurrencyconverter.ui.ExchangeRatesPresenter;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.NumberFormat;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRatesPresenterTest {

    @Mock
    private ExchangeRatesContract.View mView;

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
}