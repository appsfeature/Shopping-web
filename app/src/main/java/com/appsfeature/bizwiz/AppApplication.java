package com.appsfeature.bizwiz;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;

import com.helper.util.DayNightPreference;

public class AppApplication extends Application {


    private static final String HOST_URL = "https://bizwiz.co.in/Api/";
    private static AppApplication instance;

    public static AppApplication getInstance() {
        return instance;
    }
    public ViewModelProvider.Factory viewModelFactory = new ViewModelProvider.AndroidViewModelFactory(this);

    public ViewModelProvider.Factory getViewModelFactory() {
        return viewModelFactory;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DayNightPreference.setNightMode(this, false);
        instance = this;
    }

}
