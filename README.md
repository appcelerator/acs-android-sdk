This open source Java library allows you to integrate ACS into your Android application. Except as otherwise noted, the ACS Android SDK is licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)

**Note**: The ACS iOS SDK is no longer being developed or maintained. Please consider using
the [Appcelerator Platform Services SDK for iOS](http://docs.appcelerator.com/cloud/latest/#!/guide/ios) instead 
(Platform subscription required).


<div id="doc_api">

<h1>Getting Started: Using the Android SDK</h1>

<p>The ACS Android SDK allows you to integrate Appcelerator Cloud Services into your Android application.</p>

<h2>Setting up your environment</h2>

<p>The ACS Android SDK works in any Android development environment. The following steps assume you are
using Eclipse.</p>

<p>The first step is to clone the ACS Android SDK from GitHub.</p>

<p>
<strong>Clone the ACS Android SDK</strong>:
<pre class="prettyprint">git clone git@github.com:appcelerator/acs-android-sdk.git</pre>
</p>

<p>Next, you'll create a new project in Eclipse for the ACS SDK.</p>

<p><strong>Create a new project for the ACS SDK</strong>:
<ol>
  <li>In Eclipse, select <strong>File >  New > Project</strong>.</li>
  <li>Select <strong>Android Project from Existing Code</strong> from the Android category, and click <strong>Next</strong>.</li>
  <li>Select the <strong>acs-android-sdk/acs</strong> directory. You should see the project properties populated.</li>
  <li>Click Finish.</li>
  <li>Open the Properties dialog for the project and select the Android category.</li>
  <li>In the Library section, enable the <strong>Is Library</strong> option.
    {@img is_library.png}
  </li>
  <li>Click <strong>OK</strong></li>
</ol>
</p>
<p>Now you can add the ACS project as a library of an existing project.</p>

<h2>Add the SDK to an existing project</h2>

<p>Once you've created the Android project for the SDK in your workspace, you can include the
SDK as a library of an existing project.</p>

<strong>To add the ACS Android SDK to an existing project:</strong>

<ol>
  <li>In the same workspace that contains the ACS Android SDK library project, open the Properties dialog for the project.</li>
  <li>Select the Android category.</li>
  <li>In the Library section, click <strong>Add</strong>.</li>
  <li>In the Project Selection dialog, select the ACS Android SDK library project.
    {@img add-library-dialog.png}
  </li>
  <li>Click OK.</li>
</ol>

<p>You're now ready to use the SDK in your application.</p>

<h2>Initialization and authorization</h2>

<p>You use the `com.appcelerator.cloud.sdk.ACSClient` class to make API calls. First, you must import the ACSClient library into your application:</p>

<pre class="prettyprint">
import com.appcelerator.cloud.sdk.ACSClient;
</pre>

<p>You need to authorize your application with Appcelerator Cloud Services using either an
application key, or your OAuth consumer key/secret. </p>

<pre class="prettyprint">
ACSClient sdk = new ACSClient("&lt;app key&gt;");
</pre>

<pre class="prettyprint">
ACSClient sdk = new ACSClient("&lt;OAuth consumer key&gt;", "&lt;OAuth secret&gt;");
</pre>

<p>The ACS Android SDK also provides ability to store user session data in the device's local
storage. This allows the application to restore a session after it is has been closed and
then restarted. To enable this feature, pass an instance of the application's
<code>android.content.Context</code> to the <code>ACSClient</code> constructor method as an additional
parameter. You use the <code><a href="http://developer.android.com/reference/android/content/Context.html#getApplicationContext()">getApplicationContext()</a>
</code> to get the application context.</p>

<p>If you are using an OAuth consumer key and secret:</p>
<pre class="prettyprint">
ACSClient sdk = new ACSClient("&lt;OAuth consumer key&gt;", "&lt;OAuth secret&gt;", "&lt;app. context&gt;");
</pre>

<p>Or if you are using an application key to authorize:</p>

<pre class="prettyprint">
ACSClient sdk = new ACSClient("&lt;app key&gt;", "&lt;app. context&gt;");
</pre>

<h3>Session Management</h3>

<p>As mentioned previously, when you create an ACSClient instance,  lets you optionally store your application's
session status in the device's local storage. The client also holds an instance of
<code>com.appcelerator.cloud.sdk.CCUser</code> that contains the currently logged in user's
information. The <code>CCUser</code> class has the following definition:</p>

<pre class="prettyprint">
public class CCUser {
  public String getObjectId();  //id
  public Date getCreatedDate(); //created date
  public Date getUpdatedDate(); //updated date
  public String getFirst();     //first name
  public String getLast();      //last name
  public String getEmail();     //email
  public String getUserName();  //user name
}
</pre>

<p>You get an instance of this class by calling the <code>getCurrentUser()</code> method of the ACSClient class. If there is
no current session or "logout" is called, the return value will be null.</p>

<p>Call the <code>getCurrentUser()</code> method of the <code>ACSclient</code> class; if there is no
active session, or <code>logoutUser()</code> was called, the return value will be <code>null</code>.</p>

<h2>Making API calls</h2>

The SDK provides the <code>sendRequest()</code> method to make synchronized REST calls to
the ACS server easier. It has the following signature:

<pre class="prettyprint">
public CCResponse sendRequest(String url, CCRequestMethod method, Map&lt;String, Object&gt; data, boolean useSecure) throws ACSClientError
</pre>

<h3>Parameters</h3>

<p><strong>url</strong></p> <p> The request API URL, which is a shortened version of the full REST
URL. For instance, with the REST API you use
<strong>http://api.cloud.appcelerator.com/v1/users/create.json</strong> to create a new user. In
this case, you would use the value <strong>users/create.json</strong> for the <code>url</code>
parameter.

<p><strong>method</strong></p>

<p>Specifies the REST-ful method to use when making the method call. Accepts one of the following
four objects from class <code>com.appcelerator.cloud.sdk.CCRequestMethod</code> as its value:</p>

<ul>
<li>CCRequestMethod.GET</li>
<li>CCRequestMethod.POST</li>
<li>CCRequestMethod.PUT</li>
<li>CCRequestMethod.DELETE</li>
</ul>

<p><strong>data</strong></p>
<p>An instance of <code>Map&lt;String, Object&gt;</code> which contains the parameters to pass. The key
is the name of the passing parameter, and the value is the value of the passing parameter. For
example, using the user login API:</p>

<pre class="prettyprint">
Map&lt;String, Object&gt; data = new HashMap&lt;String, Object&gt;();
data.put("login", "test@appcelerator.com");
data.put("password", "test");
</pre>

<p><strong>useSecure</strong></p>

<p>Indicates whether the client should use SSL for HTTP communication. Set to <strong>true</strong> to use SSL, or
<strong>false</strong> to a non-SSL connection.</p>

<h2>Handling server responses</h2>

<p>Server response are contained in an instance of
<code>com.appcelerator.cloud.sdk.CCResponse</code> returned by the <code>sendRequest()</code>
method. The CCResponse class has the following definition:</p>

<pre class="prettyprint">
public class CCResponse {
  public CCMeta getMeta();
  public JSONObject getResponseData();
}
</pre>

<p>The <code>getMeta()</code> method returns an instance of
<code>com.appcelerator.cloud.sdk.CCMeta</code> which contains all metadata returned by server. Here
is the CCMeta's class definition:</p>

<pre class="prettyprint">
public class CCMeta {
  public String getStatus();
  public int getCode();
  public String getMessage();
  public String getMethod();
}
</pre>

<p>The <code>getResponseData()</code> method returns an instance of class
<code>org.json.JSONObject</code> which contains all of the response data returned by server. The ACS
Android SDK uses the standard <code><a href="http://www.json.org/javadoc/org/json/JSONObject.html">JSONObject</a></code>
class to wrap the returned JSON data.

<h3>Example of accessing a response object</h3>

The code below demonstrates how to handle an array users returned by the `users/search.json` method :

<pre class="prettyprint">
CCResponse response = sdk.sendRequest("users/search.json", CCRequestMethod.GET, null, false);
JSONObject responseJSON = response.getResponseData();
JSONArray users = responseJSON.getJSONArray("users");
for (int i=0;i&lt;users.length();i++) {
  JSONObject user = usersArr.getJSONObject(i);
  System.out.println("User: " + user);
}
</pre>

<h2>Push Notifications</h2>

ACS supports <a href="http://developer.android.com/google/gcm/index.html">Google Cloud
Messaging</a> (GCM) for sending notifications to Android clients. This section describes how to
integrate GCM notifications into your Android application with ACS.

<h3>Obtaining a Google API key and GCM sender ID</h3>

To use GCM in your Android application, you need to create a Google API project, enable its GCM service,
and obtain the API project's Google API key and GCM sender ID. For steps on obtaining these items
see <a href="/titanium/latest/#!/guide/Push_Notifications-section-37532857_PushNotifications-SettingupGoogleCloudMessaging" target="_">Setting up Google Cloud Messaging</a>.

<h3>Setup the Android Project</h3>

Next you need to add the ACS `PushService` service to your Android project's manifest file.

<ol>
  <li>Open your project's AndroidManifest.xml file.</li>
  <li>Add following to the <code>&lt;application&gt;</code> section:
<pre class="prettyprint">
&lt;service android:name="com.appcelerator.cloud.push.PushService" /&gt;
</pre>
  </li>
  <li>Add following to the <code>&lt;manifest&gt;</code> section:
<pre class="prettyprint">
&lt;uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" /&gt;
&lt;uses-permission android:name="android.permission.READ_PHONE_STATE" /&gt;
&lt;uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /&gt;
&lt;uses-permission android:name="android.permission.INTERNET" /&gt;
&lt;uses-permission android:name="android.permission.VIBRATE" /&gt;
&lt;uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /&gt;
</pre>
  </li>
  <li>Save your changes to AndroidManifest.xml.</li>
</ol>

<h3>Device Token</h3>

<p>A device token is a unique identifier for an Android device. The push notification server can
locate and push the desired notifications to the right device. You request a device token from the
Appcelerator Cloud Services push notification server by calling one of following methods from the
<code>com.appcelerator.cloud.push.CCPushService</code> class:</p>

<pre class="prettyprint">
public static String getDeviceToken(final Context androidContext, final String appKey);
public static void getDeviceTokenAsnyc(final Context androidContext, final String appKey, final DeviceTokenCallback callback);
</pre>

<p>As their method names suggest, the difference between these methods is that <code>getDeviceToken()</code> operates synchronously, where the <code>getDeviceTokenAsnyc()</code> is asynchronous.</p>

<ul>
  <li><strong>androidContext</strong> -- The instance of <code>android.context.Context</code> from your app</li>
  <li><strong>appKey</strong> -- The app's key of your Appcelerator Cloud Services app </li>
  <li><strong>callback</strong> -- An instance of <code>com.appcelerator.cloud.push.DeviceTokenCallback</code> for which whenever a device token is available, its <code>receivedDeviceToken</code> will be called.</li>
</ul>

<p>Once you get the device token, you may proceed to subscribe the device by using the {@link
PushNotifications#subscribe} API. This starts the push notification service and gets the Android
device ready to receive push notifications.</p>

<p>Note: once device token is successfully acquired, it will be stored on the device's local
storage, and next time when getting device token, it will return from reading local storage instead
of communicating with server. So it is safe to request device token multiple times if necessary.</p>

<h3>Push Notification Service</h3>

<p>The push notification service is a standard Android service that keeps a persistent connection to
the push notification server. Once the service is started, the device is ready to receive push
notifications. The service continues running as a background process even if app is not actively
running. The class <code>com.appcelerator.cloud.push.CCPushService</code> is a utility for starting
and stopping the push notification service.</p>

<p>To start or stop the push notification service:</p>

<pre class="prettyprint">
/* Start push notification service */
CCPushService.getInstance().startService(context);
/* Stop push notification service */
CCPushService.getInstance().stopService(context);
</pre>

<p>The <code>context</code> is the instance of <code>android.content.Context</code> which belongs to
your Android app.</p>

<p>Please be noted that you need to get device token before starting the push notification service
on your device, otherwise, there will be an exception thrown when trying to start push notification
service without having a valid device token.</p>

<h3 id="receivepush">Receiving push notifications</h3>

<p>Once your application has acquired the device token, and started the push notification service,
the device is ready to receive push notifications. When a push notification arrives, there will
be an application-wide broadcast containing the notification payload.

<p>To process a push notification, your application must implement an Android receiver to receive
the payload, and register the receiver in your project's AndroidManifest.xml file.</p>

<p>Below is an example of a receiver. The payload is a
string that's passed to your receiver as an extra data field in the <code>Intent</code>
parameter.</p>

<pre class="prettyprint">
import com.appcelerator.cloud.push.PushService;

public class CustomReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if(intent == null || context == null)
      return;
    if (intent.getAction().equals(PushService.ACTION_MSG_ARRIVAL)) {
      String payloadString = intent.getStringExtra("payload");
      // Covert payload from String to JSONObject
      JSONObject payload = null;
      try {
        payload = new JSONObject(payloadString);
      } catch (JSONException ex) {
        //error
      }
      ...
    }
  }
}
</pre>

