package com.browser.browser;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.browser.BrowserSdk;
import com.browser.R;
import com.browser.interfaces.BrowserListener;
import com.browser.views.VideoEnabledWebChromeClient;
import com.browser.views.VideoEnabledWebView;
import com.theartofdev.edmodo.cropper.CropImage;


public class BrowserWebView {

    private static final String TAG = "BrowserActivity";
    private final Activity activity;
    private VideoEnabledWebView webView;
    private boolean isRemoveHeaderFooter = false;
    private boolean isFixCropRatio = false;
    private BrowserListener callback;
    private View layoutInternetError;
    private String mUrl;
    private boolean isDisableExtraError = false;

    public BrowserWebView(Activity activity) {
        this.activity = activity;
    }

    public BrowserWebView addBrowserListener(BrowserListener callback) {
        this.callback = callback;
        return this;
    }

    public BrowserWebView setRemoveHeaderFooter(boolean removeHeaderFooter) {
        isRemoveHeaderFooter = removeHeaderFooter;
        return this;
    }

    public boolean isDisableExtraError() {
        return isDisableExtraError;
    }

    public BrowserWebView setDisableExtraError(boolean disableExtraError) {
        isDisableExtraError = disableExtraError;
        return this;
    }

    public BrowserWebView setFixCropRatio(boolean fixCropRatio) {
        isFixCropRatio = fixCropRatio;
        return this;
    }

    private ValueCallback<Uri[]> mFilePathCallback;


    public void init(Activity activity) {
        View rootView = activity.getWindow().getDecorView().getRootView();
        initView(rootView);
    }

    public void init(View view) {
        initView(view);
    }

    public void loadUrl(String url) {
        this.mUrl = url;
        if (webView == null || TextUtils.isEmpty(url)) {
            BrowserSdk.showToast(activity, "Invalid Url");
            activity.finish();
            return;
        }
        webView.setVisibility(View.INVISIBLE);
        if (callback != null) {
            callback.onProgressBarUpdate(View.VISIBLE);
        }
        webView.loadUrl(url);
    }

