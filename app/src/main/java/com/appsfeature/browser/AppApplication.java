package com.appsfeature.browser;

import android.app.Application;
import android.util.Log;
import android.webkit.WebView;

import com.browser.BrowserSdk;
import com.browser.interfaces.UrlOverloadingListener;

import java.util.ArrayList;
import java.util.List;

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
        List<String> urlOverloadingList = new ArrayList<>();
        urlOverloadingList.add("https://stackoverflow.com/tags");

        BrowserSdk.getInstance()
                .setDebugMode(BuildConfig.DEBUG)
                .setEnableDeveloperMode(this, false)
                .setEnableInternetErrorViewOnly(this, false)
                .setEnableErrorLayoutOverlay(true)
                .addUrlOverloadingListener(this.hashCode(), urlOverloadingList, new UrlOverloadingListener() {
                    @Override
                    public void onOverrideUrlLoading(WebView view, String url) {
                        Log.d("@Hammpy", "url" + url);
                    }
                });
//        BrowserSdk.getInstance().setCallback(new BrowserCallback() {
//            @Override
//            public void onOpenPdf(Activity activity, String url) {
//                Log.d("PDF", url);
//            }
//        });
    }

}
