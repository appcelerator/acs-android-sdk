package com.appcelerator.cloud.pushdemo;

import java.io.IOException;
import java.sql.Timestamp;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

public class GCMUtility {
	protected static GCMUtility instance;
	public static GCMUtility getInstance() {
		if (instance == null) {
			instance = new GCMUtility();
		}
		return instance;
	}

	private final static String LOG_TAG = GCMUtility.class.getName();
	public static final String PROPERTY_REG_ID = "GCMRegistrationId";
	private static final String PROPERTY_APP_VERSION = "GCMUsedAppVersion";
	private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "GCMOnServerExpirationTimeMs";

	/**
	 * Default lifespan (7 days) of a reservation until it is considered expired.
	 */
	public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;

	/**
	 * Substitute you own sender ID here.
	 */
	String[] senderIds;

	GoogleCloudMessaging gcm;
	
	public void setSenderId(String... senderIds){
		this.senderIds = senderIds;
	}
		

	/**
	 * Gets the current registration id for application on GCM service. If result is empty, the registration has failed.
	 * 
	 * @return registration id, or empty string if the registration is not complete.
	 */
	public String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			Log.v(LOG_TAG, "Registration not found.");
			return "";
		}
		// check if app was updated; if so, it must clear registration id to
		// avoid a race condition if GCM sends a message
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion || isRegistrationExpired(context)) {
			Log.v(LOG_TAG, "App version changed or registration expired.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		return context.getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException ex) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + ex);
		}
	}

	/**
	 * Checks if the registration has expired.
	 * 
	 * To avoid the scenario where the device sends the registration to the server but the server loses it, the app developer may
	 * choose to re-register after REGISTRATION_EXPIRY_TIME_MS.
	 * 
	 * @return true if the registration has expired.
	 */
	private boolean isRegistrationExpired(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		// checks if the information is not stale
		long expirationTime = prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
		return System.currentTimeMillis() > expirationTime;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * Stores the registration id, app versionCode, and expiration time in the application's shared preferences.
	 */
	public void registerBackground(final Context context) {
		new AsyncTask<Object, Integer, String>() {
			@Override
			protected String doInBackground(Object... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					String regid = gcm.register(senderIds);
					msg = "Device registered. RegistrationId=(" + regid + ")";

					// You should send the registration ID to your server over HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your app.

					// For this demo: we don't need to send it because the device
					// will send upstream messages to a server that echo back the message
					// using the 'from' address in the message.

					// Save the regid - no need to register again.
					setRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error: " + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.v(LOG_TAG, msg);
				// mDisplay.append(msg + "\n");
			}
		}.execute(null, null, null);
	}

	/**
	 * Stores the registration id, app versionCode, and expiration time in the application's {@code SharedPreferences}.
	 * 
	 * @param context
	 *          application's context.
	 * @param regId
	 *          registration id
	 */
	private void setRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.v(LOG_TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

		Log.v(LOG_TAG, "Setting registration expiry time to " + new Timestamp(expirationTime));
		editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
		editor.commit();
	}

}