    private void initView(View rootView) {
        webView = rootView.findViewById(R.id.webView);
        layoutInternetError = rootView.findViewById(R.id.layout_internet_error);
        (rootView.findViewById(R.id.btn_refresh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateErrorUi(false);
                loadUrl(mUrl);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;
                openFileChooser();
                return true;//; return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                super.onConsoleMessage(consoleMessage);
                if (consoleMessage.message().startsWith(TAG)) {
                    if (consoleMessage.message().toLowerCase().contains("viewable only") && consoleMessage.message().toLowerCase().contains("landscape")) {
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }
                return false;
            }
        });

        View nonVideoLayout = rootView.findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) rootView.findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = activity.getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        // See all available constructors...
        // Subscribe to standard events, such as onProgressChanged()...
        // Your code...
        VideoEnabledWebChromeClient webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Your code...
            }
        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    activity.getWindow().setAttributes(attrs);
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    if (callback != null) {
                        callback.onToolbarVisibilityUpdate(View.GONE);
                    }
                } else {
                    WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    activity.getWindow().setAttributes(attrs);
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (callback != null) {
                        callback.onToolbarVisibilityUpdate(View.VISIBLE);
                    }
                }
            }
        });
        webView.setWebChromeClient(webChromeClient);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                super.shouldOverrideUrlLoading(view, request);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String requestUrl = request.getUrl().toString();
                    return filterUrl(view, requestUrl);
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    return filterUrl(view, url);
                }
                return false;
            }

            private boolean filterUrl(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL,
                                Uri.parse(url));
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        BrowserSdk.showToast(activity, e.getMessage());
                    }
                    return true;
                }
                if (url.contains("geo:") || url.contains("google.com/maps/")) {
                    try {
                        Uri gmmIntentUri = Uri.parse(url);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivity(mapIntent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        BrowserSdk.showToast(activity, e.getMessage());
                    }
                    return true;
                }
                if (isUrlPdfType(url)) {
                    openPDF(url);
                    return true;
                }
                if (url.endsWith("viewer.action=download")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    activity.startActivity(i);
                    return true;
                }

                if (isUrlIntentType(url) || isUrlWhatsAppType(url) || isUrlTelegramType(url) || isUrlFbMessengerType(url)) {
                    BrowserSdk.openIntentUrl(activity, url);
                    view.stopLoading();
                    if (callback != null) {
                        callback.onProgressBarUpdate(View.GONE);
                    }
                    return true;
                }
                if (isUrlFacebookType(url) || isUrlTwitterType(url)) {
                    BrowserSdk.openUrlExternal(activity, url);
                    view.stopLoading();
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                setHideGoogleTranslatorHeaderJavaScript(view);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                if (callback != null) {
                    callback.onProgressBarUpdate(View.GONE);
                }
                if (webView != null) {
                    webView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (callback != null) {
                    callback.onProgressBarUpdate(View.GONE);
                }
                if (webView != null) {
                    webView.setVisibility(View.VISIBLE);
                }
//                if (!isUrlPdfType(url))
//                    view.loadUrl("javascript:console.log('" + TAG + "'+document.getElementsByTagName('html')[0].innerHTML);");
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                if(isDisableExtraError){
                    updateErrorUi(true);
                }
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if(isDisableExtraError){
                    updateErrorUi(true);
                }
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                updateErrorUi(true);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                updateErrorUi(true);
                super.onReceivedError(view, request, error);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setAppCachePath(activity.getApplicationContext()
                .getCacheDir().getAbsolutePath());
    }

    private void openPDF(String mUrl) {
        if (!TextUtils.isEmpty(mUrl)) {
            if (BrowserSdk.getInstance().getCallback() != null) {
                BrowserSdk.getInstance().getCallback().onOpenPdf(activity, mUrl);
            } else {
                BrowserSdk.openUrlExternal(activity, mUrl);
            }
        }
    }

    private void updateErrorUi(boolean isVisible) {
        if (layoutInternetError != null) {
            layoutInternetError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }


    private void openFileChooser() {
        CropImage.startPickImageActivity(activity);
    }


    private void setHideGoogleTranslatorHeaderJavaScript(WebView view) {
        try {
            if (isRemoveHeaderFooter) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript(hideGoogleTranslatorHeaderJavaScript, null);
                    view.evaluateJavascript(hideGoogleTranslatorFooterJavaScript, null);
                } else {
                    view.loadUrl("javascript:"
                            + "var FunctionOne = function () {"
                            + "  try{" + hideGoogleTranslatorHeaderJavaScript + "}catch(e){}"
                            + "};");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String hideGoogleTranslatorHeaderJavaScript = "document.getElementsByTagName('header')[0].style.display = 'none'";
    //    private String hideGoogleTranslatorHeaderJavaScript = "document.getElementById('home').remove()";
    private static final String hideGoogleTranslatorFooterJavaScript = "document.getElementById('footer').remove()";


    private boolean isUrlPdfType(String url) {
        return url.toLowerCase().endsWith(".pdf");
    }

    private boolean isUrlIntentType(String url) {
        return url.toLowerCase().startsWith("intent://");
    }

    /**
     * @param url https://www.facebook.com/sharer.php?t=
     */
    private boolean isUrlFacebookType(String url) {
        return url.toLowerCase().startsWith("https://www.facebook.com");
    }

    /**
     * @param url whatsapp://send?text=
     */
    private boolean isUrlWhatsAppType(String url) {
        return url.toLowerCase().startsWith("whatsapp://");
    }

    /**
     * @param url tg:msg_url?url=
     */
    private boolean isUrlTelegramType(String url) {
        return url.toLowerCase().startsWith("tg:msg_url");
    }

    /**
     * @param url https://twitter.com/intent/tweet?text=
     */
    private boolean isUrlTwitterType(String url) {
        return url.toLowerCase().startsWith("https://twitter.com");
    }

    /**
     * @param url fb-messenger://share/?link=
     */
    private boolean isUrlFbMessengerType(String url) {
        return url.toLowerCase().startsWith("fb-messenger://");
    }

    private Uri mCropImageUri;

    private void startCropImageActivity(Uri imageUri) {
        if (isFixCropRatio) {
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(activity);
        } else {
            CropImage.activity(imageUri)
                    .start(activity);
        }
    }

    @MainThread
    public void onPause() {
        webView.onPause();
        webView.pauseTimers();
    }

    @MainThread
    public void onResume() {
        webView.resumeTimers();
        webView.onResume();
    }

    @MainThread
    public boolean isWebViewClosedAllPages() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return false;
        } else {
            return true;
        }
    }

    @MainThread
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = CropImage.getPickImageResultUri(activity, data);

                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (CropImage.isReadExternalStoragePermissionsRequired(activity, imageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    mCropImageUri = imageUri;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                    }
                } else {
                    // no permissions required or already grunted, can start crop image activity
                    startCropImageActivity(imageUri);
                }
            } else {
                mFilePathCallback.onReceiveValue(null);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                mCropImageUri = result.getUri();
//                imagePath = mCropImageUri.getPath();
//                setImage(mCropImageUri.toString());
                mFilePathCallback.onReceiveValue(new Uri[]{mCropImageUri});
            } else {
                mFilePathCallback.onReceiveValue(null);
            }
        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                mFilePathCallback.onReceiveValue(null);
            }
        }
    }

    @MainThread
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(activity);
            } else {
                BrowserSdk.showToast(activity, "Cancelling, required permissions are not granted");
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                BrowserSdk.showToast(activity, "Cancelling, required permissions are not granted");
            }
        }
    }

}