package com.browser.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.browser.BrowserSdk;
import com.browser.R;
import com.browser.util.BrowserConstant;
import com.browser.views.VideoEnabledWebChromeClient;
import com.browser.views.VideoEnabledWebView;
import com.theartofdev.edmodo.cropper.CropImage;


public class BrowserActivity extends BaseToolbarActivity {

    private static final String TAG = "BrowserActivity";
    private String url;
    public ProgressBar progressBar;
//    public RelativeLayout container;
    public VideoEnabledWebView webView;
    private String title;
    private boolean isRemoveHeaderFooter = false;
    private VideoEnabledWebChromeClient webChromeClient;
    private Toolbar toolbar;
    private View layoutInternetError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_activity);

        initDataFromIntent();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (!TextUtils.isEmpty(title)) {
                getSupportActionBar().setTitle(title);
            }
        }
        BrowserSdk.loadBanner(findViewById(R.id.rlBannerAds), this);
        toolbar = findViewById(R.id.toolbar);
        setUpToolBar(toolbar, title);
    }


    @Override
    public void onPause() {
        webView.onPause();
        webView.pauseTimers();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.resumeTimers();
        webView.onResume();
    }

    private ValueCallback<Uri[]> mFilePathCallback;


    private void initDataFromIntent() {
        progressBar = findViewById(R.id.progressBar);
        layoutInternetError = findViewById(R.id.layout_internet_error);
        (findViewById(R.id.btn_refresh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateErrorUi(false);
                loadUrl();
            }
        });
//        container = findViewById(R.id.container);
        Intent intent = getIntent();

        if (intent.hasExtra(BrowserConstant.WEB_VIEW_URL)) {
            url = intent.getStringExtra(BrowserConstant.WEB_VIEW_URL);
        }
        if (intent.hasExtra(BrowserConstant.TITLE)) {
            title = intent.getStringExtra(BrowserConstant.TITLE);
        }
        if (intent.hasExtra(BrowserConstant.IS_REMOVE_HEADER_FOOTER)) {
            isRemoveHeaderFooter = intent.getBooleanExtra(BrowserConstant.IS_REMOVE_HEADER_FOOTER, false);
        }
        if (TextUtils.isEmpty(url)) {
            BrowserSdk.showToast(this, "Invalid Url");
            finish();
            return;
        }

        webView = findViewById(R.id.webView);
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

        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
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
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    //noinspection all
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    toolbar.setVisibility(View.GONE);
                } else {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    //noinspection all
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
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
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        BrowserSdk.showToast(BrowserActivity.this, e.getMessage());
                    }
                    return true;
                }
                if (url.contains("geo:") || url.contains("google.com/maps/")) {
                    try {
                        Uri gmmIntentUri = Uri.parse(url);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        BrowserSdk.showToast(BrowserActivity.this, e.getMessage());
                    }
                    return true;
                }
                if (url.endsWith("viewer.action=download")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return true;
                }

                if (isUrlIntentType(url) || isUrlWhatsAppType(url) || isUrlTelegramType(url) || isUrlFbMessengerType(url)) {
                    BrowserSdk.openIntentUrl(BrowserActivity.this, url);
                    view.stopLoading();
                    progressBar.setVisibility(View.GONE);
                    return true;
                }
                if (isUrlFacebookType(url) || isUrlTwitterType(url)) {
                    BrowserSdk.openUrlExternal(BrowserActivity.this, url);
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
                webView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
//                if (!isUrlPdfType(url))
//                    view.loadUrl("javascript:console.log('" + TAG + "'+document.getElementsByTagName('html')[0].innerHTML);");
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                updateErrorUi(true);
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                updateErrorUi(true);
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
        webView.getSettings().setAppCachePath(this.getApplicationContext()
                .getCacheDir().getAbsolutePath());
        loadUrl();
    }

    private void updateErrorUi(boolean isVisible) {
        if (layoutInternetError != null) {
            layoutInternetError.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    private void loadUrl() {
        if (webView != null && !TextUtils.isEmpty(url)) {
            webView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            webView.loadUrl(url);
        }else {
            Toast.makeText(this, BrowserConstant.ERROR_MESSAGE_INVALID_URL, Toast.LENGTH_SHORT).show();
        }
    }


    private void openFileChooser() {
        CropImage.startPickImageActivity(BrowserActivity.this);
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

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = CropImage.getPickImageResultUri(this, data);

                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    mCropImageUri = imageUri;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
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
            if (resultCode == RESULT_OK) {
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

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
//                .setAspectRatio(1,1)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                BrowserSdk.showToast(this, "Cancelling, required permissions are not granted");
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                BrowserSdk.showToast(this, "Cancelling, required permissions are not granted");
            }
        }
    }

    private Uri mCropImageUri;

}