package com.appcelerator.cloud.pushdemo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.appcelerator.cloud.push.CCPushService;
import com.appcelerator.cloud.push.PushServiceException;
import com.appcelerator.cloud.sdk.CCMeta;
import com.appcelerator.cloud.sdk.CCRequestMethod;
import com.appcelerator.cloud.sdk.CCResponse;
import com.appcelerator.cloud.sdk.Cocoafish;
import com.appcelerator.cloud.sdk.CocoafishError;

import android.content.Context;
import android.util.Log;

/**
 * Push notifications management static class.
 * Push service can be started from here.
 */
public class PushNotificationsManager{
	public final static String LOG_TAG = PushNotificationsManager.class.getName();
	
	public static void startPush(final Context context) {
    	try {
			CCPushService.getInstance().startService(context);
		} catch (PushServiceException ex) {
			Log.e(LOG_TAG, "Push Service occurs an exception: " + ex.getMessage());
		}
	}
	
	public static void stopPush(Context context) {
		CCPushService.getInstance().stopService(context);
	}
	
	public static boolean subscribePushNotifications(Cocoafish sdk, String deviceToken, String channel) throws IOException,
			CocoafishError, JSONException {
		String type = "android";

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("device_token", deviceToken);
		data.put("channel", channel);
		data.put("type", type);
		CCResponse response = sdk.sendRequest("push_notification/subscribe.json", CCRequestMethod.POST, data, false);
		CCMeta meta = response.getMeta();
		if ("ok".equals(meta.getStatus()) && meta.getCode() == 200 && "SubscribeNotification".equals(meta.getMethod())) {
			return true;
		}
		throw new CocoafishError("SubscribeNotification failed. Error Message:" + meta.getMessage());
	}
	
	public static boolean unsubscribePushNotifications(Cocoafish sdk, String deviceToken, String channel) throws IOException,
			CocoafishError, JSONException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("device_token", deviceToken);
		data.put("channel", channel);
		CCResponse response = sdk.sendRequest("push_notification/unsubscribe.json", CCRequestMethod.DELETE, data, false);
		CCMeta meta = response.getMeta();
		if ("ok".equals(meta.getStatus()) && meta.getCode() == 200 && "UnsubscribeNotification".equals(meta.getMethod())) {
			return true;
		}
		throw new CocoafishError("UnsubscribeNotification failed. Error Message:" + meta.getMessage());
	}
	
	public static boolean loggedIn(Cocoafish sdk) throws IOException, CocoafishError, JSONException {
		CCResponse response = sdk.sendRequest("users/show/me.json", CCRequestMethod.GET, null, false);
		CCMeta meta = response.getMeta();
		if ("ok".equals(meta.getStatus()) && meta.getCode() == 200 && "showMe".equals(meta.getMethod())) {
			return true;
		}
		return false;
	}
	
	public static boolean loginUser(Cocoafish sdk, String username, String password) throws IOException, CocoafishError,
			JSONException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("login", username);
		data.put("password", password);
		CCResponse response = sdk.sendRequest("users/login.json", CCRequestMethod.POST, data, false);

		CCMeta meta = response.getMeta();
		if ("ok".equals(meta.getStatus()) && meta.getCode() == 200 && "loginUser".equals(meta.getMethod())) {
			return true;
		}
		throw new CocoafishError("User login failed. Error Message:" + meta.getMessage());
	}
	
	public static boolean logoutUser(Cocoafish sdk) throws IOException, CocoafishError, JSONException {
		CCResponse response = sdk.sendRequest("users/logout.json", CCRequestMethod.GET, null, false);
		CCMeta meta = response.getMeta();
		if ("ok".equals(meta.getStatus()) && meta.getCode() == 200 && "logoutUser".equals(meta.getMethod())) {
			return true;
		}

		throw new CocoafishError("User logout failed. Error Message:" + meta.getMessage());
	}
}