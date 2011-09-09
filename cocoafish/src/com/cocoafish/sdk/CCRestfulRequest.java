package com.cocoafish.sdk;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import oauth.signpost.signature.HmacSha1MessageSigner;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CCRestfulRequest {
   private static final String BACKEND_URL = "http://api.cocoafish.com/v1";
   private static final String BACKEND_URL_SECURE = "https://api.cocoafish.com/v1";
   private static final String TAG = "CCRestfulTask";
   public static final int DEFAULT_PER_PAGE = 10;
   public static final int FIRST_PAGE = 1;
   
   private HttpClient httpClient;
   private Cocoafish cocoafish;
   private CCResponse response = null;
   
   public synchronized CCResponse getResponse() {
	   return response;
   }
   
   public CCRestfulRequest(Cocoafish cocoafish)  {
	   this.cocoafish = cocoafish;
	   httpClient  = new DefaultHttpClient();
   }
   
   public synchronized void logoutUser() throws IOException {
	   
	   try {
		String requestUrl = BACKEND_URL + "/users/logout.json?key=" + cocoafish.getAppId();

		response = performGet(requestUrl);
		cocoafish.setCurrentUser(null);

	   } catch (CocoafishError e) {
		   e.printStackTrace();
	   }
	   
   }
   
   public synchronized CCUser facebookUserLogin(String accessToken, String facebookAppId) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/fb_login.json?key=" + cocoafish.getAppId();

	// Create post Data
	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	   nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
	   nameValuePairs.add(new BasicNameValuePair("facebook_app_id", facebookAppId));
	   
	   response = performPost(requestUrl, nameValuePairs);
	   
	   CCUser user = null;
	   if (response != null) {
		   if (response.getMeta().getCode() != 200) {
			   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
		   }
		   List<CCUser> users;
		   try {
			   users = response.getUsersFromResponse();
			   if (users != null && users.size() > 0) {
				   // set the current User object;
				   user = users.get(0);
				   cocoafish.setCurrentUser(user);
			   }
		   } catch (Exception e) {
			   throw (new CocoafishError(e.getLocalizedMessage()));
		   }
	   }
	   return user;   }
   
   public synchronized CCUser loginUser(String login, String password) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/users/login.json?key=" + cocoafish.getAppId();
	   
	   // Create post Data
	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	   nameValuePairs.add(new BasicNameValuePair("login", login));
	   nameValuePairs.add(new BasicNameValuePair("password", password));
	   
	   response = performPost(requestUrl, nameValuePairs);
	   
	   CCUser user = null;
	   if (response != null) {
		   if (response.getMeta().getCode() != 200) {
			   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
		   }
		   List<CCUser> users;
		   try {
			   users = response.getUsersFromResponse();
			   if (users != null && users.size() > 0) {
				   // set the current User object;
				   user = users.get(0);
				   cocoafish.setCurrentUser(user);
			   }
		   } catch (Exception e) {
			   throw (new CocoafishError(e.getLocalizedMessage()));
		   }
	   }
	   return user;
   }
   
   public synchronized CCUser registerUser(String email, String userName, String first, String last, String password) throws CocoafishError, IOException {
	   
	   // construct the request url
	   String requestUrl = 	BACKEND_URL + "/users/create.json?key=" + cocoafish.getAppId();
	   
	   // Add post data
	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	   nameValuePairs.add(new BasicNameValuePair("email", email));
	   nameValuePairs.add(new BasicNameValuePair("username", userName));
	   nameValuePairs.add(new BasicNameValuePair("first_name", first));
	   nameValuePairs.add(new BasicNameValuePair("last_name", last));
	   nameValuePairs.add(new BasicNameValuePair("password", password));
	   nameValuePairs.add(new BasicNameValuePair("password_confirmation", password));

	   response = performPost(requestUrl, nameValuePairs);

	   CCUser user = null;
	   if (response != null) {
		   if (response.getMeta().getCode() != 200) {
			   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
		   }
		   List<CCUser> users;
		   try {
			   users = response.getUsersFromResponse();
			   if (users != null && users.size() > 0) {
				   // set the current User object;
				   user = users.get(0);
				   cocoafish.setCurrentUser(user);
			   }
		   } catch (Exception e) {
			   throw (new CocoafishError(e.getLocalizedMessage()));
		   }
	   }
	   return user;
	}
   
   public synchronized void deleteUser() throws CocoafishError, IOException {
	   // construct the request url
	   String requestUrl = 	BACKEND_URL + "/users.json?app=" + cocoafish.getAppId();
	 
	   response = performDelete(requestUrl);
	   
	   if (response != null && response.meta.getCode() == 200) {
		   // delete was successful
		   cocoafish.setCurrentUser(null);
	   }

   }

   public synchronized List<CCPlace> getPlaces(int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/search.json?key=" + 
	   				cocoafish.getAppId()  + 
	   				"&page="+page+"&per_page=" + perPage;
	   response = performGet(requestUrl);
	   List<CCPlace> places = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   places = response.getPlacesFromResponse(); 
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return places;
   }
 
   public synchronized List<CCCheckin> getCheckinsForUser(String userId, int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/checkins/search.json?key=" + cocoafish.getAppId() + "&user_id=" + userId+
	   		"&page="+page+"&per_page=" + perPage;
	   response = performGet(requestUrl);
	   List<CCCheckin> checkins = null;

	   if (response != null && response.getMeta().getCode() == 200) {
		   checkins = response.getCheckinsFromResponse(); 
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return checkins;
   }
   
   public synchronized List<CCCheckin> getCheckinsForPlace(String placeId, int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/checkins/search.json?key=" + cocoafish.getAppId() + "&place_id=" + placeId +
	   		"&page="+page+"&per_page=" + perPage;
	   response = performGet(requestUrl);
	   
	   List<CCCheckin> checkins = null;

	   if (response != null && response.getMeta().getCode() == 200) {
		   checkins = response.getCheckinsFromResponse();
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return checkins;
   }
   
   public synchronized CCCheckin checkinPlace(String placeId) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/checkins/create.json?key=" + cocoafish.getAppId() + "&place_id="+placeId;
	   
	   CCCheckin checkin = null;
	   response = performPost(requestUrl, null);
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCCheckin> checkins = response.getCheckinsFromResponse();
		   if (checkins.size() > 0) {
			   checkin = checkins.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return checkin;
   }
   
   // user must be logged in
   public synchronized CCStatus createUserStatus(String newStatus) throws CocoafishError, IOException {
	   CCUser currentUser = cocoafish.getCurrentUser();
	   String requestUrl = BACKEND_URL + "/users/" + currentUser.getObjectId() + 
	   			"/status.json?key=" + cocoafish.getAppId();
	  
	   // Add post data
	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	   nameValuePairs.add(new BasicNameValuePair("status", newStatus));

	   CCStatus status = null;
	   response = performPost(requestUrl, nameValuePairs);
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCStatus> statuses = response.getStatusesFromResponse(); 
		   if (statuses.size() > 0) {
			   status = statuses.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return status;
   }
   
   public synchronized List<CCStatus> getUserStatuses(String userId, int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/users/" + userId + "/status/search.json?key=" + 
	   			cocoafish.getAppId()  + 
	   			"&page="+page+"&per_page=" + perPage;
	   response = performGet(requestUrl);
	   List<CCStatus> statuses = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   statuses = response.getStatusesFromResponse(); 
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return statuses;
   }
   
   public synchronized CCPlace getPlace(String placeId) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/" + placeId +".json?app=" + 
	   		cocoafish.getAppId();
	   
	   CCPlace place = null;
	   response = performGet(requestUrl);
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCPlace> places = response.getPlacesFromResponse(); 
		   if (places.size() > 0) {
			   place = places.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return place;
   }
   
   public synchronized CCStatus createPlaceStatus(String placeId, String newStatus) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/" + placeId + 
	   		"/status.json?app=" + cocoafish.getAppId();
	  
	   // Add post data
	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	   nameValuePairs.add(new BasicNameValuePair("status", newStatus));

	   response = performPost(requestUrl, nameValuePairs);
	   CCStatus status = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCStatus> statuses = response.getStatusesFromResponse(); 
		   if (statuses.size() > 0) {
			   status = statuses.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return status;
   }
   
   public synchronized List<CCStatus> getPlaceStatus(String placeId, int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/" + placeId + 
	   		"/status.json?app=" + cocoafish.getAppId() + 
	   		"&page="+page+"&per_page=" + perPage;
	   
	   response = performGet(requestUrl);
	   List<CCStatus> statuses = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   statuses = response.getStatusesFromResponse(); 
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return statuses;
   }
   
   public synchronized CCPhoto uploadPhoto(CCPlace place, File photoFile) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/" + place.getObjectId() + 
  		"/photos.json?app=" + cocoafish.getAppId();
	   
	   response = performUploadPhoto(requestUrl, null, photoFile);
	   CCPhoto photo = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCPhoto> photos = response.getPhotosFromResponse(); 
		   if (photos.size() > 0) {
			   photo = photos.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return photo;
   }
   
   public synchronized List<CCPhoto> getPhotosForPlace(String placeId, int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/" + placeId + 
  		"/photos.json?app=" + cocoafish.getAppId() + 
  		"&page="+page+"&per_page=" + perPage;
  
	   response = performGet(requestUrl);
	   List<CCPhoto> photos = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   photos = response.getPhotosFromResponse(); 
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return photos;
   }
   
   public synchronized CCPhoto getPhoto(String photoId) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/photos/" + photoId + 
  			".json?app=" + cocoafish.getAppId();
  
	   response = performGet(requestUrl);
	   CCPhoto photo = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCPhoto> photos = response.getPhotosFromResponse(); 
		   if (photos.size() > 0) {
			   photo = photos.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return photo;
   }
   
   public synchronized void storeKeyValue(String key, String value) throws CocoafishError, IOException {
	   	String requestUrl = BACKEND_URL + "/keyvalues.json?app=" + cocoafish.getAppId();
	   	
	    // Create post Data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("key", key));
		nameValuePairs.add(new BasicNameValuePair("value", value));

	   	response = performPost(requestUrl, nameValuePairs);
	   	
	   	if (response != null && response.getMeta().getCode() != 200) {
	   		throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   	}
 
   }
   
   public synchronized CCKeyValuePair getKeyValuePair(String key) throws CocoafishError, IOException {
	   String encodedKey = null;
	   try {
		   encodedKey = URLEncoder.encode(key, "UTF-8");
	   } catch (UnsupportedEncodingException e) {
		   throw new CocoafishError("Failed to encode key: " + key +". " + e.getLocalizedMessage());
	   }
	   String requestUrl = BACKEND_URL + "/keyvalues.json?app=" + cocoafish.getAppId() +
		   "&key="+encodedKey;
	   response = performGet(requestUrl);
	   CCKeyValuePair keyvalue = null;	
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCKeyValuePair> keyvalues = response.getKeyvaluesFromResponse();
		   if (keyvalues.size() > 0) {
			   keyvalue = keyvalues.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }   
	   return keyvalue;
   }
   
   private static String convertStreamToString(InputStream is) {
       /*
        * To convert the InputStream to String we use the BufferedReader.readLine()
        * method. We iterate until the BufferedReader return null which means
        * there's no more data to read. Each line will appended to a StringBuilder
        * and returned as String.
        */
       BufferedReader reader = new BufferedReader(new InputStreamReader(is));
       StringBuilder sb = new StringBuilder();

       String line = null;
       try {
           while ((line = reader.readLine()) != null) {
               sb.append(line + "\n");
           }
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           try {
               is.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       return sb.toString();
   }
   
   public CCResponse performGet(String urlString) throws CocoafishError, IOException {
	   return performRestful(urlString, "GET", null, null, null);
   }
   
   public CCResponse performDelete(String urlString) throws CocoafishError, IOException {
	   return performRestful(urlString, "DELETE", null, null, null);
   }
   
   public CCResponse performPost(String urlString, List<NameValuePair> nameValuePairs) throws CocoafishError, IOException {
	   return performRestful(urlString, "POST", nameValuePairs, null, null);
   }
   
   public CCResponse performPost(String urlString, List<NameValuePair> nameValuePairs, Map<String, File> nameFileMap) throws CocoafishError, IOException {
	   return performRestful(urlString, "POST", nameValuePairs, nameFileMap, null);
   }
   
   public CCResponse performPut(String urlString, List<NameValuePair> nameValuePairs) throws CocoafishError, IOException {
	   return performRestful(urlString, "PUT", nameValuePairs, null, null);
   }
   
   public CCResponse performPut(String urlString, List<NameValuePair> nameValuePairs, Map<String, File> nameFileMap) throws CocoafishError, IOException {
	   return performRestful(urlString, "PUT", nameValuePairs, nameFileMap, null);
   }
   
   public CCResponse performUploadPhoto(String urlString, List<NameValuePair> nameValuePairs, File photoFile) throws CocoafishError, IOException {
	   HashMap<String, File> map = new HashMap<String, File>();
	   map.put("photo", photoFile);
	   return performRestful(urlString, "POST", nameValuePairs, map , null);
   }
   
   protected CCResponse performRestful(String urlString, String requestType, 
		   List<NameValuePair> nameValuePairs, Map<String, File> nameFileMap, OAuthConsumer consumer) throws CocoafishError, IOException 
   {
	   CCResponse response = null;
	   Log.d(TAG, "performRestufl: " + requestType + ": " + urlString);
	   
	   try {
		   URL url = null;
		   HttpContext localContext = new BasicHttpContext();
	       localContext.setAttribute(ClientContext.COOKIE_STORE, cocoafish.getCookieStore());
	       HttpResponse httpResponse = null;
	       if (HttpPost.METHOD_NAME.equals(requestType) ) {
	    	   HttpPost pagePost = null;
	    	   
	    	   if (nameFileMap != null && nameFileMap.size() > 0) {
	    		   CCMultipartEntity entity = new CCMultipartEntity();
	    			StringBuffer uploadFileUrlString = new StringBuffer(urlString);
	    			
	    			// Append the nameValuePairs to request's url string
	    			if( nameValuePairs != null && !nameValuePairs.isEmpty() ) {
	    				Iterator<NameValuePair> nameValueIt = nameValuePairs.iterator();
	    				while(nameValueIt.hasNext()){
	    					NameValuePair nvpair = nameValueIt.next();
	    					
	    					entity.addPart(URLEncoder.encode(nvpair.getName()), 
	    							URLEncoder.encode(nvpair.getValue()) );
	    				}
	    			}
	    			
	    			url = new URL(uploadFileUrlString.toString());
	    			pagePost = new HttpPost(url.toURI());
	    			
	    			// Add up the file to request's entity.
	    			Set<String> nameSet = nameFileMap.keySet();
	    			Iterator<String> it = nameSet.iterator();
	    			if(it.hasNext()){
	    				String name = it.next();
	    				// Assume there is only one file in the map.
		    			entity.addPart( name, new FileBody(nameFileMap.get(name), "image/jpeg") );
	    			}
	    			pagePost.setEntity(entity);
	    	   } else if (nameValuePairs != null && !nameValuePairs.isEmpty()) {
	    		   url = new URL(urlString);
	    		   pagePost = new HttpPost(url.toURI());
	    		   pagePost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
	    	   }
	    	   
	    	   if(consumer != null) {
  		    	   try {
  					consumer.sign(pagePost);
  					} catch (OAuthMessageSignerException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					} catch (OAuthExpectationFailedException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					} catch (OAuthCommunicationException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					}
  	    	   	}
	    	   httpResponse = httpClient.execute(pagePost, localContext);
	       } else if ( HttpPut.METHOD_NAME.equals(requestType) ) {
	    	   HttpPut pagePut = null;
	    	   
	    	   if (nameFileMap != null && nameFileMap.size() > 0) {
	    			CCMultipartEntity entity = new CCMultipartEntity();
	    			StringBuffer uploadFileUrlString = new StringBuffer(urlString);
	    			
	    			// Append the nameValuePairs to request's url string
	    			if( nameValuePairs != null && !nameValuePairs.isEmpty() ) {
	    				Iterator<NameValuePair> nameValueIt = nameValuePairs.iterator();
	    				while(nameValueIt.hasNext()){
	    					NameValuePair nvpair = nameValueIt.next();
	    					entity.addPart(URLEncoder.encode(nvpair.getName()), 
	    							URLEncoder.encode(nvpair.getValue()) );
	    					
	    				}
	    			}
	    			
	    			url = new URL(uploadFileUrlString.toString());
	    			pagePut = new HttpPut(url.toURI());
	    			
	    			// Add up the file to request's entity.
	    			Set<String> nameSet = nameFileMap.keySet();
	    			Iterator<String> it = nameSet.iterator();
	    			if(it.hasNext()){
	    				String name = it.next();
	    				// Assume there is only one file in the map.
		    			entity.addPart( name, new FileBody(nameFileMap.get(name), "image/jpeg") );
	    			}

	    			pagePut.setEntity(entity);
	    	   } else if (nameValuePairs != null && !nameValuePairs.isEmpty()) {
	    		   url = new URL(urlString);
	    		   pagePut = new HttpPut(url.toURI());
	    		   pagePut.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
	    	   }
	    	   
	    	   if(consumer != null) {
  		    	   try {
  					consumer.sign(pagePut);
  					} catch (OAuthMessageSignerException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					} catch (OAuthExpectationFailedException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					} catch (OAuthCommunicationException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					}
  	    	   	}
		       httpResponse = httpClient.execute(pagePut, localContext);
	       } else if ( HttpGet.METHOD_NAME.equals(requestType) ) {
	    	   url = new URL(urlString);
	    	   HttpGet pageGet = new HttpGet(url.toURI());
	    	   
	    	   if(consumer != null) {
  		    	   try {
  					consumer.sign(pageGet);
  					} catch (OAuthMessageSignerException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					} catch (OAuthExpectationFailedException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					} catch (OAuthCommunicationException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					}
  	    	   	}
	    	   httpResponse = httpClient.execute(pageGet, localContext);
	       } else if ( HttpDelete.METHOD_NAME.equals(requestType) ) {
	    	   url = new URL(urlString);
	    	   HttpDelete pageDelete = new HttpDelete(url.toURI());
	    	   
	    	   if(consumer != null) {
  		    	   try {
  					consumer.sign(pageDelete);
  					} catch (OAuthMessageSignerException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					} catch (OAuthExpectationFailedException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					} catch (OAuthCommunicationException e) {
  						throw new CocoafishError(e.getLocalizedMessage());
  					}
  	    	   	}
		       httpResponse = httpClient.execute(pageDelete, localContext);
	       }
	       HttpEntity entity = httpResponse.getEntity();
	     
	       if (entity != null) {

	    	   // A Simple JSON Response Read
	           InputStream instream = entity.getContent();
	           String result= convertStreamToString(instream);
	           Log.d(TAG,result);

	           // A Simple JSONObject Creation
	    	   JSONObject jsonObject = new JSONObject(result);
	           Log.d(TAG,"<jsonobject>\n"+jsonObject.toString()+"\n</jsonobject>");
			   response = new CCResponse(jsonObject);
	       }
	   } catch (URISyntaxException e) {
		   throw new CocoafishError(e.getLocalizedMessage());
	   } catch (JSONException e) {
		   throw new CocoafishError("Invalid server response: " + e.getLocalizedMessage());
	   } catch (IOException e) {
		   throw new CocoafishError(e.getLocalizedMessage());
	   }
	   
	   return response;
   }
   
   public CCResponse sendRequestByAppKey( String actionUrl, String requestType, String appKey, 
		   List<NameValuePair> nameValuePairs, Map<String, File> nameFileMap, boolean useSecure) throws CocoafishError, IOException
   {
	   CCResponse response = null;
	   StringBuffer urlsb = null;
	   if(useSecure){
		   urlsb = new StringBuffer( BACKEND_URL_SECURE );
	   } else {
		   urlsb = new StringBuffer( BACKEND_URL );
	   }
	   
	   // construct full request url
	   // TODO NEED A VALUE CHECK
	   if( !actionUrl.startsWith("/") )
		   urlsb.append("/");
	   urlsb.append(actionUrl);
	   
	   // Append the appkey to url.
	   if(appKey == null || appKey.trim().length() == 0 )
		   throw new CocoafishError("The AppKey cannot be empty.");
	   urlsb.append("?key=").append(appKey);
	   
	   if( requestType == null || requestType.trim().length() == 0 )
		   throw new CocoafishError("The request type parameter cannot be empty.");
	   
	   requestType = requestType.toUpperCase();
	   
	   if ( HttpPost.METHOD_NAME.equals(requestType) ) { // Do POST
		   if(nameFileMap == null || nameFileMap.size() == 0)
			   response = performPost(urlsb.toString(), nameValuePairs);
    	   else
    		   response = performPost(urlsb.toString(), nameValuePairs, nameFileMap);
       
       } else if ( HttpPut.METHOD_NAME.equals(requestType) ) { // Do PUT
    	   if(nameFileMap == null || nameFileMap.size() == 0)
    		   response = performPut(urlsb.toString(), nameValuePairs);
    	   else
    		   response = performPut(urlsb.toString(), nameValuePairs, nameFileMap);
       } else if ( HttpGet.METHOD_NAME.equals(requestType) ) { // Do GET
    	   if( nameValuePairs != null )
	    	   for( int i = 0 ; i < nameValuePairs.size() ; i++ ){
	    		   NameValuePair pair = nameValuePairs.get(i);
	    		   urlsb.append("&").append(pair.getName()).append("=").append(pair.getValue());
	    	   }
    	   response = performGet(urlsb.toString());
       } else if ( HttpDelete.METHOD_NAME.equals(requestType) ) { // Do DELETE
    	   if( nameValuePairs != null )
    		   for( int i = 0 ; i < nameValuePairs.size() ; i++ ){
	    		   NameValuePair pair = nameValuePairs.get(i);
	    		   urlsb.append("&").append(pair.getName()).append("=").append(pair.getValue());
	    	   }
    	   response = performDelete(urlsb.toString());
       } else {
    	   throw new CocoafishError("Not supported http method.");
       }
	   return response;
   }

   public CCResponse sendRequestByOAuth( String actionUrl, String requestType, String consumer_key, 
		   String consumer_secret, List<NameValuePair> nameValuePairs, 
		   Map<String, File> nameFileMap, boolean useSecure) throws CocoafishError, IOException
   {
	   CCResponse response = null;
	   StringBuffer urlsb = null;
	   if(useSecure){
		   urlsb = new StringBuffer( BACKEND_URL_SECURE );
	   } else {
		   urlsb = new StringBuffer( BACKEND_URL );
	   }
	   
	   // construct full request url
	   // TODO NEED A VALUE CHECK
	   if( !actionUrl.startsWith("/") )
		   urlsb.append("/");
	   urlsb.append(actionUrl);
	   
	   // Append the appkey to url.
	   if( consumer_key == null || consumer_key.trim().length() == 0 
			   || consumer_secret == null || consumer_secret.trim().length() == 0 )
		   throw new CocoafishError("The cosumer key or comsumer secret cannot be empty.");
	   
	   OAuthConsumer consumer = new CommonsHttpOAuthConsumer( consumer_key, consumer_secret );
	   consumer.setMessageSigner(new HmacSha1MessageSigner());
	   consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
	   
	   if( requestType == null || requestType.trim().length() == 0 )
		   throw new CocoafishError("The request type parameter cannot be empty.");
	   
	   requestType = requestType.toUpperCase();
	   response = performRestful( urlsb.toString(), requestType, nameValuePairs, nameFileMap, consumer);
	   return response;
   }
   
   /*
    * Rewrite the MultipartEntity class to send a request with self-constructed body content.
    */
   public class CCMultipartEntity implements HttpEntity {

	    private final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	    private String boundary = null;

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    boolean isSetLast = false;
	    boolean isSetFirst = false;

	    public CCMultipartEntity() {
	        final StringBuffer buf = new StringBuffer();
	        final Random rand = new Random();
	        for (int i = 0; i < 30; i++) {
	            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
	        }
	        this.boundary = buf.toString();

	    }

	    public void addPart(String name, FileBody fileBody) {
	    	try {
				this.addPart(name, fileBody.getFile().getName(), new FileInputStream(fileBody.getFile()), fileBody.getMimeType());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		public void writeFirstBoundaryIfNeeds(){
	        if(!isSetFirst){
	            try {
	                out.write(("--" + boundary + "\r\n").getBytes());
	            } catch (final IOException e) {
	            	System.out.println(e.getLocalizedMessage());
	                //Log.e(Constants.TAG, e.getMessage(), e);
	            }
	        }
	        isSetFirst = true;
	    }

	    public void writeLastBoundaryIfNeeds() {
	        if(isSetLast){
	            return ;
	        }
	        try {
	            out.write(("\r\n--" + boundary + "--\r\n").getBytes());
	        } catch (final IOException e) {
	        	System.out.println(e.getLocalizedMessage());
	        }
	        isSetLast = true;
	    }

	    public void addPart(final String key, final String value) {
	        writeFirstBoundaryIfNeeds();
	        try {
	            out.write(("Content-Disposition: form-data; name=\"" +key+"\"\r\n\r\n").getBytes());
	            out.write(value.getBytes());
	            out.write(("\r\n--" + boundary + "\r\n").getBytes());
	        } catch (final IOException e) {
	        	System.out.println(e.getLocalizedMessage());
	        }
	    }

	    public void addPart(final String key, final String fileName, final InputStream fin){
	        addPart(key, fileName, fin, "application/octet-stream");
	    }

	    public void addPart(final String key, final String fileName, final InputStream fin, String type){
	        writeFirstBoundaryIfNeeds();
	        try {
	            type = "Content-Type: "+type+"\r\n";
	            out.write(("Content-Disposition: form-data; name=\""+ key+"\"; filename=\"" + fileName + "\"\r\n").getBytes());
	            out.write(type.getBytes());
	            out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

	            final byte[] tmp = new byte[4096];
	            int l = 0;
	            while ((l = fin.read(tmp)) != -1) {
	                out.write(tmp, 0, l);
	            }
	            out.flush();
	        } catch (final IOException e) {
	        	System.out.println(e.getLocalizedMessage());
	        } finally {
	            try {
	                fin.close();
	            } catch (final IOException e) {
	            	System.out.println(e.getLocalizedMessage());
	            }
	        }
	    }

	    public void addPart(final String key, final File value) {
	        try {
	            addPart(key, value.getName(), new FileInputStream(value));
	        } catch (final FileNotFoundException e) {
	        	System.out.println(e.getLocalizedMessage());
	        }
	    }

	    public long getContentLength() {
	        writeLastBoundaryIfNeeds();
	        return out.toByteArray().length;
	    }

	    public Header getContentType() {
	        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
	    }

	    public boolean isChunked() {
	        return false;
	    }

	    public boolean isRepeatable() {
	        return false;
	    }

	    public boolean isStreaming() {
	        return false;
	    }

	    public void writeTo(final OutputStream outstream) throws IOException {
	        outstream.write(out.toByteArray());
	    }

	    public Header getContentEncoding() {
	        return null;
	    }

	    public void consumeContent() throws IOException,
	    UnsupportedOperationException {
	        if (isStreaming()) {
	            throw new UnsupportedOperationException(
	            "Streaming entity does not implement #consumeContent()");
	        }
	    }

	    public InputStream getContent() throws IOException,
	    UnsupportedOperationException {
	        return new ByteArrayInputStream(out.toByteArray());
	    }

	}
   
   
}
