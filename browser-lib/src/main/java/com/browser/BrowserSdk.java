package com.browser;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.browser.interfaces.BrowserCallback;
import com.browser.interfaces.OverrideType;
import com.browser.interfaces.UrlOverloadingListener;
import com.browser.util.BrowserPreferences;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowserSdk {

    private static volatile BrowserSdk browserSdk;
    private BrowserCallback mCallback;
    private final List<String> urlOverloadingList = new ArrayList<>();
    private boolean isEnableDebugMode = false;
    private int mCameraCompressQuality = 20;
    private boolean isEnableErrorLayoutOverlay = true;

    public List<String> getUrlOverloadingList() {
        return urlOverloadingList;
    }

    private BrowserSdk() {
    }

    public static BrowserSdk getInstance() {
        if (browserSdk == null) {
            synchronized (BrowserSdk.class) {
                if (browserSdk == null) browserSdk = new BrowserSdk();
            }
        }
        return browserSdk;
    }

    public boolean isEnableDebugMode() {
        return isEnableDebugMode;
    }

    /**
     * @param isDebug = BuildConfig.DEBUG
     * @return this
     */
    public BrowserSdk setDebugMode(Boolean isDebug) {
        isEnableDebugMode = isDebug;
        return this;
    }

    public BrowserCallback getCallback() {
        return mCallback;
    }

    public BrowserSdk setCallback(BrowserCallback mCallback) {
        this.mCallback = mCallback;
        return this;
    }

    public static void open(Context context, String title, String webUrl) {
        open(context, title, webUrl, false);
    }

    public static void open(Context context, String title, String webUrl, boolean isEmbedPdf) {
        open(context, title, webUrl, isEmbedPdf, false);
    }

    public static void open(Context context, String title, String webUrl, boolean isEmbedPdf, boolean isRemoveHeaderFooter) {
        BrowserClassUtil.open(context, title, webUrl, isEmbedPdf, isRemoveHeaderFooter);
    }

    public static void openPDFViewer(Context context, String title, String webUrl) {
        BrowserClassUtil.openPDFViewer(context, title, webUrl);
    }

    public static void hideKeyboard(Activity activity) {
        if(activity!=null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View f = activity.getCurrentFocus();
            if (null != f && null != f.getWindowToken() && EditText.class.isAssignableFrom(f.getClass()))
                imm.hideSoftInputFromWindow(f.getWindowToken(), 0);
            else
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    public static SpannableString generateBoldTitle(String title, String boldText) {
        SpannableString s = new SpannableString(title + " " + boldText);
        s.setSpan(new StyleSpan(Typeface.BOLD), title.length(), title.length() + boldText.length() + 1  , 0);
        return s;
    }


    public static void loadBanner(final RelativeLayout view , Activity activity) {

    }

    public static void showToast(Context context, String message) {
        showToastCentre(context, message);
    }

    public static void showToastCentre(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void openIntentUrl(Context context, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            context.startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void openUrlExternal(Activity activity, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void clearUrlOverloadingList() {
        clearUrlOverloadingList(null);
    }

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

    public BrowserSdk addUrlOverloadingListener(int hashCode, List<String> urlOverloadingList, UrlOverloadingListener callback) {
        synchronized (mUrlOverloadingListener) {
            this.urlOverloadingList.addAll(urlOverloadingList);
            this.mUrlOverloadingListener.put(hashCode, callback);
        }
        return this;
    }

    public void removeUrlOverloadingListener(int hashCode) {
        if (mUrlOverloadingListener.get(hashCode) != null) {
            synchronized (mUrlOverloadingListener) {
                this.mUrlOverloadingListener.remove(hashCode);
            }
        }
    }

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
    public BrowserSdk setEnableDeveloperMode(Context context, boolean isEnable) {
        BrowserPreferences.setEnableDeveloperMode(context, isEnable);
        return this;
    }

    public BrowserSdk setEnableInternetErrorViewOnly(Context context, boolean isEnable) {
        BrowserPreferences.setEnableInternetErrorViewOnly(context, isEnable);
        return this;
    }

    /**
     * @apiNote :the quality (if applicable) to use when writing the image (0 - 100).
     * Default: 20
     */
    public int getCameraCompressQuality() {
        return mCameraCompressQuality;
    }

    public BrowserSdk setCameraCompressQuality(int cameraCompressQuality) {
        this.mCameraCompressQuality = cameraCompressQuality;
        return this;
    }

    public boolean isEnableErrorLayoutOverlay() {
        return isEnableErrorLayoutOverlay;
    }

    public BrowserSdk setEnableErrorLayoutOverlay(boolean enableExtraError) {
        this.isEnableErrorLayoutOverlay = enableExtraError;
        return this;
    }
}