<p>You must also register the receiver in the <code>&lt;application&gt;</code> section of your project's AndroidManifest.xml:</p>

<pre class="prettyprint">
&lt;receiver android:name="com.your.app.CustomReceiver" &gt;
  &lt;intent-filter&gt;
    &lt;action android:name="com.appcelerator.cloud.push.PushService.MSG_ARRIVAL" /&gt;
    &lt;category android:name="android.intent.category.HOME" /&gt;
  &lt;/intent-filter&gt;
&lt;/receiver&gt;
</pre>

<h4>Using the default broadcast receiver</h4>

<p>The ACS Android SDK includes a fully-featured notification receiver that you can use in your
project, instead of creating your own from scratch. The default notification receiver can take one of the
following actions when a notification arrives:</p>

<ul>
	<li>Show a custom alert</li>
	<li>Display custom title</li>
	<li>Display custom icon</li>
	<li>Display custom badge</li>
	<li>Play a custom sound</li>
	<li>Vibrate the device</li>
</ul>

<p>Your application can invoke any of these actions simply by setting specifically named fields in
the notification's JSON payload. First, you must register the default notification receiver in the
<code>&lt;application&gt;</code> section of your project's AndroidManifest.xml file, as shown below.</p>

<pre class="prettyprint">
&lt;receiver android:name="com.appcelerator.cloud.push.PushBroadcastReceiver" &gt;
  &lt;intent-filter&gt;
    &lt;action android:name="android.intent.action.BOOT_COMPLETED" /&gt;
    &lt;action android:name="android.intent.action.USER_PRESENT" /&gt;
    &lt;action android:name="com.appcelerator.cloud.push.PushService.MSG_ARRIVAL" /&gt;
    &lt;category android:name="android.intent.category.HOME" /&gt;
  &lt;/intent-filter&gt;
  &lt;meta-data
    android:name="com.appcelerator.cloud.push.BroadcastReceiver.ArrivalActivity"
    android:value="com.yourorganization.pushnotifications.ArrivalActivity" /&gt;
