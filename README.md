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
        implementation 'com.github.appsfeature:browser:1.3'
}
```
#### Usage  
```java
    BrowserSdk.open(this, "Title", AppConstant.REQUEST_URL);
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
```
 
