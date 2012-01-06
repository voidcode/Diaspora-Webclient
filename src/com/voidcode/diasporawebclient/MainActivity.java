package com.voidcode.diasporawebclient;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
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
	public SharedPreferences sp_currentpod;
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        if (isNetworkAvailable()) 
	        {
	        	// set the home screen
	        	setContentView(R.layout.main);
	        	
	        	// load main domain´s rooturl
	        	SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
	        	this.main_domain = preferences.getString("currentpod", ""); 
	        	
	        	//if user don´t has set a currentpod
	        	//then open SettingsActivity
	        	if(this.main_domain.equals("")) 
	        	{
	        		startActivity(new Intent(this, SettingsActivity.class));	
	        	}
	        	else
	        	{
	        		// goto users stream
	        		startDiasporaBrowser("/stream");
	        		Toast.makeText(getApplicationContext(), "Pod: "+main_domain, Toast.LENGTH_SHORT).show();
	        	}
	        }
	        else
	        {
	        	// if user don´t have internet
	        	this.finish();
        		startActivity(new Intent(this, SetupInternetActivity.class));
	        }
	    }
		//if user has added a google-api-key, returns 'true' else 'false'
		public boolean userHasEnableTranslate()
		{
			// load google-api-key 
	    	SharedPreferences preferences = getSharedPreferences("translate_settings", MODE_PRIVATE);
	    	String googleapikey = preferences.getString("googleapikey", "microsoft-translator");
	    	if(!googleapikey.equals(""))//if user has added a google-api-key
	    		return true;
	    	else if(googleapikey.toLowerCase().equals("microsoft-translator"))
	    		return true;
	    	else
	    		return false;
		}
		public void onclick_stream(View v)
		{
			startDiasporaBrowser("/stream");
		}
		public void onclick_share(View v)
		{
			startDiasporaBrowser("/status_messages/new");
		}
		public void onclick_contacts(View v)
		{
			startDiasporaBrowser("/contacts");
		}
		  
		public void onclick_findtag(View v)
		{
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);
			alert.setView(input);
			alert.setTitle(R.string.findtag_alert_title);
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String inputtag = input.getText().toString().trim();
					// this validate the input data for tagfind
					if(inputtag.equals("") || inputtag.equals(null))
					{
						dialog.cancel(); // if user don´t have added a tag
						Toast.makeText(getApplicationContext(), R.string.findtag_alert_validate_needsomedata, Toast.LENGTH_LONG).show();
					}
					else // if user have added a search tag
					{
						startDiasporaBrowser("/tags/"+inputtag);
					}
				}
			});
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
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
		public void startDiasporaBrowser(String uri)
		{
			 	mWeb = (WebView) findViewById(R.id.webView_main);
		        // set Javascript
		        WebSettings settings = mWeb.getSettings();
		        settings.setJavaScriptEnabled(true);
		        
		        //set cache size to 8mb by default.
		        settings.setAppCacheMaxSize(1024*1024*8);
		        settings.setDomStorageEnabled(true);
		        settings.setAppCachePath("/data/data/com.voidcode.diasporawebclient/cache");
		        settings.setAllowFileAccess(true);
		        settings.setAppCacheEnabled(true);
		        
		        //settings.setBuiltInZoomControls(true);
		        
		        //get current uri an format it into a title for the AlertDialog
		        String loadingmsg=uri.substring(1, 2).toUpperCase()+uri.substring(2); 
		        if(uri.equals("/status_messages/new")) //this is just a workaround
		        {
		        	loadingmsg = "Share it";
		        } 
		        //the init state of progress dialog
		        mProgress = ProgressDialog.show(this, loadingmsg, "Please wait a moment...");
		        //fix to bug 2: cannot reshare
		        //see: https://github.com/voidcode/Diaspora-Webclient/issues/2
		        mWeb.setWebChromeClient(new WebChromeClient() {
		        	public boolean onJsAlert(WebView view, String url, String message, JsResult result)
		            {            
		        		 return super.onJsAlert(view, url, message, result);
		            }
		        });
		        // adds JSInterface class to webview
		        if(userHasEnableTranslate())
		        	mWeb.addJavascriptInterface(new JSInterface(), "jsinterface");
		        
		        mWeb.setWebViewClient(new WebViewClient() {
		        	private String googleapikey;
					private String defaultlanguage;
					// load url
		        	public boolean shouldOverrideUrlLoading(WebView view, String url) 
		        	{
		        		//this see if the user is trying to open a internel or externel link
		        		Pattern pattern = Pattern.compile("^(https?)://"+main_domain+"[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		        		Matcher matcher = pattern.matcher(url);
		        		if (matcher.matches()) //if internel(on main_domain) eks: joindiaspora.com
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
		        });      
		        // open pages in webview
				mWeb.loadUrl("https://" + main_domain+uri);
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
				    	startDiasporaBrowser("/status_messages/new");
				        return true;
				    case R.id.mainmenu_translate:
				    	this.finish();
				    	startActivity(new Intent(this, TranslateActivity.class));
				    	return true;
				    case R.id.mainmenu_settings:
				    	this.finish();
				    	startActivity(new Intent(this, SettingsActivity.class));
				    	return true;
					case R.id.mainmenu_tips:
				    	 mWeb.loadUrl("file:///android_asset/tips.html");
				    	return true;
				    case R.id.mainmenu_donation:
				    	 mWeb.loadUrl("file:///android_asset/donation.html");
				    	return true;	
				    case R.id.mainmenu_exit:
				    	this.finish();
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
		    	if (wimaxInfo!=null) {
		    		return mobileInfo.isConnected() || wifiInfo.isConnected()|| wimaxInfo.isConnected();
		    	}
		    	else {
		    		return mobileInfo.isConnected() || wifiInfo.isConnected();
		    	}
		   }
}