&lt;/receiver&gt;
</pre>

<p> When the device receives a push notification and has been processed by the default receiver, the
ACSClient needs an <code>Activity</code> from your application to hand processing control to. The
<code>&lt;meta-data/&gt;</code> element registers your custom <code>Activity</code>. The
<strong>name</strong> attribute of the <code>&lt;meta-data/&gt;</code> section must be
<strong>com.appcelerator.cloud.push.BroadcastReceiver.ArrivalActivity</strong> for the receiver to
locate your activity, and its <strong>value</strong> attribute must point to your custom <code>Activity</code>.</p>

<p>The complete payload is passed to your <code>Activity</code> so that your application still
further process the payload data, as necessary. Below is an example of an custom activity getting
access to the notification payload:</p>

<pre class="prettyprint">
public class ArrivalActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.arrival);
    Intent intent = getIntent();
    String payload = intent.getExtras().getString("payload");
    ...
  }
}
</pre>

<p>To invoke the desired behavior of the default receiver, the JSON notification payload must use
certain reserved keywords, defined in the following table.</p>

<table class="doc-table">
	<tr>
		<th>Key</th>
		<th nowrap>Value type</th>
		<th>Comments</th>
	</tr>
	<tr>
		<td><code>alert</code></td>
		<td>String</td>
		<td>Content of the notification item to be displayed in notification center.</td>
	</tr>
	<tr>
      <td><code>title</code></td>
      <td>String</td>
      <td>Title of the notification item to be displayed in notification center.</td>
    </tr>
	<tr>
		<td><code>icon</code></td>
		<td>String</td>
    <td>The icon's file name without extension. This icon will be used to show in notification bar
    and be used as icon of notification item in the notification center. The file must be located in
    your project's <code><strong>res/drawable</strong></code> directory. If an icon file is not
    specified, or the specified file cannot be located, then app's default icon is used
    instead.</td>
	</tr>
	<tr>
		<td><code>badge</code></td>
		<td>Number</td>
		<td>The number to display as the badge of the application icon.</td>
	</tr>
	<tr>
		<td><code>sound</code></td>
		<td>String</td>
    <td>The sound's file name. The file should be located under app's
    <code><strong>assets/sound</strong></code> directory. If the sound file is not reachable, no
    sound will be played.</td>
	</tr>
	<tr>
		<td><code>vibrate</code></td>
		<td>Boolean</td>
		<td>If the value is true, then the device will vibrate for 1 second.</td>
	</tr>
