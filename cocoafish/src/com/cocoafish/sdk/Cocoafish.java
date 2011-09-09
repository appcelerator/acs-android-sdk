package com.cocoafish.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class Cocoafish {

	private static final String COOKIES_FILE = "CocofishCookiesFile";
	private static CookieStore cookieStore = new BasicCookieStore();

	private String appId;
	private String oauthConsumerKey;
	private String oauthConsumerSecret;

	private CCRestfulRequest request;
	private CCUser currentUser;
	private Context curApplicationContext;
	private Facebook authenticatedFacebook = null; 
	private DialogListener customFacebookLoginListener = null; 
	
	
	
	// private constructor
	public Cocoafish(String appId, String facebookAppId, Context context) {
		this.appId = appId;
		this.curApplicationContext = context;
		request = new CCRestfulRequest(this);
		
		if (facebookAppId != null) {
			authenticatedFacebook = new Facebook(facebookAppId);
		}
	}
	
	// private constructor
	public Cocoafish(String consumerKey, String consumerSecret, String facebookAppId, Context context) {
		this.oauthConsumerKey = consumerKey;
		this.oauthConsumerSecret = consumerSecret;
		this.curApplicationContext = context;
		request = new CCRestfulRequest(this);
		
		if (facebookAppId != null) {
			authenticatedFacebook = new Facebook(facebookAppId);
		}
	}
	/**
	 * 
	 * @param actionUrl The last fragment of request url
	 * @param requestMethod It only can be one of CCRequestMthod.GET, CCRequestMthod.POST, CCRequestMthod.PUT, CCRequestMthod.DELETE.  
	 * @param dataMap The name-value pairs which is ready to be sent to server, 
	 * 			the value only can be String type or java.io.File type 
	 * @param useSecure Decide whether use http or https protocol.
	 * @return
	 * @throws IOException If there is network problem, the method will throw this type of exception.
	 * @throws CocoafishError If other problems cause the request cannot be fulfilled, the CocoafishError will be threw.
	 */
	public CCResponse sendRequest(String actionUrl, CCRequestMethod requestMethod, 
			   Map<String, Object> dataMap, boolean useSecure) throws IOException, CocoafishError{
		CCResponse response = null;
		
		List<NameValuePair> nameValuePairs = null;	// store all request parameters
		Map<String, File> nameFileMap = null;		// store the requested file and its parameter name
		
		if( dataMap != null && !dataMap.isEmpty() )
		{
			Iterator it = dataMap.keySet().iterator();
			while(it.hasNext()){
				String name = (String) it.next();
				
				Object value = dataMap.get(name);
				
				if( value instanceof String){
					if( nameValuePairs == null )
						nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair( name, (String)value ) );
				} else if (value instanceof File){
					if( nameFileMap == null)
						nameFileMap = new HashMap<String, File>();
					nameFileMap.put( name, (File) value );
				}
			}
		} 
		if( requestMethod == null || requestMethod.getTypeString() == null )
			throw new CocoafishError("The request method cannot be null.");
		
		String requestType = requestMethod.getTypeString();
		
		if( oauthConsumerKey!= null && this.oauthConsumerSecret != null )
		{
			response = request.sendRequestByOAuth(actionUrl, requestType, oauthConsumerKey, 
					oauthConsumerSecret, nameValuePairs, nameFileMap, useSecure);
		} else {
			response = request.sendRequestByAppKey(actionUrl, requestType, appId, 
					nameValuePairs, nameFileMap, useSecure);
		}
		return response;
	}
	
	public void facebookAhtorize(Activity activity, String[] permissions,
            final DialogListener customListener) {
		customFacebookLoginListener = customListener;
		authenticatedFacebook.authorize(activity, permissions, new FacebookLoginListener(this));
	}
	
	public Facebook getFacebook() {
		return authenticatedFacebook;
	}
	
	public String getAppId() {
		return appId;
	}

	public String getOauthConsumerKey() {
		return oauthConsumerKey;
	}

	public String getOauthConsumerSecret() {
		return oauthConsumerSecret;
	}

	public Context getCurApplicationContext() {
		return curApplicationContext;
	}
	
	public void setCurrentUser(CCUser user) {
		currentUser = user;
		if (user != null) {
			saveLoginInfo();
		} else {
			clearLoginInfo();
		}
	}
	
	public CCUser getCurrentUser() {
		return currentUser;
	}
	
	public CookieStore getCookieStore() {
		return cookieStore;
	}
	
	protected void loadLoginInfo() {
		   try {

			   // read the user cookies
			   FileInputStream fis = null;
			   ObjectInputStream in = null;
			 
			   fis = curApplicationContext.openFileInput(COOKIES_FILE);
			   in = new ObjectInputStream(fis);
			
			   currentUser = (CCUser)in.readObject();
			   int size = in.readInt();
			   for (int i = 0; i < size; i++) {
				   SerializableCookie cookie = (SerializableCookie)in.readObject();
		           cookieStore.addCookie(cookie);
			   }
			
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	   }
	   
	   // Saving current user name after login and signup
	protected void saveLoginInfo() {
		   // We need an Editor object to make preference changes.
		   try {
			   // save the cookies
			   List<Cookie> cookies = cookieStore.getCookies();
		        if (cookies.isEmpty()) {
		            // should not happen when we have a current user but no cookies
		        	return;
		        } else {
		        	final List<Cookie> serialisableCookies = new ArrayList<Cookie>(cookies.size());
		            for (Cookie cookie : cookies) {
		                serialisableCookies.add(new SerializableCookie(cookie));
		            }
		           
		            FileOutputStream fos = null;
		            ObjectOutputStream out = null;
		            try
		            {		            	
		            	fos = curApplicationContext.openFileOutput(COOKIES_FILE, Context.MODE_PRIVATE);
		            	out = new ObjectOutputStream(fos);
		            	out.writeObject(currentUser);
		            	out.writeInt(cookies.size());
		            	for (Cookie cookie : serialisableCookies) {
		            		out.writeObject(cookie);
		            	}
		            	out.flush();
		            	out.close();
		            }
		            catch(IOException ex)
		            {
		            	ex.printStackTrace();
		            }
		            
		        }
			   
			   
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	   }
	
	protected void clearLoginInfo() {
		curApplicationContext.getFileStreamPath(COOKIES_FILE).delete(); 
	}
	
    public class FacebookLoginListener implements DialogListener {
    	private Cocoafish cocoafish ;
    	private CCRestfulRequest request ;
    	public FacebookLoginListener(Cocoafish cocoafish){
    		this.cocoafish = cocoafish;
    		request = new CCRestfulRequest(this.cocoafish );
    	}

        public void onComplete(Bundle values) {
        	// login with cocoafish server using facebook access token
        	try {
				request.facebookUserLogin(authenticatedFacebook.getAccessToken(), authenticatedFacebook.getAppId());
			} catch (IOException e) {
				if (customFacebookLoginListener != null) {
					customFacebookLoginListener.onFacebookError(new FacebookError(e.getLocalizedMessage()));
				}
			} catch (CocoafishError e) {
				if (customFacebookLoginListener != null) {
					customFacebookLoginListener.onFacebookError(new FacebookError(e.getLocalizedMessage()));
				}
			}
			if (customFacebookLoginListener != null) {
				customFacebookLoginListener.onComplete(values);
			}
			customFacebookLoginListener = null;
        }

        public void onCancel() {
			customFacebookLoginListener = null;

        }

        public void onError(DialogError e) {
            e.printStackTrace();
			customFacebookLoginListener = null;

        }

        public void onFacebookError(FacebookError e) {
            e.printStackTrace();
			customFacebookLoginListener = null;
        }
    }

    // This method is just used for test, 
    // and it will be delete in the final version code.
	public static void initialize(String appId, String facebookAppId,
			Context applicationContext) {
		testFish = new Cocoafish(appId, facebookAppId, applicationContext);
		
	}

    // This method is just used for test, 
    // and it will be delete in the final version code.
	public static void initialize(String comsuerKey, String comsumerSecret, String facebookAppId,
			Context applicationContext) {
		testFish = new Cocoafish(comsuerKey, comsumerSecret, facebookAppId, applicationContext);
		
	}
	
	// This method is just used for test, 
    // and it will be delete in the final version code.
	public static Cocoafish getDefaultInstance() {
		return testFish;
	}
	
	public static Cocoafish testFish;
}