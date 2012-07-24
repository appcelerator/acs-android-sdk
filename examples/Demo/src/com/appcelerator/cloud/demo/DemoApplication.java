package com.appcelerator.cloud.demo;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.appcelerator.cloud.sdk.Cocoafish;

public class DemoApplication extends Application {
	// TODO Update your own app_id here
	public static final String APP_ID = "NIE4y3ax2UnmiWtkpi3Rbr9RChBvit2a";
	public static final String FACEBOOK_APP_ID = "";
	// TODO Update your own oAuth account here
	public static final String APP_CONSUMER_KEY = "VGJSVgFHs7FaOcgcvMWMAGe6bwNpHBfq";
	public static final String APP_CONSUMER_SECRET = "ZDkLBzlL28ISUngLgjwuUuMdMqF3Jrm5";
	private static Cocoafish sdk = null;
	private static DemoSession session = null;

	@Override
	public void onCreate() {
		PreferenceManager.setDefaultValues(this, R.xml.default_values, false);

		// Initialize Cocoafish
		initialize(APP_CONSUMER_KEY, APP_CONSUMER_SECRET, getApplicationContext());
	}

	private static void initialize(String appConsumerKey, String appConsumerSecret, Context appContext) {
//		//Pass app key to the 'key' argument
//		sdk = new Cocoafish(APP_ID, appContext, "192.168.1.113:3000/v1/");
//		//Pass both oauth key and secret
//		sdk = new Cocoafish(appConsumerKey, appConsumerSecret, appContext, "192.168.1.113:3000/v1/");
		
		//Pass both oauth key and secret and use 3-legged oauth
		sdk = new Cocoafish(appConsumerKey, appConsumerSecret, appContext, "192.168.1.113:3000/v1/");
		//for authentication/authorization with Authorization Server
		sdk.useThreeLegged(true);
		
//		//Use 3-legged OAuth but without OAuth secret specified - the 'key' argument expects OAuth key
//		sdk = new Cocoafish(appConsumerKey, appContext, "192.168.1.113:3000/v1/");
//		sdk.useThreeLegged(true);
        
		sdk.setDlgCustomizer(new MyDlgCustomizer());
        sdk.setAuthHost("192.168.1.113:3001");
		session = new DemoSession();
	}

	@Override
	public void onTerminate() {
	}

	public static Cocoafish getSdk() {
		return sdk;
	}

	public static DemoSession getSession() {
		return session;
	}
}