</table>

<p>Below an example JSON payload including all the reserved keywords with custom content:</p>

<pre class="prettyprint">
{
  "title": "Example",
  "alert": "Sample alert",
  "icon": "little_star",
  "badge": 3,
  "sound": "door_bell.wav",
  "vibrate": true,
  "score": 51,
  "custom_field": {
    "headlines": "Appcelerator Cloud Services Rocks!"
  }
}
</pre>

<p class="note">It's recommended to only have one push notification receiver registered at a time.
Registering multiple receivers at the same time may cause conflicts.</p>

<h3>Extending the default ACS receiver</h3>

<p>You can extend the default notification's receiver implementation with your own functionality. To
do this, you create your own broadcast receiver, as explained in the "Receiving
push notifications" section above. In your custom receiver's <code>onReceive()</code> handler, extract the JSON
payload and pass it to the ACSClient's <code>showNotification()</code> method to invoke the default receiver's
behavior (for example, playing an audio file or displaying a message).</p>

<p>There are two versions of the <code>showNotification()</code> method. One takes two parameters:
the Android application context and the JSON payload. The second version takes these two parameters
and a notification ID (an integer) that lets you control how or if multiple notifications are merged
into a single item in the Android notification center. </p>

<pre class="prettyprint">
protected void showNotification(Context context, JSONObject payload);
protected void showNotification(Context context, JSONObject payload, int notificationId);
</pre>

