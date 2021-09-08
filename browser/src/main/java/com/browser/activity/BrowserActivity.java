package com.browser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.browser.BrowserSdk;
import com.browser.R;
import com.browser.browser.BrowserWebView;
import com.browser.interfaces.BrowserListener;
import com.browser.util.BrowserConstant;


public class BrowserActivity extends BaseToolbarActivity {

    private ProgressBar progressBar;
    private Toolbar toolbar;
    private BrowserWebView webView;
    private String url, title;
    private boolean isRemoveHeaderFooter;
    private boolean isDisableExtraError;
    private boolean isEmbedPdf;
    private boolean isOpenPdfInWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_activity);

        initDataFromIntent();
        setupToolbar();

        BrowserSdk.loadBanner(findViewById(R.id.rlBannerAds), this);

        loadUi();
    }

    private void loadUi() {
        progressBar = findViewById(com.browser.R.id.progressBar);

        webView = new BrowserWebView(this);
        webView.init(this);
        webView.setRemoveHeaderFooter(isRemoveHeaderFooter);
        webView.setDisableExtraError(isDisableExtraError);
        webView.setEmbedPDF(isEmbedPdf);
        webView.setOpenPdfInWebView(isOpenPdfInWebView);
        webView.addBrowserListener(new BrowserListener() {
            @Override
            public void onToolbarVisibilityUpdate(int isVisible) {
                if (toolbar != null) {
                    toolbar.setVisibility(isVisible);
                }
            }

            @Override
            public void onProgressBarUpdate(int isVisible) {
                if (progressBar != null) {
                    progressBar.setVisibility(isVisible);
                }
            }
        });

        if (TextUtils.isEmpty(url)) {
            BrowserSdk.showToast(this, "Invalid Url");
            finish();
            return;
        }
        webView.loadUrl(url);
    }

    private void initDataFromIntent() {
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
        if (intent.hasExtra(BrowserConstant.IS_EMBED_PDF)) {
            isEmbedPdf = intent.getBooleanExtra(BrowserConstant.IS_EMBED_PDF, false);
        }
        if (intent.hasExtra(BrowserConstant.IS_OPEN_PDF_IN_WEBVIEW)) {
            isOpenPdfInWebView = intent.getBooleanExtra(BrowserConstant.IS_OPEN_PDF_IN_WEBVIEW, false);
        }
        if (intent.hasExtra(BrowserConstant.IS_DISABLE_EXTRA_ERROR)) {
            isDisableExtraError = intent.getBooleanExtra(BrowserConstant.IS_DISABLE_EXTRA_ERROR, false);
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!TextUtils.isEmpty(title)) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(title);
                toolbar.setVisibility(View.VISIBLE);
            }
        }else {
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onBackPressed() {
        if(webView.isWebViewClosedAllPages()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        webView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}