package com.voidcode.diasporawebclient;

import android.util.Log;
import android.webkit.WebView;

public class JSInterface {
	private WebView mWeb;
	public JSInterface(WebView view)
	{
		this.mWeb=view;
	}
	//TODO
	//is like the javascript function not is inject or maby see on 'response' parmeter type
	public void GoogleV2TranslateComplete(Object response)
	{	
		Log.i("GoogleV2TranslateComplete", "face 2: starts - response:"+response);
		
		//Inject google translate via javascript to page-header
	    mWeb.loadUrl("javascript:(function() { " + 
	    				"var body=document.getElementsByClassName('ltr').item(0); "+
			    		"if (response.error) body.innerHTML(response.error.message); "+
						"else body.innerHTML(response.data.translations[0].translatedText); "+ 
	    			"} "+
	                "})()");  
	    Log.i("GoogleV2TranslateComplete", "face 2: Complete");
	}
	public void GoogleV2TranslateStart(String btn_translate_id_ID)
	{	
		String ltrID=btn_translate_id_ID.substring(17);//remove 'btn_translate_id_?' to get postID
		String google_api_key="AIzaSyC4x_B8s8M6eIsjUdMPq2uUDH81lkE0Ls0";//users google-api-key
		
		//Inject google-translate-page-header
	    mWeb.loadUrl("javascript:(function() { " +  
						"var el = document.createElement('script'); "+
						"el.src = 'https://www.googleapis.com/language/translate/v2'; "+
						"el.src += '?callback=GoogleV2TranslateComplete'; "+
						"el.src += '&key="+google_api_key+"'; "+
						"var posttext = 'hello world'; "+
						"el.src += '&q=' + escape (posttext); "+
						"el.src += '&target=da' "+
						"document.getElementsByTagName('head')[0].appendChild(el); "+ 
	    			"} "+
	                "})()");  
	    Log.i("GoogleV2TranslateStart", "face 1: Complete");
	}
}