<p><strong>Parameters</strong></p>
<ul>
  <li><code>context</code> -- The <code>android.content.Context</code> that belongs to your app.</li>
  <li><code>payload</code> -- The <code>JSONObject</code> containing the notification payload.</li>
  <li><code>notificationId</code> -- An integer that identifies the notification. Notifications that
  share the same ID are merged into a single item in the Android notification center.</li>
</ul>

<p class="note">Remember to remove the default receiver from the AndroidManifest if you register your own receiver.</p>

<h2>Photo Uploads</h2>

<p>To upload a photo with the Android ACS SDK you create an instance of
<code>java.io.File</code>, then pass the instance as an item of the <code>data</code> to the
<code>sendRequest()</code> method.

<p class="Note">The <strong>File</strong> instance must be initialized with an existing file.</p>

<p>Here is an example of uploading a local image file to ACS server through Android SDK:</p>

<p>
1. Create a File instance:
<pre class="prettyprint">
// Create a File instance
File file = new File("/photos/profile.gif");
// Upload the image file by calling <code>sendRequest</code>:
ACSClient sdk = new ACSClient('vw1G7wq6KTKd52m76XwjvoiIhxeHxeXG');
Map&lt;String, Object&gt; data = new HashMap&lt;String, Object&gt;();
data.put("photo", file);
sdk.sendRequest("photos/create.json", CCRequestMethod.POST, data, false);
</pre>
</p>

