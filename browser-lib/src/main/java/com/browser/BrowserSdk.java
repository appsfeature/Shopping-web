package com.browser;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.browser.activity.BrowserSdkClass;
import com.browser.interfaces.BrowserCallback;
import com.browser.interfaces.UrlOverloadingListener;
import com.browser.player.VideoProperty;
import com.browser.util.BrowserUtil;

import java.util.List;

public interface BrowserSdk {

    List<String> getUrlOverloadingList();

    static BrowserSdk getInstance() {
        return BrowserSdkClass.getInstance();
    }

    boolean isEnableDebugMode();

    /**
     * @param isDebug = BuildConfig.DEBUG
     * @return this
     */
    BrowserSdk setDebugMode(Boolean isDebug);

    BrowserCallback getCallback();

    BrowserSdk setCallback(BrowserCallback mCallback);

    void clearUrlOverloadingList();

    void clearUrlOverloadingList(List<String> urlOverloadingRemoveList);

    BrowserSdk addUrlOverloadingListener(int hashCode, List<String> urlOverloadingList, UrlOverloadingListener callback);

    void removeUrlOverloadingListener(int hashCode);

    void dispatchUrlOverloadingListener(WebView webView, String url, int overrideType);

    /**
     * @apiNote : use to hide internet error view.
     */
    BrowserSdk setEnableDeveloperMode(Context context, boolean isEnable);

    BrowserSdk setEnableInternetErrorViewOnly(Context context, boolean isEnable);

    /**
     * @apiNote :the quality (if applicable) to use when writing the image (0 - 100).
     * Default: 20
     */
    int getCameraCompressQuality();

    BrowserSdk setCameraCompressQuality(int cameraCompressQuality);

    boolean isEnableErrorLayoutOverlay();

    BrowserSdk setEnableErrorLayoutOverlay(boolean enableExtraError);


    static void open(Context context, String title, String webUrl) {
        open(context, title, webUrl, false);
    }

    static void open(Context context, String title, String webUrl, boolean isEmbedPdf) {
        open(context, title, webUrl, isEmbedPdf, false);
    }

    static void open(Context context, String title, String webUrl, boolean isEmbedPdf, boolean isRemoveHeaderFooter) {
        BrowserClassUtil.open(context, title, webUrl, isEmbedPdf, isRemoveHeaderFooter);
    }

    static void openVideoPlayer(Context context, String videoId, String videoTitle) {
        BrowserClassUtil.openVideoPlayer(context, videoId, videoTitle);
    }

    static void openVideoPlayer(Context context, VideoProperty videoProperty) {
        BrowserClassUtil.openVideoPlayer(context, videoProperty);
    }

    static void openPDFViewer(Context context, String title, String webUrl) {
        BrowserClassUtil.openPDFViewer(context, title, webUrl);
    }

    static void hideKeyboard(Activity activity) {
        BrowserUtil.hideKeyboard(activity);
    }

    static SpannableString generateBoldTitle(String title, String boldText) {
        return BrowserUtil.generateBoldTitle(title, boldText);
    }


    static void loadBanner(final RelativeLayout view , Activity activity) {

    }

    static void showToast(Context context, String message) {
        showToastCentre(context, message);
    }

    static void showToastCentre(Context context, String msg) {
        BrowserUtil.showToastCentre(context, msg);
    }

    static void openIntentUrl(Context context, String url) {
        BrowserClassUtil.openIntentUrl(context, url);
    }

    static void openUrlExternal(Context context, String url) {
        BrowserClassUtil.openUrlExternal(context, url);
    }

}
