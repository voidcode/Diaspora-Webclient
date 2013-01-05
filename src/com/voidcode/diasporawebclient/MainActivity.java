package com.voidcode.diasporawebclient;

import java.security.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.voidcode.diasporawebclient.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String SETTINGS_FILENAME="settings";
	public static final String TRANSLATE_FILENAME="translate_settings";
	public String main_domain;
	public WebView mWeb;
	public ProgressDialog mProgress;
	public Date TimesindsLastWebviewUpdate = new Date();
	public SharedPreferences sp_currentpod;
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        if (isNetworkAvailable()) 
	        {
	        	// load main domain�s rooturl
	        	SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
	        	this.main_domain = preferences.getString("currentpod", ""); 
	        	
	        	// set the home screen
	        	setContentView(R.layout.main);
	        	mWeb = (WebView) findViewById(R.id.webView_main);
	        	
	        	//--------------------------------------------------------------------------//
	        	//start up the webbrowser------------------------------------------------START
	        	//--------------------------------------------------------------------------//
		        WebSettings settings = mWeb.getSettings();
		        settings.setJavaScriptEnabled(true);// set Javascript
		        
		        //settings.setRenderPriority(RenderPriority.HIGH);
		        
		        
		        settings.setCacheMode(WebSettings.LOAD_NORMAL);
		        //set cache size to 8mb by default.
		        //settings.setCacheMode(WebSettings.LOAD_NORMAL);
		        //settings.setAppCacheMaxSize(1024*1024*8);
		        //settings.setDomStorageEnabled(true);
		        //settings.setAppCachePath("/data/data/com.voidcode.diasporawebclient/cache");
		        //settings.setAllowFileAccess(true);
		        //settings.setAppCacheEnabled(true);
		        
		        //settings.setBuiltInZoomControls(true);
	        	
		        // adds JSInterface class to webview
		        if(userHasEnableTranslate())
	    		{	
	    			mWeb.addJavascriptInterface(new JSInterface(), "jsinterface");
	    		}
		        //fix to bug 2: cannot reshare
		        //see: https://github.com/voidcode/Diaspora-Webclient/issues/2
		        mWeb.setWebChromeClient(new WebChromeClient() {
		        	public boolean onJsAlert(WebView view, String url, String message, JsResult result)
		            {    
		        		return super.onJsAlert(view, url, message, result);
		            }
		        });	
		        mWeb.setWebViewClient(new WebViewClient() {
		        	private String googleapikey;
					private String defaultlanguage;
					private Matcher matcher;
					private Pattern pattern = Pattern.compile("^(https?)://"+main_domain+"[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
					// load url
		        	public boolean shouldOverrideUrlLoading(WebView view, String url) 
		        	{
		        		//this see if the user is trying to open a internel or externel link
		        		if (pattern.matcher(url).matches()) //if internel(on main_domain) eks: joindiaspora.com
		        		{  
		        	         view.loadUrl(url);
		        	         return true;  
		        	    } 
		        		else // if user try to open a externel link, then open it in the default webbrowser.
		        		{
		        			 Intent i = new Intent(Intent.ACTION_VIEW);
		        			 i.setData(Uri.parse(url));
		  	               	 startActivity(i);
		  	               	 return true;
		        	    }
		        	}
		        	public void onPageFinished(WebView view, String url) { // when finish loading page
		        		if(mProgress.isShowing()) {
		        			mProgress.dismiss();
		        		}
		        		else
		        		{
			        		if(userHasEnableTranslate())//adds translate link to all post
			        		{
			        			SharedPreferences preferences = getSharedPreferences(TRANSLATE_FILENAME, MODE_PRIVATE);
			        	    	this.googleapikey = preferences.getString("googleapikey", "microsoft-translator");
			        	    	this.defaultlanguage = preferences.getString("defaultlanguage", "en");//default-language=english
				        		//Inject google translate link via javascript into all posts
				        	    mWeb.loadUrl("javascript:(function() { " +  
				        	    			//get variables			        	    			"var i=0; "+
				        	    			"var ltrs=document.getElementsByClassName('ltr'); "+
				        	    			//loop: adds translate buttons to all 'ltr' tags
				        	    			"for(i=0;i<ltrs.length;i++) "+
				        	    			"{"+ 
				        	    				"var btn = document.createElement('div'); "+//makes new div
				        	    				"var selectpost = encodeURIComponent(ltrs.item(i).innerHTML); "+//retrive select post
				        	    				//"var selectpost = 'google is a search '; "+
				        	    				"btn.setAttribute('onclick','alert(window.jsinterface.Translate( \""+main_domain+"\",  \""+this.googleapikey+"\", \""+this.defaultlanguage+"\", \"'+selectpost+'\" ));'); "+//adds onclick-handler
				        	    				"btn.setAttribute('style','margin:15px 0px 15px 0px;'); "+//adds style
				        	    				"btn.id='btn_translate_id_'+i; "+//adds id
				        	    				"btn.innerHTML='Translate this post'; "+//title on link.
				        	    				//append new button to post '.ltr'
				        	    				"ltrs.item(i).appendChild(btn); "+ 
				        	    			"} "+
				        	                "})()");  
			        		}
		        		}
		        	}
		        });
		        //------------------------------------------------------------------------//
		        //start up the webbrowser------------------------------------------------END
		        //------------------------------------------------------------------------//
	        	
	        	//if user don�t has set a currentpod
	        	//then open SettingsActivity
	        	if(this.main_domain.equals("")) 
	        	{
	        		startActivity(new Intent(this, PodSettingsActivity.class));	
	        	}
	        	else
	        	{
		        		mProgress = ProgressDialog.show(this, "Stream", "Please wait a moment...");
		        		this.mWeb.loadUrl("https://" + main_domain+"/stream");//goto start or logon pages
		        		
		        		// goto users stream
		        		Toast.makeText(this, "Pod: "+main_domain, Toast.LENGTH_SHORT).show();
	        		
	        	}
	        }
	        else
	        {
	        	// if user don�t have internet
	        	this.finish();
        		startActivity(new Intent(this, SetupInternetActivity.class));
	        }
	    }
		//if user has added a google-api-key, returns 'true' else 'false'
		public boolean userHasEnableTranslate()
		{
			// load google-api-key 
	    	SharedPreferences preferences = getSharedPreferences("translate_settings", MODE_PRIVATE);
	    	String googleapikey = preferences.getString("googleapikey", "");
	    	if(!googleapikey.equals(""))//if user has added a google-api-key
	    		return true;
	    	else if(googleapikey.toLowerCase().equals("microsoft-translator"))
	    		return true;
	    	else
	    		return false;
		}
		public void onclick_stream(View v)
		{
			mProgress = ProgressDialog.show(this, "Stream", "Please wait a moment...");
			this.mWeb.loadUrl("https://" + main_domain+"/stream");//goto start or logon pages
		}
		public void onclick_share(View v)
		{
			mProgress = ProgressDialog.show(this, "StatusMessage", "Please wait a moment...");
			this.mWeb.loadUrl("https://" + main_domain+"/status_messages/new");//goto new status message
		}
		public void onclick_contacts(View v)
		{
			mProgress = ProgressDialog.show(this, "Contacts", "Please wait a moment...");
			this.mWeb.loadUrl("https://" + main_domain+"/contacts");//goto contacts
		}
		  
		public void onclick_search(View v)
		{
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);
			alert.setView(input);
			alert.setTitle(R.string.search_alert_title);
			alert.setPositiveButton("By people", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String inputtag = input.getText().toString().trim();
					// this validate the input data for tagfind
					if(inputtag.equals("") || inputtag.equals(null))
					{
						dialog.cancel(); // if user don�t have added a tag
						Toast.makeText(getApplicationContext(), R.string.search_alert_bypeople_validate_needsomedata, Toast.LENGTH_LONG).show();
					}
					else // if user have added a search tag
					{
						mWeb.loadUrl("https://"+main_domain+"/people.mobile?q="+inputtag);
					}
				}
			});
			alert.setNegativeButton("By tags",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String inputtag = input.getText().toString().trim();
							// this validate the input data for tagfind
							if(inputtag == null || inputtag.length() == 0)
							{
								dialog.cancel(); // if user hasn't added a tag
								Toast.makeText(getApplicationContext(), R.string.search_alert_bytags_validate_needsomedata, Toast.LENGTH_LONG).show();
							}
							else // if user have added a search tag
							{
								mWeb.loadUrl("https://"+main_domain+"/tags/"+inputtag);
							}
						}
					});
			alert.show();
		}
		//Screen orientation crashes app fix
		//http://jamesgiang.wordpress.com/2010/06/05/screen-orientation-crashes-my-app/
		@Override
		public void onConfigurationChanged(Configuration newConfig) 
		{
			super.onConfigurationChanged(newConfig);
		} 
		
		    // Handle the Back button in WebView, to back in history.
		    @Override
		    public boolean onKeyDown(int keyCode, KeyEvent event) {
		        if (keyCode == KeyEvent.KEYCODE_BACK){    
		            if(mWeb.canGoBack()){
		            	mWeb.goBack();
		                return true;
		            }
		        }
		        return super.onKeyDown(keyCode, event); 
		    }
		    //Build the main menu in MainActivity
		    @Override
		    public boolean onCreateOptionsMenu(Menu menu) 
		    {
		        MenuInflater inflater = getMenuInflater();
		        inflater.inflate(R.menu.main_menu, menu);
		        return true;
		    }
		    @Override
		    public boolean onOptionsItemSelected(MenuItem item) {
		    	// Handle item selection
			    switch (item.getItemId()) 
			    {
				    case R.id.mainmenu_share:
				    	mProgress = ProgressDialog.show(this, "Status-message", "Please wait a moment...");
				    	// load: open new messages
		        		mWeb.loadUrl("https://"+main_domain+"/status_messages/new");
				        return true;
				    case R.id.mainmenu_translate:
				    	startActivity(new Intent(this, TranslateActivity.class));
				    	return true;
				    case R.id.mainmenu_podsettings:
				    	startActivity(new Intent(this, PodSettingsActivity.class));
				    	return true;
					case R.id.mainmenu_tips:
						mProgress = ProgressDialog.show(this, "Tips", "Please wait a moment...");
				    	mWeb.loadUrl("file:///android_asset/tips.html");
				    	return true;		
				    default:
				        return super.onOptionsItemSelected(item);
			    }
		   }
		    private boolean isNetworkAvailable()
			{
				ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mobileInfo = connec.getNetworkInfo(0);
				NetworkInfo wifiInfo = connec.getNetworkInfo(1);
				NetworkInfo wimaxInfo = connec.getNetworkInfo(6);
				
				//fix to bug 14 by vrthra: https://github.com/voidcode/Diaspora-Webclient/issues/14
				boolean bm = false;
				boolean bw = false;
				boolean bx = false;
				if (mobileInfo != null) bm = mobileInfo.isConnected();
				if (wimaxInfo != null) bx = wimaxInfo.isConnected();
				if (wifiInfo != null) bw = wifiInfo.isConnected();
				return (bm || bw || bx);
			}
}
