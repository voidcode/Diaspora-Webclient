package com.voidcode.diasporawebclient;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ShareActivity extends MainActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNetworkAvailable()) 
        {
        	if(!this.main_domain.equals(""))
        	{	
		        WebSettings settings = mWeb.getSettings();
		        settings.setJavaScriptEnabled(true);// set Javascript
		        
		        //set cache size to 8mb by default.
		        settings.setCacheMode(1);
		        //settings.setAppCacheMaxSize(1024*1024*8);
		       // settings.setDomStorageEnabled(true);
		        //settings.setAppCachePath("/data/data/com.voidcode.diasporawebclient/cache");
		        //settings.setAllowFileAccess(true);
		        //settings.setAppCacheEnabled(true);
		        
		        //settings.setBuiltInZoomControls(true);
        		// load: open new messages
        		mWeb.loadUrl("https://"+main_domain+"/status_messages/new");
        		
        		//when you are on eg your default browser and choose 'share with', 
        		//and then choose 'Diaspora-Webclient' it goto here 
        		Intent intent = getIntent();
        		final Bundle extras = intent.getExtras();
        		String action = intent.getAction();

        		if (Intent.ACTION_SEND.equals(action)) //if user has 
        		{  
		        		mWeb.setWebViewClient(new WebViewClient() 
		        		{
				        	public void onPageFinished(WebView view, String url) 
				        	{
				        		if(mProgress.isShowing())
				        		{
				        			mProgress.dismiss();
				        			
				        		}
				        		//TODO user has to touch the 'textarea' before bookmarklink is paste in 'textarea'
				        		//this have to be intent
							    if(extras.containsKey(Intent.EXTRA_TEXT) && extras.containsKey(Intent.EXTRA_SUBJECT)) 
							    {
					        		final String extraText = (String) extras.get(Intent.EXTRA_TEXT);//get url on the site user will share
					        		final String extraSubject = (String) extras.get(Intent.EXTRA_SUBJECT);//get the url´s title
					        		
					        		//inject share pageurl into 'textarea' via javascript
						        	mWeb.loadUrl("javascript:(function() { " + 
							        	    			//make more space to user-message
							        	    			"document.getElementsByTagName('textarea')[0].style.height='110px'; "+
							        	                //inject formate bookmark
							        	    			"document.getElementsByTagName('textarea')[0].innerHTML = '["+extraSubject+"]("+extraText+") #bookmark '; " +  
							        	            "})()"); 
							    }
				        	}
				        });
        		}
        	}
        	else
        	{
        		this.finish();
        		startActivity(new Intent(this, SetupInternetActivity.class));
        	}
        }
    }
	//Screen orientation crashes app fix
	//http://jamesgiang.wordpress.com/2010/06/05/screen-orientation-crashes-my-app/
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
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
