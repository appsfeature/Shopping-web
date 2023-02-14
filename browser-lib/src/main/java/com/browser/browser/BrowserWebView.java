package com.browser.browser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
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
import com.browser.interfaces.OverrideType;
import com.browser.util.BrowserFileUtil;
import com.browser.util.BrowserLogger;
import com.browser.util.BrowserUtil;
import com.browser.views.VideoEnabledWebChromeClient;
import com.browser.views.VideoEnabledWebView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;


public class BrowserWebView {

    private static final String TAG = "BrowserActivity";
    private static final int CAMERA_REQUEST_CODE = 205;
    private static final int FILE_CHOOSER_REQUEST_CODE = 206;
    private final Activity activity;
    private VideoEnabledWebView webView;
    private boolean isRemoveHeaderFooter = false;
    private boolean isFixCropRatio = false;
    private BrowserListener callback;
    private View layoutInternetError;
    private String mUrl;
    private boolean isEnableExtraError = false;
    private boolean isEmbedPdf = false;
    private boolean isOpenPdfInWebView = false;
    private boolean isDisableBackButtonHistory = false;
    private Uri mfileUri;

    public BrowserWebView(Activity activity) {
        this.activity = activity;
    }

    public BrowserWebView addBrowserListener(BrowserListener callback) {
        this.callback = callback;
        return this;
    }

    public BrowserWebView setEmbedPDF(boolean isEmbedPdf) {
        this.isEmbedPdf = isEmbedPdf;
        return this;
    }

    public BrowserWebView setOpenPdfInWebView(boolean isOpenPdfInWebView) {
        this.isOpenPdfInWebView = isOpenPdfInWebView;
        return this;
    }

    public BrowserWebView setRemoveHeaderFooter(boolean removeHeaderFooter) {
        this.isRemoveHeaderFooter = removeHeaderFooter;
        return this;
    }

    public boolean isEnableExtraError() {
        return BrowserSdk.getInstance().isEnableErrorLayoutOverlay() && isEnableExtraError;
    }

    public BrowserWebView setEnableExtraError(boolean enableExtraError) {
        this.isEnableExtraError = enableExtraError;
        return this;
    }

    public BrowserWebView setFixCropRatio(boolean fixCropRatio) {
        this.isFixCropRatio = fixCropRatio;
        return this;
    }

    public BrowserWebView setDisableBackButtonHistory(boolean disableBackButtonHistory) {
        isDisableBackButtonHistory = disableBackButtonHistory;
        return this;
    }

    private ValueCallback<Uri[]> mFilePathCallback;


    public BrowserWebView init(Activity activity) {
        View rootView = activity.getWindow().getDecorView().getRootView();
        initView(rootView);
        return this;
    }

    public void init(View view) {
        initView(view);
    }

