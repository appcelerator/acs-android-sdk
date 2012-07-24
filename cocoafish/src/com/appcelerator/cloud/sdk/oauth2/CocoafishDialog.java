package com.appcelerator.cloud.sdk.oauth2;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appcelerator.cloud.demo.R;
import com.appcelerator.cloud.sdk.Cocoafish;
import com.appcelerator.cloud.sdk.CocoafishError;

public class CocoafishDialog extends Dialog {

    static final int FB_BLUE = 0xFF6D84B4;
    static final float[] DIMENSIONS_LANDSCAPE = {460, 260};
    static final float[] DIMENSIONS_PORTRAIT = {280, 420};
    static final FrameLayout.LayoutParams FILL =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;
  
    private String url;
    private DialogListener listener;
    private ProgressDialog spinner;
    private WebView webView;
    private LinearLayout content;
    private TextView title;
    Context context = null;
    private DlgCustomizer dlgCustomizer;

    public CocoafishDialog(Context _context, String url, DialogListener listener, DlgCustomizer dlgCustomizer) {
        super(_context);
        this.context = _context;
        this.url = url;
        this.listener = listener;
        this.dlgCustomizer = dlgCustomizer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        spinner = new ProgressDialog(getContext());
        spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinner.setMessage("Loading...");
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        content = new LinearLayout(getContext());
        content.setOrientation(LinearLayout.VERTICAL);

        if(this.dlgCustomizer != null) {
        	content.addView(this.dlgCustomizer.setUpTitle(getContext()));
        } else {
        	content.addView(this.setUpTitle());
        }
        
        setUpWebView();

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final float scale = getContext().getResources().getDisplayMetrics().density;

        if(this.dlgCustomizer != null) {
        	float[] dimensions = (display.getWidth() < display.getHeight())? this.dlgCustomizer.getPortraitDimensions() : this.dlgCustomizer.getLandscapeDimensions();
	        addContentView(content, new FrameLayout.LayoutParams(
	                (int) (dimensions[0] * scale + 0.5f),
	                (int) (dimensions[1] * scale + 0.5f)));
        } else {
        	float[] dimensions = (display.getWidth() < display.getHeight())? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
	        addContentView(content, new FrameLayout.LayoutParams(
	                (int) (dimensions[0] * scale + 0.5f),
	                (int) (dimensions[1] * scale + 0.5f)));
        }

    }

    
    private TextView setUpTitle() {
        Drawable icon = getContext().getResources().getDrawable(R.drawable.cocoafish_icon);
        title = new TextView(getContext());
        title.setText("Appcelerator Cloud Service");
        title.setTextColor(Color.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setBackgroundColor(FB_BLUE);
        title.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
        title.setCompoundDrawablePadding(MARGIN + PADDING);
        title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        return title;
    }

    
    private void setUpWebView() {
        webView = new WebView(getContext());
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new CocoafishWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setLayoutParams(FILL);
        content.addView(webView);
    }

    
    
    
    private class CocoafishWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	String method = "CocoafishDialog.CocoafishWebViewClient.shouldOverrideUrlLoading";
            
        	Log.d(method, "About to load: " + url);

            if (url.startsWith(Cocoafish.REDIRECT_URI)) {
            
            	Bundle values = Util.parseUrl(url);
                String error = values.getString("error");
                if (error == null) {
                    error = values.getString("error_type");
                }
                if (error == null) {
                    listener.onComplete(values);
                } else if (error.equals("access_denied") || error.equals("OAuthAccessDeniedException")) {
                    listener.onCancel();
                } else {
                    listener.onCocoafishError(new CocoafishError(error));
                }

                CocoafishDialog.this.dismiss();
                return true;

            } else if (url.startsWith(Cocoafish.CANCEL_URI)) {
                listener.onCancel();
                CocoafishDialog.this.dismiss();
                return true;
            } 
            
            // launch non-dialog URLs in a full browser
            //getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            listener.onError(new DialogError(description, errorCode, failingUrl));
            CocoafishDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	String method = "CocoafishDialog.CocoafishWebViewClient.onPageStarted";
            Log.d(method, "Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            spinner.show();
        }

//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            String title = webView.getTitle();
//            if (title != null && title.length() > 0) {
//                title.setText(title);
//            }
//            spinner.dismiss();
//        }
        
        @Override  
        public void onPageFinished(WebView view, String url) {  
        	String method = "CocoafishDialog.CocoafishWebViewClient.onPageFinished";
            super.onPageFinished(view, url);  
			if (view != null && view.getUrl() != null)
				Log.d(method, view.getUrl());
            spinner.dismiss();
        }  

    }
}

