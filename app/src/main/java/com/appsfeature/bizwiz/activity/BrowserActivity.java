package com.appsfeature.bizwiz.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.appsfeature.bizwiz.R;
import com.appsfeature.bizwiz.util.AppConstant;
import com.helper.util.BaseConstants;
import com.helper.util.BaseUtil;
import com.helper.util.SocialUtil;
import com.theartofdev.edmodo.cropper.CropImage;


public class BrowserActivity extends BaseToolbarActivity {

    private String TAG = "BrowserActivity";
    private String url;
    public ProgressBar progressBar;
    public RelativeLayout container;
    public WebView webView;
    private String title;
    private boolean isRemoveHeaderFooter = false;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        initDataFromIntent();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (!TextUtils.isEmpty(title)) {
                getSupportActionBar().setTitle(title);
            }
        }
        BaseUtil.loadBanner(findViewById(R.id.rlBannerAds), this);
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
        container = findViewById(R.id.container);
        Intent intent = getIntent();

        if (intent.hasExtra(BaseConstants.WEB_VIEW_URL)) {
            url = intent.getStringExtra(BaseConstants.WEB_VIEW_URL);
        }
        if (intent.hasExtra(BaseConstants.TITLE)) {
            title = intent.getStringExtra(BaseConstants.TITLE);
        }
        if (intent.hasExtra(AppConstant.IS_REMOVE_HEADER_FOOTER)) {
            isRemoveHeaderFooter = intent.getBooleanExtra(AppConstant.IS_REMOVE_HEADER_FOOTER, false);
        }
        if (TextUtils.isEmpty(url)) {
            BaseUtil.showToast(this, "Invalid Url");
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

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                super.shouldOverrideUrlLoading(view, request);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String requestUrl = request.getUrl().toString();
                    if (url.startsWith("tel:")) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL,
                                    Uri.parse(url));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            BaseUtil.showToast(BrowserActivity.this, e.getMessage());
                        }
                        return false;
                    }
                    if(url.contains("geo:") || url.contains("google.com/maps/")) {
                        try {
                            Uri gmmIntentUri = Uri.parse(url);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            BaseUtil.showToast(BrowserActivity.this, e.getMessage());
                        }
                        return false;
                    }
                    if (request.getUrl().toString().endsWith("viewer.action=download")) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(requestUrl));
                        startActivity(i);
                        return false;
                    }
                    if (isUrlIntentType(request.getUrl().toString())) {
                        SocialUtil.openIntentUrl(BrowserActivity.this, request.getUrl().toString());
                        webView.stopLoading();
                        progressBar.setVisibility(View.GONE);
                        webView.goBack();
                        return false;
                    }
                    view.loadUrl(request.getUrl().toString());
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    if (url.endsWith("viewer.action=download")) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        return false;
                    }

                    if (isUrlIntentType(url)) {
                        SocialUtil.openIntentUrl(BrowserActivity.this, url);
                        progressBar.setVisibility(View.GONE);
                        webView.stopLoading();
                        webView.goBack();
                        return false;
                    }
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                setHideGoogleTranslatorHeaderJavaScript(view);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
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
        webView.loadUrl(url);
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

    private String hideGoogleTranslatorHeaderJavaScript = "document.getElementsByTagName('header')[0].style.display = 'none'";
    //    private String hideGoogleTranslatorHeaderJavaScript = "document.getElementById('home').remove()";
    private String hideGoogleTranslatorFooterJavaScript = "document.getElementById('footer').remove()";


    private boolean isUrlIntentType(String url) {
        return url.toLowerCase().startsWith("intent://");
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
                BaseUtil.showToast(this, "Cancelling, required permissions are not granted");
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                BaseUtil.showToast(this, "Cancelling, required permissions are not granted");
            }
        }
    }

    private Uri mCropImageUri;

}