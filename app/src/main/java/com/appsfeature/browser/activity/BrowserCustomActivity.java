package com.appsfeature.browser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.appsfeature.browser.R;
import com.appsfeature.browser.util.AppConstant;
import com.browser.browser.BrowserWebView;
import com.browser.interfaces.BrowserListener;

public class BrowserCustomActivity extends AppCompatActivity {


    private ProgressBar progressBar;
    private Toolbar toolbar;
    private BrowserWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        setupToolbar();

        progressBar = findViewById(com.browser.R.id.progressBar);

        webView = new BrowserWebView(this);
        webView.init(this);
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

        webView.loadUrl(AppConstant.BASE_URL);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
