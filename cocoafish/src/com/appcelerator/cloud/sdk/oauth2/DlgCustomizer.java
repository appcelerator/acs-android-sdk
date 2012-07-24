/**
 * 
 */
package com.appcelerator.cloud.sdk.oauth2;

import android.content.Context;
import android.widget.TextView;

/**
 * Developers should implement this interface and customize the dialog for showing 
 * authorization pages from authorization server.
 *
 */
public interface DlgCustomizer {
	
	public float[] getPortraitDimensions();
	
	public float[] getLandscapeDimensions();
	
	public TextView setUpTitle(Context context);
	
	

}