<p class="note">It is the client code's responsibility to make sure the instance of <code>File</code> is
initialized with an existing local image file before passing to SDK, otherwise, the upload won't be
succeed.</p>

<h2>Example</h2>

<p>Below is an example of creating a user by using the ACS Android SDK with a profile photo.</p>

<p> First, prepare the data object to pass to the sendRequest() method.

<pre class="prettyprint">
// Prepare new user data:
Map&lt;String, Object&gt; data = new HashMap&lt;String, Object&gt;()
data.put("email", "test@appcelerator.com");
data.put("first_name", "test_firstname");
data.put("last_name", "test_lastname");
data.put("password", "test_password");
data.put("password_confirmation", "test_password");

// Prepare new user profile image:
File file = new File("/photos/profile.gif");
data.put("photo", file);
</pre>
</p>

<p>
Next, create an ACSClient instance and send the request.
<pre class="prettyprint">
ACSClient sdk = new ACSClient('vw1G7wq6KTKd52m76XwjvoiIhxeHxeXG');
CCResponse response = sdk.sendRequest("users/create.json", URLRequestMethod.POST, data, false);
</pre>
</p>

<p> When the <code>sendRequest()</code> method returns, process the server response. In this case,
the new user's information is logged to the system console.</p>

<pre class="prettyprint">
JSONObject responseJSON = response.getResponseData();  //CCResponse response
CCMeta meta = response.getMeta();
if("ok".equals(meta.getStatus())
    && meta.getCode() == 200
    && "createUser".equals(meta.getMethod())) {
  JSONArray users = responseJSON.getJSONArray("users");
  JSONObject user = users.getJSONObject(0);

  StringBuffer sb = new StringBuffer();
  sb.append("Create user successful!\n");
  sb.append("id:" + user.getString("id") + "\n");
  sb.append("first name:" + user.getString("first_name") + "\n");
  sb.append("last name:" + user.getString("last_name") + "\n");
  sb.append("email:" + user.getString("email") + "\n");
  System.out.println(sb.toString());
}
</pre>
</p>

<h2>Run the ACS Demo sample application</h2>

<p>Below are instructions for creating and running the <a href="https://github.com/appcelerator/acs-android-sdk/tree/master/examples/Demo">Demo sample</a> using Eclipse.
  The sample application uses the Google Maps v2 API to display location data. To build the application you will need to install <a
href="http://developer.android.com/google/play-services/setup.html">Google Play services</a> and add
it to your project as a library. You'll also need to <a href="https://developers.google.com/maps/documentation/android/start#obtain_a_google_maps_api_key">generate an API key</a>
for the Google Maps Android API v2 service.</p>

<strong>Importing and setting up the Demo application:</strong>
<ol>
  <li>In Eclipse, select <strong>File > Import...</strong>.</li>
  <li>Select <strong>Existing Android Code into Workspace</strong> from the Android category folder and click <strong>Next</strong>.</li>
  <li>Click <strong>Browse...</strong> and select the <strong>acs-android-sdk/examples/Demo</strong> folder.</li>
  <li>Click <strong>Finish</strong>.</li>
  <li>Open the Properties dialog for the project and, in the Android section, add Google Play Services as a library.
    {@img play-services-lib.png}
  </li>
  <li>Click <strong>OK</strong> to apply your changes.</li>
  <li>Open <strong>examples/Demo/res/layout/main.xml</strong>.</li>
  <li>Locate the <code><strong>&lt;com.google.android.maps.MapView/&gt;</strong></code> section of
  the file update the <code><strong>android:apiKey</strong></code> with your Google Maps API key,
  for example:
