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
import com.appcelerator.cloud.push.GCMSenderIdCallback;
import com.appcelerator.cloud.push.PushServiceException;
import com.appcelerator.cloud.push.PushType;

public class PushActivity extends Activity {
	public final static String LOG_TAG = PushActivity.class.getName();
	// TODO Update this line to your app_key
	public final static String APP_KEY = "<Your App Key>";

	private String mDeviceID;
	private Handler activityHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//TODO Switch Push type to MQTT mode instead of default GCM mode
		// CCPushService.getInstance().setPushType(getBaseContext(), PushType.MQTT);
		
		
		final Button registerMQTTButton = ((Button) findViewById(R.id.register_mqtt_button));
		final Button startMQTTButton = ((Button) findViewById(R.id.start_mqtt_button));
		final Button stopMQTTButton = ((Button) findViewById(R.id.stop_mqtt_button));
		final Button registerGCMButton = ((Button) findViewById(R.id.register_gcm_button));

		registerMQTTButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Context context = getBaseContext();
				try {
					PushType pushType = CCPushService.getInstance().getPushType(context);
					if (PushType.GCM.equals(pushType)) {
						Log.e(LOG_TAG, "Push is under GCM mode and cannot use this function.");
					} else if (PushType.MQTT.equals(pushType)) {
						CCPushService.getInstance().getMQTTDeviceTokenAsnyc(context, APP_KEY, new DeviceTokenCallback() {
							public void receivedDeviceToken(final String deviceToken) {
								activityHandler.post(new Runnable() {
									public void run() {
										mDeviceID = deviceToken;
										((TextView) findViewById(R.id.device_token_text)).setText(mDeviceID);
										((Button) findViewById(R.id.register_mqtt_button)).setEnabled(false);
									}
								});
							}
							public void failedReceiveDeviceToken(Throwable ex) {
								Log.e(LOG_TAG, ex.getMessage());
							}
						});
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
				stopMQTTButton.setEnabled(true);
			}
		});

		stopMQTTButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PushNotificationsManager.stopPush(getApplicationContext());
				startMQTTButton.setEnabled(true);
				stopMQTTButton.setEnabled(false);
			}
		});

		registerGCMButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PushType pushType = CCPushService.getInstance().getPushType(getApplicationContext());
				if (PushType.GCM.equals(pushType)) {
					try {
						CCPushService.getInstance().getGCMSenderIdAsnyc(getApplicationContext(), APP_KEY, new GCMSenderIdCallback() {
							public void receivedGCMSenderId(String senderId) {
								Log.i(LOG_TAG, "Got SenderId: " + senderId);
								CCPushService.getInstance().registerGCM(getApplicationContext(), senderId, APP_KEY, new DeviceTokenCallback() {
									public void receivedDeviceToken(final String deviceToken) {
										if (deviceToken == null || deviceToken.length() == 0) {
											Log.e(LOG_TAG, "GCM server refused request. Have you configured this app for ACS?");
										} else {
											activityHandler.post(new Runnable() {
												public void run() {
													mDeviceID = deviceToken;
													((TextView) findViewById(R.id.device_token_text)).setText(mDeviceID);
													((Button) findViewById(R.id.register_gcm_button)).setEnabled(false);
												}
											});
										}
									}

									public void failedReceiveDeviceToken(Throwable ex) {
										Log.e(LOG_TAG, ex.getMessage());
									}
								});
							}

							public void failedReceiveGCMSenderId(Throwable ex) {
								Log.e(LOG_TAG, ex.getMessage());
							}
						});
					} catch (PushServiceException ex) {
						Log.e(LOG_TAG, ex.getMessage());
					}
				} else if (PushType.MQTT.equals(pushType)) {
					Log.e(LOG_TAG, "Push is under MQTT mode and cannot use this function.");
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		final Context context = getBaseContext();
		boolean enabled = CCPushService.getInstance().ifEnabled(context);
		PushType pushType = CCPushService.getInstance().getPushType(context);
		((TextView) findViewById(R.id.push_type_label)).setText("Push Type: " + pushType.name());
		final String localToken = CCPushService.getInstance().getDeviceTokenLocally(context);

		activityHandler.post(new Runnable() {
			public void run() {
				if (localToken != null) {
					Log.d(LOG_TAG, "Retrieve token directly from local side: " + localToken);
					((TextView) findViewById(R.id.device_token_text)).setText(localToken);
				}
			}
		});
		if (PushType.GCM.equals(pushType)) {
			((Button) findViewById(R.id.register_mqtt_button)).setEnabled(false);
			((Button) findViewById(R.id.start_mqtt_button)).setEnabled(false);
			((Button) findViewById(R.id.stop_mqtt_button)).setEnabled(false);
			((Button) findViewById(R.id.register_gcm_button)).setEnabled(localToken == null);
		} else if (PushType.MQTT.equals(pushType)) {
			((Button) findViewById(R.id.register_mqtt_button)).setEnabled(localToken == null);
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