package com.browser.activity;

import android.content.Context;
import android.webkit.WebView;

import com.browser.BrowserSdk;
import com.browser.interfaces.BrowserCallback;
import com.browser.interfaces.OverrideType;
import com.browser.interfaces.UrlOverloadingListener;
import com.browser.util.BrowserPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowserSdkClass implements BrowserSdk {

    private static volatile BrowserSdkClass browserSdk;
    private BrowserCallback mCallback;
    private final List<String> urlOverloadingList = new ArrayList<>();
    private boolean isEnableDebugMode = false;
    private int mCameraCompressQuality = 20;
    private boolean isEnableErrorLayoutOverlay = true;

    @Override
    public List<String> getUrlOverloadingList() {
        return urlOverloadingList;
    }

    private BrowserSdkClass() {
    }

    public static BrowserSdkClass getInstance() {
        if (browserSdk == null) {
            synchronized (BrowserSdkClass.class) {
                if (browserSdk == null) browserSdk = new BrowserSdkClass();
            }
        }
        return browserSdk;
    }

    @Override
    public boolean isEnableDebugMode() {
        return isEnableDebugMode;
    }

    /**
     * @param isDebug = BuildConfig.DEBUG
     * @return this
     */
    @Override
    public BrowserSdkClass setDebugMode(Boolean isDebug) {
        isEnableDebugMode = isDebug;
        return this;
    }

    @Override
    public BrowserCallback getCallback() {
        return mCallback;
    }

    @Override
    public BrowserSdkClass setCallback(BrowserCallback mCallback) {
        this.mCallback = mCallback;
        return this;
    }

    @Override
    public void clearUrlOverloadingList() {
        clearUrlOverloadingList(null);
    }

    @Override
    public void clearUrlOverloadingList(List<String> urlOverloadingRemoveList) {
        if(urlOverloadingRemoveList != null) {
            try {
                for (String item : urlOverloadingRemoveList){
                    for (int i = 0; i < urlOverloadingList.size(); i++){
                        if(item.equalsIgnoreCase(urlOverloadingList.get(i))){
                            urlOverloadingList.remove(i);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            this.urlOverloadingList.clear();
        }
    }

    private final HashMap<Integer, UrlOverloadingListener> mUrlOverloadingListener = new HashMap<>();

    @Override
    public BrowserSdkClass addUrlOverloadingListener(int hashCode, List<String> urlOverloadingList, UrlOverloadingListener callback) {
        synchronized (mUrlOverloadingListener) {
            this.urlOverloadingList.addAll(urlOverloadingList);
            this.mUrlOverloadingListener.put(hashCode, callback);
        }
        return this;
    }

    @Override
    public void removeUrlOverloadingListener(int hashCode) {
        if (mUrlOverloadingListener.get(hashCode) != null) {
            synchronized (mUrlOverloadingListener) {
                this.mUrlOverloadingListener.remove(hashCode);
            }
        }
    }

    @Override
    public void dispatchUrlOverloadingListener(WebView webView, String url, int overrideType) {
        try {
            if (mUrlOverloadingListener.size() > 0) {
                for (Map.Entry<Integer, UrlOverloadingListener> entry : mUrlOverloadingListener.entrySet()) {
                    UrlOverloadingListener callback = entry.getValue();
                    if (callback != null) {
                        switch (overrideType){
                            case OverrideType.OverrideUrlLoading:
                                callback.onOverrideUrlLoading(webView, url);
                                break;
                            case OverrideType.LoadUrl:
                                callback.onLoadUrl(url);
                                break;
                            case OverrideType.PageFinished:
                                callback.onPageFinished(webView, url);
                                break;
                            case OverrideType.ReceivedError:
                                callback.onReceivedError(webView);
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @apiNote : use to hide internet error view.
     */
    @Override
    public BrowserSdkClass setEnableDeveloperMode(Context context, boolean isEnable) {
        BrowserPreferences.setEnableDeveloperMode(context, isEnable);
        return this;
    }

    @Override
    public BrowserSdkClass setEnableInternetErrorViewOnly(Context context, boolean isEnable) {
        BrowserPreferences.setEnableInternetErrorViewOnly(context, isEnable);
        return this;
    }

    /**
     * @apiNote :the quality (if applicable) to use when writing the image (0 - 100).
     * Default: 20
     */
    @Override
    public int getCameraCompressQuality() {
        return mCameraCompressQuality;
    }

    @Override
    public BrowserSdkClass setCameraCompressQuality(int cameraCompressQuality) {
        this.mCameraCompressQuality = cameraCompressQuality;
        return this;
    }

    @Override
    public boolean isEnableErrorLayoutOverlay() {
        return isEnableErrorLayoutOverlay;
    }

    @Override
    public BrowserSdkClass setEnableErrorLayoutOverlay(boolean enableExtraError) {
        this.isEnableErrorLayoutOverlay = enableExtraError;
        return this;
    }
}
