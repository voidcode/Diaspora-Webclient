 //The JsReshareHandler class and this is a fix to bug 2: cannot reshare
//see: https://github.com/voidcode/Diaspora-Webclient/issues/2

//Idea by a user
//Has email me and say
//way can´t I reshare diaspora post to facebook, google+ and twitter?
//maby this class can help on this feature
//TODO
//find facebook, google+ and twitter share-post-java class
//add it here in a listview/AlertDialog

package com.voidcode.diasporawebclient;

import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class JsReshareHandler extends WebChromeClient {
	@Override
	public boolean onJsAlert(WebView view, String url, String message, JsResult result)
    {
		final AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
		final JsResult finalRes = result;
		alert
            .setMessage(message)
            .setPositiveButton(android.R.string.ok,
                    new AlertDialog.OnClickListener()
                    {
						public void onClick(DialogInterface dialog, int which) {
							finalRes.confirm();  
						}
                    })
            .setCancelable(false)
            .create()
            .show();
		
		//Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    	//sharingIntent.setType("text/plain");

    	//String shareBody = "Here is the share content body";

    	//sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
    	//sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
    	//sharingIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[]{"abc@def.com,pqr@xyz.com"});
    	//startActivity(Intent.createChooser(sharingIntent, "Share via"));
        return true;
    }
}