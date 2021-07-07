package com.appsfeature.browser;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.browser.BrowserSdk;
import com.browser.interfaces.BrowserCallback;

public class AppApplication extends Application {


    private static final String HOST_URL = "https://bizwiz.co.in/Api/";
    private static AppApplication instance;

    public static AppApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BrowserSdk.getInstance().setCallback(new BrowserCallback() {
            @Override
            public void onOpenPdf(Activity activity, String url) {
                Log.d("PDF", url);
            }
        });
    }

}