    public void loadUrl(String url) {
        BrowserLogger.info("loadUrl()");
        this.mUrl = url;
        if (webView == null || TextUtils.isEmpty(url)) {
            BrowserSdk.showToast(activity, "Invalid Url");
            BrowserLogger.e("loadUrl()", "Url is null or empty");
            activity.finish();
            return;
        }
        webView.setVisibility(View.INVISIBLE);
        if (callback != null) {
            callback.onProgressBarUpdate(View.VISIBLE);
        }
        webView.loadUrl(url);
        BrowserLogger.d("loadUrl()", url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView(View rootView) {
        webView = rootView.findViewById(R.id.webView);
        layoutInternetError = rootView.findViewById(R.id.layout_internet_error);
        (rootView.findViewById(R.id.btn_refresh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateErrorUi(false, null);
                loadUrl(mUrl);
            }
        });

        View nonVideoLayout = rootView.findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) rootView.findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = activity.getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        // See all available constructors...
        // Subscribe to standard events, such as onProgressChanged()...
        // Your code...
        VideoEnabledWebChromeClient webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Your code...
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;
                openChooser();
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
                BrowserLogger.d("shouldOverrideUrlLoading()", url);
                if (BrowserSdk.getInstance().getUrlOverloadingList().size() > 0) {
                    for (String overrideUrl : BrowserSdk.getInstance().getUrlOverloadingList()) {
                        if (url.contains(overrideUrl)) {
                            BrowserSdk.getInstance().dispatchUrlOverloadingListener(view, url, OverrideType.OverrideUrlLoading);
                            return true;
                        }
                    }
                }
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
                        activity.startActivity(mapIntent);
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
                BrowserSdk.getInstance().dispatchUrlOverloadingListener(view, url, OverrideType.LoadUrl);
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
                BrowserLogger.info("onPageFinished()");
                if (callback != null) {
                    callback.onProgressBarUpdate(View.GONE);
                }
                if (webView != null) {
                    webView.setVisibility(View.VISIBLE);
                }
                BrowserSdk.getInstance().dispatchUrlOverloadingListener(view, url, OverrideType.PageFinished);
//                if (!isUrlPdfType(url))
//                    view.loadUrl("javascript:console.log('" + TAG + "'+document.getElementsByTagName('html')[0].innerHTML);");
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                String errorDetail = null;
                if (errorResponse != null) {
                    try {
                        errorDetail = errorResponse.toString();
                        BrowserLogger.info("onReceivedHttpError()", errorResponse.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (isEnableExtraError()) {
                    updateErrorUi(true, errorDetail);
                }
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                String errorDetail = null;
                if (error != null) {
                    try {
                        errorDetail = error.toString();
                        BrowserLogger.info("onReceivedSslError()", error.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (isEnableExtraError()) {
                    updateErrorUi(true, errorDetail);
                }
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (description != null) {
                    try {
                        BrowserLogger.e("onReceivedError()", "errorCode:" + errorCode, description);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                String errorDetail = description + "\n\n" + failingUrl;
                if (isEnableExtraError()) {
                    updateErrorUi(true, errorDetail);
                }
                BrowserSdk.getInstance().dispatchUrlOverloadingListener(view, "", OverrideType.ReceivedError);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                String errorDetail = null;
                if (error != null) {
                    try {
                        errorDetail =  error.toString();
                        BrowserLogger.e("onReceivedError()", error.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (isEnableExtraError()) {
                    updateErrorUi(true, errorDetail);
                }
                BrowserSdk.getInstance().dispatchUrlOverloadingListener(view, "", OverrideType.ReceivedError);
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
//        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        webView.getSettings().setAppCachePath(activity.getApplicationContext()
//                .getCacheDir().getAbsolutePath());
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

    private void updateErrorUi(boolean isVisible, String errorDetail) {
        BrowserUtil.showErrorView(layoutInternetError, isVisible, errorDetail);
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
        return url.toLowerCase().startsWith("tg:msg_url") || url.toLowerCase().startsWith("tg:join");
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
        if (!isDisableBackButtonHistory && webView != null && webView.canGoBack()) {
            webView.goBack();
            return false;
        } else {
            return true;
        }
    }

    @MainThread
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Uri imageUri = mfileUri;
                if (imageUri != null) {
                    BrowserLogger.info("onActivityResult", "CropImage.getPickImageResultUri(activity, data)", "imageUri:" + imageUri.toString());
                }
                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (CropImage.isReadExternalStoragePermissionsRequired(activity, imageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    mCropImageUri = imageUri;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                    }
                } else {
                    // no permissions required or already grunted, can start crop image activity
                    startCropImageActivity(imageUri, BrowserSdk.getInstance().getCameraCompressQuality());
                }

            } else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
                Uri imageUri = CropImage.getPickImageResultUri(activity, data);
                if (imageUri != null) {
                    BrowserLogger.info("onActivityResult", "CropImage.getPickImageResultUri(activity, data)", "imageUri:" + imageUri.toString());
                }
                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (CropImage.isReadExternalStoragePermissionsRequired(activity, imageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    mCropImageUri = imageUri;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                    }
                } else {
                    // no permissions required or already grunted, can start crop image activity
                    startCropImageActivity(imageUri, 90);
                }

            } else if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
                Uri fileUri = CropImage.getPickImageResultUri(activity, data);
                if (fileUri != null) {
                    BrowserLogger.info("onActivityResult", "CropImage.getPickImageResultUri(activity, data)", "imageUri:" + fileUri.toString());
                }
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(new Uri[]{fileUri});
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                mCropImageUri = result.getUri();
                if (mCropImageUri != null) {
                    BrowserLogger.info("onActivityResult", "result.getUri()", "mCropImageUri:" + mCropImageUri.toString());
                }
//                imagePath = mCropImageUri.getPath();
//                setImage(mCropImageUri.toString());
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(new Uri[]{mCropImageUri});
                }
            }
        } else {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            BrowserLogger.e("onActivityResult", "resultCode : Activity.RESULT_CANCELED");
        }
    }

    @MainThread
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(activity);
                BrowserLogger.info("CropImage.startPickImageActivity(activity);");
            } else {
                BrowserSdk.showToast(activity, "Cancelling, required permissions are not granted");
                BrowserLogger.e("onRequestPermissionsResult()", "Cancelling, required permissions are not granted");
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri, 90);
                BrowserLogger.info("startCropImageActivity(mCropImageUri)", "mCropImageUri:" + mCropImageUri.toString());
            } else {
                BrowserSdk.showToast(activity, "Cancelling, required permissions are not granted");
                BrowserLogger.e("onRequestPermissionsResult()", "Cancelling, required permissions are not granted");
            }
        }
    }


    private void openChooser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_camera_file_chooser, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        (dialogView.findViewById(R.id.ll_camera)).setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            openCamera();
        });
        (dialogView.findViewById(R.id.ll_gallery)).setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            openGallery();
        });
        (dialogView.findViewById(R.id.ll_folder)).setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            openFileChooser();
        });
        (dialogView.findViewById(R.id.iv_close)).setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
        });
    }

    private void openCamera() {
        try {
            File file = BrowserFileUtil.getFile(activity, "sampleCamera.png");
            mfileUri = BrowserFileUtil.getUriFromFile(activity, file);
            Intent cameraIntent = CropImage.getCameraIntent(activity, mfileUri);
            cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openGallery() {
        try {
            Intent cameraIntent = CropImage.getPickImageChooserIntent(
                    activity, activity.getString(R.string.pick_image_intent_chooser_title), false, false);
            cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivityForResult(cameraIntent, CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFileChooser() {
        try {
            String[] mimeTypes = new String[]{"image/jpeg", "image/png", "image/gif", "application/pdf"};
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            } else {
                intent.setType("*/*");
            }
            activity.startActivityForResult(Intent.createChooser(intent, "Choose file"), FILE_CHOOSER_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFileChooser2() {
        try {
//            Intent fileIntent = CropImage.getPickImageChooserIntent(
//                    activity, activity.getString(R.string.pick_image_intent_chooser_title), true, false);
//            fileIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            fileIntent.setType("*/*");
            Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            fileIntent.setType("*/*");
            Intent chooserIntent = Intent.createChooser(fileIntent, "Select File");
            activity.startActivityForResult(chooserIntent, FILE_CHOOSER_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri mCropImageUri;

    private void startCropImageActivity(Uri imageUri, int quality) {
        BrowserLogger.info("startCropImageActivity(Uri imageUri)", "imageUri:" + imageUri.toString());
        if (isFixCropRatio) {
            CropImage.activity(imageUri)
                    .setOutputCompressQuality(quality)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(activity);
        } else {
            CropImage.activity(imageUri)
                    .setOutputCompressQuality(quality)
                    .start(activity);
        }
    }
}