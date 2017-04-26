package com.example.ken.worldcurrencyconverter;

import android.app.Application;
import android.content.Context;

/**
 * Created by ken on 2017-04-26.
 */

public class ApplicationController extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        ApplicationController.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ApplicationController.context;
    }
}
