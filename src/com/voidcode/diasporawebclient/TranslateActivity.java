package com.voidcode.diasporawebclient;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TranslateActivity extends Activity {
	public static final String TRANSLATE_FILENAME="translate_settings";
	private String googleapikey="";
	private EditText editTextGoogleApiKey;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate);
        
        // load google-api-key 
    	SharedPreferences preferences = getSharedPreferences(TRANSLATE_FILENAME, MODE_PRIVATE);
    	this.googleapikey = preferences.getString("googleapikey", "");
    	
    	editTextGoogleApiKey = (EditText) findViewById(R.id.editText_translate);
    	
    	//if has set the google-api-key
    	//then view key in edittext
    	if(!this.googleapikey.equals(""))
    	   editTextGoogleApiKey.setText(this.googleapikey);
    }
	public void onlick_save_key(View v) throws IOException
	{
		String new_googlekey = editTextGoogleApiKey.getText().toString();	
		//if(!new_googlekey.equals(""))	
		//{
			SharedPreferences preferences = getSharedPreferences(TRANSLATE_FILENAME, MODE_PRIVATE);		
			// Save the new google-api-key
	        SharedPreferences.Editor editor = preferences.edit();
			editor.putString("googleapikey", new_googlekey);
	        editor.commit();
	   
	        this.finish();
	        startActivity(new Intent(this, MainActivity.class));
		//}
		//else
			//Toast.makeText(getApplicationContext(), "You need add a google-api-key", Toast.LENGTH_SHORT).show();
	}
	public void onlick_goto_googleapiconsole(View v)
	{
	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://code.google.com/apis/console")));
	}
	// Handle the Back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg){
        if((keyCode == KeyEvent.KEYCODE_BACK))
        {
            this.finish();
            startActivity(new Intent(this, MainActivity.class));
        	return false;
        }
        else
            return true;
    }
	//Screen orientation crashes app fix
	//http://jamesgiang.wordpress.com/2010/06/05/screen-orientation-crashes-my-app/
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	} 
}
