package com.appcelerator.cloud.pushdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.appcelerator.cloud.push.CCPushService;
import com.appcelerator.cloud.push.DeviceTokenCallback;
import com.appcelerator.cloud.push.PushServiceException;
import com.appcelerator.cloud.push.PushType;

public class PushActivity extends Activity {
	public final static String LOG_TAG = PushActivity.class.getName();
	// TODO Update this line to your app_key
	public final static String APP_KEY = "<Your App Key>";
	public final static String GCM_SENDER_ID = "<GCM Sender ID>";

	private String mDeviceID;
	private Handler activityHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//TODO Switch Push type to MQTT mode instead of default GCM mode
		//CCPushService.getInstance().setPushType(getBaseContext(), PushType.MQTT);
		
		
		final Button registerMQTTButton = ((Button) findViewById(R.id.register_mqtt_button));
		final Button startMQTTButton = ((Button) findViewById(R.id.start_mqtt_button));
		final Button stopMQTTButton = ((Button) findViewById(R.id.stop_mqtt_button));
		final Button registerGCMButton = ((Button) findViewById(R.id.register_gcm_button));

		registerMQTTButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Get device token synchronously
				// mDeviceID = CCPushService.getDeviceToken(getBaseContext(), APP_KEY);
				// ((TextView) findViewById(R.id.target_text)).setText(mDeviceID);

				// Get device token asynchronously
				Context context = getBaseContext();
				try {
					PushType pushType = CCPushService.getInstance().getPushType(context);
					DeviceTokenCallback deviceTokenCallback = new DeviceTokenCallback() {
						public void receivedDeviceToken(final String deviceToken) {
							activityHandler.post(new Runnable() {
								public void run() {
									mDeviceID = deviceToken;
									((TextView) findViewById(R.id.target_text)).setText(mDeviceID);
								}
							});
						}

						public void failedReceiveDeviceToken(Throwable exception) {
							Log.e(LOG_TAG, exception.getMessage());
						}
					};
					if (PushType.GCM.equals(pushType)) {

					} else if (PushType.MQTT.equals(pushType)) {
						CCPushService.getInstance().getDeviceTokenAsnyc(context, APP_KEY, deviceTokenCallback);
					}
				} catch (PushServiceException ex) {
					Log.e(LOG_TAG, ex.getMessage());
				}
			}
		});
		startMQTTButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PushNotificationsManager.startPush(getBaseContext());
				startMQTTButton.setEnabled(false);
				startMQTTButton.setEnabled(true);
			}
		});
		stopMQTTButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PushNotificationsManager.stopPush(getApplicationContext());
				stopMQTTButton.setEnabled(true);
				stopMQTTButton.setEnabled(false);
			}
		});
		registerGCMButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Context context = getApplicationContext();
				CCPushService.getInstance().registerGCM(context, GCM_SENDER_ID, APP_KEY, new DeviceTokenCallback() {
					public void receivedDeviceToken(final String deviceToken) {
						activityHandler.post(new Runnable() {
							public void run() {
								mDeviceID = deviceToken;
								((TextView) findViewById(R.id.target_text)).setText(mDeviceID);
							}
						});
					}

					public void failedReceiveDeviceToken(Throwable exception) {
						Log.e(LOG_TAG, exception.getMessage());
					}
				});
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		final Context context = getBaseContext();
		boolean enabled = CCPushService.getInstance().ifEnabled(context);
		PushType pushType = CCPushService.getInstance().getPushType(context);

		if (PushType.GCM.equals(pushType)) {
			((Button) findViewById(R.id.register_mqtt_button)).setEnabled(false);
			((Button) findViewById(R.id.start_mqtt_button)).setEnabled(false);
			((Button) findViewById(R.id.stop_mqtt_button)).setEnabled(false);
			((Button) findViewById(R.id.register_gcm_button)).setEnabled(true);
			activityHandler.post(new Runnable() {
				public void run() {
					String token = CCPushService.getInstance().getDeviceTokenLocally(context);
					if (token != null) {
						Log.d(LOG_TAG, "Retrieve token directly from local side.");
						((TextView) findViewById(R.id.target_text)).setText(token);
					}
				}
			});
		} else if (PushType.MQTT.equals(pushType)) {
			((Button) findViewById(R.id.register_mqtt_button)).setEnabled(true);
			((Button) findViewById(R.id.start_mqtt_button)).setEnabled(!enabled);
			((Button) findViewById(R.id.stop_mqtt_button)).setEnabled(enabled);
			((Button) findViewById(R.id.register_gcm_button)).setEnabled(false);
		} else {
			((Button) findViewById(R.id.register_mqtt_button)).setEnabled(false);
			((Button) findViewById(R.id.start_mqtt_button)).setEnabled(false);
			((Button) findViewById(R.id.stop_mqtt_button)).setEnabled(false);
			((Button) findViewById(R.id.register_gcm_button)).setEnabled(false);
		}
	}
}