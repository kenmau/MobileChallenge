package com.example.ken.worldcurrencyconverter.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.ken.worldcurrencyconverter.R;
import com.example.ken.worldcurrencyconverter.adapter.CurrencyAdapter;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ExchangeRatesActivity extends AppCompatActivity implements ExchangeRatesContract.View {
    private static final String TAG = ExchangeRatesActivity.class.getSimpleName();
    private static final int DEBOUNCE_THRESHOLD = 500; // in ms
    private static final int GRID_LAYOUT_SPAN_COUNT = 2; // show 2 cards width wise
    private static final int DOLLARS_MAX_LENGTH = 10;

    private ExchangeRatesContract.Presenter mPresenter;

    // Layout Views
    private CoordinatorLayout mParentLayout;
    private EditText mDollarsEditText;
    private EditText mCentsEditText;
    private Spinner mCurrencyCodeSpinner;
    private Button mGoButton;
    private RecyclerView mRecyclerViewCurrencies;
    private ProgressBar mProgressBar;

    // Recycler View Dependencies
    private CurrencyAdapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;

    // To hold our RxBinding references
    CompositeDisposable compositeDisposable;

    private Observable<String> createCurrencyCodeSpinnerObservable() {
        final PublishSubject<String> selectSubject = PublishSubject.create();
        mCurrencyCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                selectSubject.onNext(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return selectSubject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rates);

        mPresenter = new ExchangeRatesPresenter(this);
        compositeDisposable = new CompositeDisposable();

        // Initialize EditText Soft Keyboard Properties
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize layout properties
        mParentLayout = (CoordinatorLayout) findViewById(R.id.parentLayout);
        mDollarsEditText = (EditText) findViewById(R.id.etDollars);
        mCentsEditText = (EditText) findViewById(R.id.etCents);
        mCurrencyCodeSpinner = (Spinner) findViewById(R.id.spinCurrencyCode);
        mGoButton = (Button) findViewById(R.id.bGo);
        mRecyclerViewCurrencies = (RecyclerView) findViewById(R.id.rvCurrencies);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Setup Recycler View Dependencies
        mRecyclerViewLayoutManager = new GridLayoutManager(this, GRID_LAYOUT_SPAN_COUNT);
        mRecyclerViewCurrencies.setLayoutManager(mRecyclerViewLayoutManager);

        mRecyclerViewCurrencies.setItemAnimator(new SlideInUpAnimator());

        mRecyclerViewAdapter = new CurrencyAdapter();
        mRecyclerViewAdapter.setHasStableIds(true);
        mRecyclerViewCurrencies.setAdapter(mRecyclerViewAdapter);

        // Setup Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.currency_code_array, android.R.layout.simple_spinner_dropdown_item);
        mCurrencyCodeSpinner.setAdapter(spinnerAdapter);

        // Intercept any non-digits and shift in focus to cents field
        if (mDollarsEditText != null) {
            mDollarsEditText.setFilters(new InputFilter[] {
                    new InputFilter() {
                        @Override
                        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                            if (source.toString().matches("[0-9]*")) {
                                return source;
                            }

                            if (mCentsEditText != null) {
                                mCentsEditText.requestFocus();
                            }

                            // Filter out any non-digits
                            return "";
                        }
                    },
                    new InputFilter.LengthFilter(DOLLARS_MAX_LENGTH)
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Setup RxBinding Subscriptions to view elements
        // Each subscription simply passes the value to the presenter

        Disposable dollarEditTextDisp =
                RxTextView.textChanges(mDollarsEditText)
                        .debounce(DEBOUNCE_THRESHOLD, TimeUnit.MILLISECONDS)
                        .subscribe(new Consumer<CharSequence>() {
                            @Override
                            public void accept(CharSequence charSequence) throws Exception {
                                mPresenter.setDollars(charSequence.toString());
                            }
                        });
        compositeDisposable.add(dollarEditTextDisp);

        Disposable centEditTextDisp =
                RxTextView.textChanges(mCentsEditText)
                        .debounce(DEBOUNCE_THRESHOLD, TimeUnit.MILLISECONDS)
                        .subscribe(new Consumer<CharSequence>() {
                            @Override
                            public void accept(CharSequence charSequence) throws Exception {
                                mPresenter.setCents(charSequence.toString());
                            }
                        });
        compositeDisposable.add(centEditTextDisp);

        Disposable goButtonDisp =
                RxView.clicks(mGoButton)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        hideKeyboard();
                        mPresenter.fetchRates();
                    }
                });
        compositeDisposable.add(goButtonDisp);

        Disposable spinnerDisp =
                createCurrencyCodeSpinnerObservable().subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        hideKeyboard();
                        mPresenter.setBaseCurrency(s);
                    }
                });
        compositeDisposable.add(spinnerDisp);

        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPresenter.unSubscribe();
        compositeDisposable.clear();
    }


    // View Actions

    @Override
    public void clearRates() {
        mRecyclerViewAdapter.clearRates();
    }

    @Override
    public void setRates(Map<String, Double> rates, Double combinedAmount) {
        mRecyclerViewAdapter.setRates(rates, combinedAmount);
    }

    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showConnectionError() {
        Snackbar snackbar = Snackbar
                .make(mParentLayout, getResources().getString(R.string.check_internet_connection), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void showInvalidInputDollarsEditText() {
        mDollarsEditText.setError(getResources().getString(R.string.enter_number_between) + Integer.MAX_VALUE);
    }

    @Override
    public void showInvalidInputCentsEditText() {
        mDollarsEditText.setError(getResources().getString(R.string.enter_number_between) + "99");
    }


    @Override
    public void setPresenter(ExchangeRatesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        getCurrentFocus().clearFocus();

        if (mDollarsEditText != null) {
            mDollarsEditText.clearFocus();
        }
        if (mCentsEditText != null) {
            mCentsEditText.clearFocus();
        }
    }
}
