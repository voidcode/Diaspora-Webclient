package com.voidcode.diasporawebclient;

import java.net.URLDecoder;

import android.util.Log;

public class JSInterface {
	private String translatePost;
	public JSInterface()
	{
	}
	public String Translate(String main_domain, String googleapikey, String defaultlanguage, String text) throws Exception
	{ 
		String decodetext = URLDecoder.decode(text);
		String rawtext = decodetext.replaceAll("\\<.*?\\>", "");//remove all htmltags
		//TODO: find out if google is tacking money for 'none' translate word(HEX23...the..word),
		//if, then make it so all tag is remove form the 'rawtext' and then add it back to the 'translatePost'(after: Translate.DEFAULT.execute)
		rawtext = rawtext.replaceAll("#", "HEX23");//format all #tags so google-translate don´t translate the #tag
		rawtext = rawtext.trim();//remove end-spaces 	
		// Set the HTTP referrer to your website address.
			
		try
		{
			if(googleapikey.toLowerCase().equals("microsoft-translator"))//use MS-api
			{
				com.memetix.mst.MicrosoftTranslatorAPI.setHttpReferrer(main_domain);
				com.memetix.mst.MicrosoftTranslatorAPI.setKey("4DD273288D3B3C215B1A50BABC39C00F18155C2D");//set ms-api-key
				translatePost="";
				translatePost = com.memetix.mst.translate.Translate.execute(rawtext, com.memetix.mst.language.Language.AUTO_DETECT, com.memetix.mst.language.Language.fromString(defaultlanguage));
				translatePost=translatePost.replaceAll("HEX23", "#");//reformat all #tags
			   	return translatePost+"\n\nTranslate by Microsoft.";		
			}
			else //use Google-api
			{
				// See: http://code.google.com/apis/language/translate/v2/getting_started.html
				com.google.api.GoogleAPI.setHttpReferrer(main_domain);// Set the Google Translate API key
				com.google.api.GoogleAPI.setKey(googleapikey);//set google-api-key
				translatePost="";
				translatePost = com.google.api.translate.Translate.DEFAULT.execute(rawtext, com.google.api.translate.Language.AUTO_DETECT, com.google.api.translate.Language.fromString(defaultlanguage));
				translatePost=translatePost.replaceAll("HEX23", "#");//reformat all #tags
			   	return translatePost+"\n\nTranslate by Google.";
			}
		}
		catch (Exception e)
		{
		    	return "ERROR: Sorry can´t translation this. Maybe Google-translator can do this!";
		}
	}
}