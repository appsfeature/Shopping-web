package com.appsfeature.browser.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.appsfeature.browser.util.AppConstant;
import com.browser.BrowserClassUtil;
import com.browser.BrowserSdk;


public class SplashScreen extends Activity {
	final Context context = this;
	
	// Splash screen timer
	private static final int SPLASH_TIME_OUT = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startMainActivity();
	}

	private void startMainActivity() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				BrowserClassUtil.openProfileLink(SplashScreen.this, "Live Tv", AppConstant.BASE_URL);
//				startActivity(new Intent(SplashScreen.this, BrowserCustomActivity.class));
				finish();
			}
		},SPLASH_TIME_OUT);
	}
}
