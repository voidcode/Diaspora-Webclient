package com.voidcode.diasporawebclient;

import android.content.Context;
import android.widget.Toast;

import com.google.api.GoogleAPI;
import com.google.api.GoogleAPIException;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class JavaScriptInterface {
		Context mContext;
		
		JavaScriptInterface(Context c)
		{
			mContext = c;
		}
		public void translate()
		{			
			String text="string from jsinterface";
			// Set the HTTP referrer to your website address.
    	    GoogleAPI.setHttpReferrer("https://diasp.eu");

    	    // Set the Google Translate API key
    	    // See: http://code.google.com/apis/language/translate/v2/getting_started.html
    	    GoogleAPI.setKey("AIzaSyBsCR5RpRJdJDHj8Wuhqol_ZcStbYJ9vdM");

    	    String translatedText = null;
			try {
				translatedText = Translate.DEFAULT.execute(text, Language.DANISH, Language.ENGLISH);
			} catch (GoogleAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			   Toast.makeText(mContext, "TEST", Toast.LENGTH_SHORT).show();
    	    //System.out.println(translatedText);
		}
}
