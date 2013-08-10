package com.appcelerator.cloud.pushdemo;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.appcelerator.cloud.push.PushService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CustomReceiver extends BroadcastReceiver {
	public final static String LOG_TAG = CustomReceiver.class.getName();
	private final static String MSG_ARRIVAL = PushService.ACTION_MSG_ARRIVAL;
	private final static String GCM_RECEIVE = "com.google.android.c2dm.intent.RECEIVE";
	private final static String GCM_REGISTRATION = "com.google.android.c2dm.intent.REGISTRATION";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null || context == null)
			return;

		if (intent.getAction().equals(MSG_ARRIVAL)) {
			String payloadStr = intent.getStringExtra("payload");

			// Covert payload from String to JSONObject
			JSONObject payload = null;
			try {
				payload = new JSONObject(payloadStr);
			} catch (JSONException ex) {
			}
			showNotification(context, payload);
		} else if (intent.getAction().equals(GCM_REGISTRATION)) {
			handleRegistration(context, intent);
		} else if (intent.getAction().equals(GCM_RECEIVE)) {
			handleMessage(context, intent);
		}
	}

	private void handleRegistration(Context context, Intent intent) {
		Log.d(LOG_TAG, "abccc");
		String registrationid = intent.getStringExtra("registration_id");
		String error = intent.getStringExtra("error");
		String unregistered = intent.getStringExtra("unregistered");
		if (error != null) {
			// Registration failed, should try again later.
			Log.d(LOG_TAG, "error: " + error);
		} else if (unregistered != null) {
			// unregistration done, new messages from the authorized sender will be rejected
			Log.d(LOG_TAG, "unregistered: " + unregistered);
		} else if (registrationid != null) {
			Log.d(LOG_TAG, "new registration id: " + registrationid);
			// Send the registration ID to the 3rd party site that is sending the messages.
			// This should be done in a separate thread.
			// When done, remember that all registration is done.
		}
	}

	private void handleMessage(Context context, Intent intent) {
		Log.d(LOG_TAG, "bcddd");
		Iterator<String> str = intent.getExtras().keySet().iterator();
		while(str.hasNext()){
			String next = str.next();
			Log.d(LOG_TAG, "aaaa" + next + "  " + intent.getStringExtra(next));
		}
	}

	private void showNotification(Context context, JSONObject payload) {
		if (payload == null) {
			Log.e(LOG_TAG, "Payload is null!");
		}

		// Ensure payload is correct, and get needed information from received payload
		String alert = null;
		try {
			JSONObject androidPartJson = payload.getJSONObject("android");
			alert = androidPartJson.optString("alert", null);
			;
		} catch (JSONException ex) {
			if (alert == null)
				alert = payload.toString();
		}

		NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification();

		n.flags |= Notification.FLAG_SHOW_LIGHTS;
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		n.icon = R.drawable.icon;
		n.defaults = Notification.DEFAULT_ALL;

		String title = context.getResources().getString(R.string.app_name);
		Intent intent = new Intent(context, ArrivalActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("payload", payload.toString());
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		n.setLatestEventInfo(context, title + " (Custom)", alert, pi);
		notifManager.notify(0, n);
	}
}