<pre class="prettyprint">
android:apiKey="1234567890abcdefghijklmnop"</pre>
  </li>
  <li>Open <strong>DemoApplications.java</strong> located in the <code>src/</code> folder.</li>
  <li>Locate the <code>APP_ID</code> constant and assign to it your ACS application ID, for example:
<pre class="prettyprint">
public static final String APP_ID = "7YGry9R7abckXFG3ZYPAvQtA3TVBRH4T";</pre>
  </li>
  <li>In the <code>initialize()</code> method, uncomment the following line to construct the ACSClient using <code>API_ID</code>.
<pre class="prettyprint">
sdk = new ACSClient(APP_ID, appContext);</pre>
  </li>
  <li>Save your changes.</li>
</ol>

<strong>Run the application:</strong>

<ol>
  <li>Create an appropriate Android virtual device (AVD):
    <ul>
      <li>Open the <a href="http://developer.android.com/tools/help/avd-manager.html">Android Virtual Device Manager</a>. </li>
      <li>Click <strong>New</strong>.</li>
      <li>From the <strong>Target</strong> menu select <strong>Google APIs - API Level 13</strong>.</li>
      <li>Click OK.</li>
    </ul>
  </li>
  <li>In Eclipse, create a <a href="http://developer.android.com/tools/building/building-eclipse.html#RunConfig">Android run configuration</a>
    that targets the AVD you created.
  </li>
  <li>Launch the run configuration to run the application in the simulator.</li>
</ol>

<h2>3-Legged OAuth process</h2>

<p>Android SDK 2.1.1 and later supports interactions with Authorization Server. It provides APIs for
Android application developers to sign in/sign up/sign out users with Authorization Server. For
signing-in and signing-up the SDK uses a webview to load pages from Authorization Server.</p>

<h3>1. Create a ACSClient object</h3>

<p>To create a ACSClient object use one of the following constructors:</p>

<pre class="prettyprint">
ACSClient sdk = new ACSClient(appConsumerKey);
ACSClient sdk = new ACSClient(appConsumerKey, appContext);
ACSClient sdk = new ACSClient(appConsumerKey, appContext, APIhost);
ACSClient sdk = new ACSClient(appConsumerKey, appConsumerSecret);
ACSClient sdk = new ACSClient(appConsumerKey, appConsumerSecret, appContext);
ACSClient sdk = new ACSClient(appConsumerKey, appConsumerSecret, appContext, APIhost);
</pre>
If <i>appConsumerSecret</i> is not passed in the SDK will fail to sign requests. <i>appContext</i> is an android.content.Context object. <i>APIhost</i> is used to specify the ACS API server. The default is api.cloud.appcelerator.com/v1/.

<p>To use 3-Legged OAuth you need to call the following method of the sdk object after it's created.</p>

<pre class="prettyprint">
sdk.useThreeLegged(true);
</pre>
Once get the sdk object you may call the following method to set the Authorization Server host other than the default.

<pre class="prettyprint">
sdk.setAuthHost("&lt;AUTH HOST OTHER THAN DEFAULT&gt;");
</pre>
<h3>2. Check Session Status</h3>
<p>The SDK doesn't take care of saving token information. Application Developers need to take care of saving token information somewhere and set it (and check its validity) to the sdk object upon application restart.</p>

<h3>3. Sign in</h3>
<p>Use one of the following methods to sign an user in.</p>

