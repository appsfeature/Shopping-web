package com.appsfeature.browser.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.appsfeature.browser.util.AppConstant;
import com.browser.BrowserSdk;


public class SplashScreen extends Activity {
	final Context context = this;
	
	// Splash screen timer
	private static final int SPLASH_TIME_OUT = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_splash);
		startMainActivity();
	}

	private void startMainActivity() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				BrowserSdk.open(SplashScreen.this, "Live Tv", AppConstant.BASE_URL, false);
				finish();
			}
		},SPLASH_TIME_OUT);
	}
}
