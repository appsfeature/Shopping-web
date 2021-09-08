package com.browser.interfaces;

import android.webkit.WebView;

public interface UrlOverloadingListener {
    void onOverrideUrlLoading(WebView view, String url);
}