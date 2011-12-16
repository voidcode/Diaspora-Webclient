package com.voidcode.diasporawebclient;

import java.net.URLDecoder;

import com.google.api.GoogleAPI;
import com.google.api.GoogleAPIException;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class JSInterface {
	public JSInterface()
	{
	}
	public String GoogleTranslate(String main_domain, String googleapikey, String defaultlanguage, String text) throws GoogleAPIException
	{ 
		String decodetext = URLDecoder.decode(text);
		String rawtext = decodetext.replaceAll("\\<.*?\\>", "");//remove all htmltags
		//TODO: find out if google is tacking money for 'none' translate word(HEX23...the..word),
		//if, then make it so all tag is remove form the 'rawtext' and then add it back to the 'translatePost'(after: Translate.DEFAULT.execute)
		rawtext = rawtext.replaceAll("#", "HEX23");//format all #tags so google-translate don´t translate the #tag
		rawtext = rawtext.trim();//remove end-spaces 	
		// Set the HTTP referrer to your website address.
		GoogleAPI.setHttpReferrer(main_domain);
		// Set the Google Translate API key
		// See: http://code.google.com/apis/language/translate/v2/getting_started.html
		GoogleAPI.setKey(googleapikey);//set google-api-key
		try
		{
		   	String translatePost = Translate.DEFAULT.execute(rawtext, Language.AUTO_DETECT, Language.fromString(defaultlanguage));
		   	translatePost=translatePost.replaceAll("HEX23", "#");//reformat all #tags
		   	return translatePost;
		}
		catch (GoogleAPIException e)
		{
		    	//Log.i("GoogleAPIException", e.getMessage());
		    	return "Sorry can´t translation this.";
		}
	}
}