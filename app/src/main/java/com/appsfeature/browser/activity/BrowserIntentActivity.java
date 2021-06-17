package com.appsfeature.browser.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.appsfeature.browser.R;
import com.appsfeature.browser.util.AppConstant;
import com.browser.BrowserSdk;


public class BrowserIntentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intent_open);
	}

	public void onOpenUrl(View view) {
		BrowserSdk.open(BrowserIntentActivity.this, "Live Tv", AppConstant.BASE_URL, false);
	}
}
