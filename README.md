## Browser
  
## Setup 
Add this to your project build.gradle
``` gradle
allprojects {
    repositories {
        maven {
            url "https://jitpack.io"
        }
    }
}
```

#### Dependency
[![](https://jitpack.io/v/appsfeature/browser.svg)](https://jitpack.io/#appsfeature/browser)
```gradle
dependencies {
        implementation 'com.github.appsfeature:browser:2.1'
}
```
#### Usage  
```java
    // Open url method
    BrowserSdk.open(this, "Title", AppConstant.REQUEST_URL);

    // Override Url method
    List<String> urlOverloadingList = new ArrayList<>();
    urlOverloadingList.add("https://stackoverflow.com/tags");

    BrowserSdk.getInstance().addUrlOverloadingListener(this.hashCode(), urlOverloadingList, new UrlOverloadingListener() {
        @Override
        public void onOverrideUrlLoading(WebView view, String url) {
            Log.d("@Hammpy", "url" + url);
        }
    });

    // Getting clicked pdf url
    BrowserSdk.getInstance().setCallback(new BrowserCallback() {
        @Override
        public void onOpenPdf(Activity activity, String url) {
            Log.d("PDF", url);
        }
    });
```

# Custom Usage
#### changes in your AndroidManifest.xml file
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.browser">

    <application>
	    ...
	    ...
        <activity android:name=".YourActivity"
            android:configChanges="orientation|screenSize"/>
    </application>
</manifest>

```

#### changes in your xml file
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingTop="8dp"
        android:theme="@style/AppTheme.AppBarOverlay">

        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:layout_scrollFlags="scroll|exitUntilCollapsed"
            app:popupTheme="@style/AppTheme.PopupOverlay">

        </androidx.appcompat.widget.Toolbar>


    </com.google.android.material.appbar.AppBarLayout>

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <include layout="@layout/browser_layout_web_view"/>

        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="45dp"
            android:layout_height="45dp"
            android:layout_centerInParent="true" />

    </RelativeLayout>


</LinearLayout>

```

#### changes in your java file
```java
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.browser.BrowserSdk;
import com.browser.activity.BaseToolbarActivity;
import com.browser.browser.BrowserWebView;
import com.browser.interfaces.BrowserListener;
import com.browser.util.BrowserConstant;

public class AppBrowserActivity extends BaseToolbarActivity {

    private ProgressBar progressBar;
    private Toolbar toolbar;
    private BrowserWebView webView;
    private String url, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_activity);
        setupToolbar();
        initDataFromIntent();

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
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (title != null) {
                getSupportActionBar().setTitle(title);
            }
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
```
 
