package com.example.ken.worldcurrencyconverter.activity;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ken.worldcurrencyconverter.R;
import com.example.ken.worldcurrencyconverter.adapter.CurrencyAdapter;
import com.example.ken.worldcurrencyconverter.model.ExchangeRatesResponse;
import com.example.ken.worldcurrencyconverter.webclient.ApiClient;
import com.example.ken.worldcurrencyconverter.webclient.ApiInterface;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExchangeRatesActivity extends AppCompatActivity {
    private static final String TAG = ExchangeRatesActivity.class.getSimpleName();
    private static final int DEBOUNCE_THRESHOLD = 500; // in ms
    private static final int GRID_LAYOUT_SPAN_COUNT = 2; // show 2 cards width wise
    private static final int DOLLARS_MAX_LENGTH = 10;

    // Layout Views
    private CoordinatorLayout mParentLayout;
    private EditText mDollarsEditText;
    private EditText mCentsEditText;
    private Spinner mCurrencyCodeSpinner;
    private Button mGoButton;
    private RecyclerView mRecyclerViewCurrencies;

    private String mBaseCurrencySelected;

    // Recycler View Dependencies
    private CurrencyAdapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;

    // Web Client
    private ApiInterface mApiService;

    // RxAndroid Cleanup
    private Disposable _disposable;

    // Setup RxAndroid Observables
    private Observable<String> createGoButtonClickObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                if (mGoButton != null) {
                    mGoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideKeyboard();

                            e.onNext("");
                        }
                    });

                    e.setCancellable(new Cancellable() {
                        @Override
                        public void cancel() throws Exception {
                            mGoButton.setOnClickListener(null);
                        }
                    });
                }

            }
        });
    }

    private Observable<String> createDollarsTextChangeObservable() {
        final Observable<String> textChangeObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // When the user inputs/deletes text, emit a signal to let consumers know
                        e.onNext(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };

                // Register the callback
                mDollarsEditText.addTextChangedListener(watcher);

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        // Unregister the callback
                        mDollarsEditText.removeTextChangedListener(watcher);
                    }
                });
            }
        });

        // TODO: Make sure input string is a number
        // Add a de-bounce "filter" so we're not constantly emitting
        return textChangeObservable
                .debounce(DEBOUNCE_THRESHOLD, TimeUnit.MILLISECONDS);
    }

    private Observable<String> createCentsTextChangeObservable() {
        final Observable<String> textChangeObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // When the user inputs/deletes text, emit a signal to let consumers know
                        e.onNext(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };

                // Register the callback
                mCentsEditText.addTextChangedListener(watcher);

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        // Unregister the callback
                        mCentsEditText.removeTextChangedListener(watcher);
                    }
                });
            }
        });

        // TODO: Make sure input string is a number
        // Add a de-bounce "filter" so we're not constantly emitting
        return textChangeObservable
                .debounce(DEBOUNCE_THRESHOLD, TimeUnit.MILLISECONDS);
    }

    private Observable<String> createCurrencyCodeSpinnerObservable() {
        final PublishSubject<String> selectSubject = PublishSubject.create();
        // for production code, unsubscribe, UI thread assertions are needed
        // see WidgetObservable from rxandroid for example
        mCurrencyCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                mBaseCurrencySelected = item;

                hideKeyboard();

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

        // Initialize EditText Soft Keyboard Properties
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize layout properties
        mParentLayout = (CoordinatorLayout) findViewById(R.id.parentLayout);
        mDollarsEditText = (EditText) findViewById(R.id.etDollars);
        mCentsEditText = (EditText) findViewById(R.id.etCents);
        mCurrencyCodeSpinner = (Spinner) findViewById(R.id.spinCurrencyCode);
        mGoButton = (Button) findViewById(R.id.bGo);
        mRecyclerViewCurrencies = (RecyclerView) findViewById(R.id.rvCurrencies);

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

        // Setup Web Client
        mApiService = ApiClient.getClient().create(ApiInterface.class);

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

        Observable<String> amountTextChangeStream = Observable.merge(createDollarsTextChangeObservable(), createCentsTextChangeObservable());
        Observable<String> currencyCodeChangeStream = createCurrencyCodeSpinnerObservable();

        Observable<String> inputChangeStream = Observable
                .merge(createDollarsTextChangeObservable(), createCentsTextChangeObservable())
                .mergeWith(createCurrencyCodeSpinnerObservable())
                .mergeWith(createGoButtonClickObservable());

        _disposable = inputChangeStream
                // On the UI Thread
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        // TODO Remove?
                    }
                })
                // On the IO thread
                .observeOn(Schedulers.io())
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String s) throws Exception {
                        Log.d(TAG, "Map Values: " + s);

                        Observable<ExchangeRatesResponse> call;

                        // Do network call to fetch rates
                        if (mBaseCurrencySelected == null) {
                            // Default to CAD
                            call = mApiService.getLatestExchangeRates("CAD");
                        } else {
                            call = mApiService.getLatestExchangeRates(mBaseCurrencySelected);
                        }

                        call.subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<ExchangeRatesResponse>() {

                                    ExchangeRatesResponse data;

                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        Log.d(TAG, "On Subscribe");

                                        // TODO Show a progress bar while doing work
                                        Log.d(TAG, "TODO: Show Progress Bar");

                                    }

                                    @Override
                                    public void onNext(ExchangeRatesResponse value) {
                                        Log.d(TAG, "Exchange Rates Successfully Received");
                                        data = value;

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // Handle network errors
                                        if (e instanceof UnknownHostException) {
                                            Snackbar snackbar = Snackbar
                                                    .make(mParentLayout, "Check Internet Connection!", Snackbar.LENGTH_LONG);

                                            View sbView = snackbar.getView();
                                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                            snackbar.show();
                                        }
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d(TAG, "On Complete");
                                        int dollars = 0, cents = 0;
                                        double combinedAmount;

                                        if (mDollarsEditText != null && !TextUtils.isEmpty(mDollarsEditText.getText().toString())) {
                                            try {
                                                dollars = Integer.parseInt(mDollarsEditText.getText().toString());
                                            } catch (NumberFormatException e) {
                                                mDollarsEditText.setError("Please enter an amount between 0 and " + Integer.MAX_VALUE);
                                            }

                                        }

                                        if (mCentsEditText != null && !TextUtils.isEmpty(mCentsEditText.getText().toString())) {
                                            try {
                                                cents = Integer.parseInt(mCentsEditText.getText().toString());
                                            } catch (NumberFormatException e) {
                                                mDollarsEditText.setError("Please enter an amount between 0 and " + Integer.MAX_VALUE);
                                            }
                                        }

                                        // Assumption: Cents will only have 2 decimal places.  We could have more, we just have to do extra handling.
                                        combinedAmount = dollars + cents/100.0;

                                        mRecyclerViewAdapter.clearRates();

                                        mRecyclerViewAdapter.setRates(data.getRates(), combinedAmount);

                                        Log.d(TAG, "TODO: Hide Progress Bar");
                                        // TODO Hide progress bar
                                    }
                                });

                        return true;
                    }
                })
                // On the UI Thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {
                        // TODO Remove?
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (_disposable != null && !_disposable.isDisposed()) {
            _disposable.dispose();
        }
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
