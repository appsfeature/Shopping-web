package com.browser.interfaces;

import android.webkit.WebView;

public interface UrlOverloadingListener {
    void onOverrideUrlLoading(WebView view, String url);

    default void onLoadUrl(String url){}

    default void onPageFinished(WebView view, String url){}

    default void onReceivedError(WebView view){ }
}