<pre class="prettyprint">
sdk.authorize(Activity activity, String action, final DialogListener listener);
sdk.authorize(Activity activity, String action, final DialogListener listener, boolean useSecure);
</pre>
<b>activity</b>: (android.app.Activity) The Android activity relevant.<br/>
<b>action</b>: (String) Should be <i>ACSClient.ACTION_LOGIN</i>.<br/>
<b>useSecure</b>: (Boolean) Specify if HTTPS should be used for sending request. If not specified default to false.<br/>
<b>listener</b>: (com.appcelerator.cloud.sdk.oauth2.DialogListener) The listener object is used to provide various callbacks to the signing-in process. Please refer to the Listener for more detail. The most significant callback method is <i>onComplete(Bundle values)</i> where you can get token information by calling the following methods and save them as you want.

<pre class="prettyprint">
sdk.getAccessToken();
sdk.getAccessExpires();
</pre>
<p>The following code from the demo application shows a call to this method.</p>

<pre class="prettyprint">
sdk.authorize(UserView.this, ACSClient.ACTION_LOGIN, new LoginDialogListener(), false);
</pre>
<h3>4. Sign up</h3>
<p>Use one of the following methods to sign an user up.</p>

<pre class="prettyprint">
sdk.authorize(Activity activity, String action, final DialogListener listener);
sdk.authorize(Activity activity, String action, final DialogListener listener, boolean useSecure);
</pre>
<b>activity</b>: (android.app.Activity) The Android activity relevant.<br/>
<b>action</b>: (String) Should be <i>ACSClient.ACTION_SINGUP</i>.<br/>
<b>useSecure</b>: (Boolean) Specify if HTTPS should be used for sending request. If not specified default to false.<br/>
<b>listener</b>: (com.appcelerator.cloud.sdk.oauth2.DialogListener) The listener object is used to provide various callbacks to the signing-up process. Please refer to the Listenr class for more detail. The most significant callback method is <i>onComplete(Bundle values)</i> where you can get token information by calling the following methods and save them as you want.

<pre class="prettyprint">
sdk.getAccessToken();
sdk.getAccessExpires();
</pre>
<p>The following code from the demo application shows a call to this method.</p>

<pre class="prettyprint">
sdk.authorize(UserView.this, ACSClient.ACTION_SINGUP, new LoginDialogListener());
</pre>
<h3>5. Sign out</h3>
<p>Signing-out should be done the same way as before. That is calling sdk.sendRequest to send a request to <i>users/logout.json</i>.
</p>

<h3>6. Login to API Server directly</h3>
To use the new authorization flow, you need to configure your app on <a href="/apps">Apps page</a>. If an app is configured to use Authorization Server for user authentication, it's not possible to log-in/sign-up to API server directly.

<h3>7. Customize webview</h3>
The webview is used to show pages loaded from Authorization Server. It's possible to customize it. The SDK has the following method to accept a com.appcelerator.cloud.sdk.oauth2.DlgCustomizer object to support customization.

<pre class="prettyprint">
sdk.setDlgCustomizer(new MyDlgCustomizer());
</pre>
For example, the following code snippet from the demo application implements a DlgCustomizer named MyDlgCustomizer.

<pre class="prettyprint">
public class MyDlgCustomizer implements DlgCustomizer {

    static final int FB_BLUE = 0xFF6D84B4;
    static final int MARGIN = 4;
    static final int PADDING = 2;

    public float[] getPortraitDimensions() {
        return new float[]{320, 420};
    }

    public float[] getLandscapeDimensions() {
        return new float[]{460, 260};
    }

    public TextView setUpTitle(Context context) {
        Drawable icon = context.getResources().getDrawable(R.drawable.application_icon);
        TextView title = new TextView(context);
        title.setText("ACS - To be customized");
        title.setTextColor(Color.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setBackgroundColor(FB_BLUE);
        title.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
        title.setCompoundDrawablePadding(MARGIN + PADDING);
        title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        return title;
    }

}
</pre>


Have Fun!

</div>

LICENSE
------
This project is open source and provided under the Apache Public License (version 2). Please make sure you see the LICENSE file included in this distribution for more details on the license.

(C) Copyright 2012-2014, Appcelerator Inc. All Rights Reserved.
