package com.voidcode.diasporawebclient;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class TranslateActivity extends Activity {
	public static final String TRANSLATE_FILENAME="translate_settings";
	private String googleapikey="";
	private EditText editTextGoogleApiKey;
	private Spinner spinnerLanguage;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate);
        
        // load google-translate-api-key
    	SharedPreferences preferences = getSharedPreferences(TRANSLATE_FILENAME, MODE_PRIVATE);
    	this.googleapikey = preferences.getString("googleapikey", "");
    	
    	editTextGoogleApiKey = (EditText) findViewById(R.id.editText_googleapikey);
    	
    	fillLanguageSpinner();
    	//if has set the google-api-key
    	//then view key in edittext
    	if(!this.googleapikey.equals(""))
    	   editTextGoogleApiKey.setText(this.googleapikey);
    }
	public void fillLanguageSpinner()
	{
		spinnerLanguage = (Spinner) findViewById(R.id.translate_spinner_language);
		spinnerLanguage.setAdapter(new ArrayAdapter<Language>(this, android.R.layout.simple_spinner_item, Language.values()));
		spinnerLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				// Set default-languages.
				SharedPreferences preferences = getSharedPreferences(TRANSLATE_FILENAME, MODE_PRIVATE);		
			    SharedPreferences.Editor editor = preferences.edit();
				editor.putString("defaultlanguage", Language.fromString(parent.getItemAtPosition(pos).toString()).shortCode());
			    editor.commit();
			}
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	public void onlick_save_key(View v) throws IOException
	{
		String new_googlekey = editTextGoogleApiKey.getText().toString().trim();	
		SharedPreferences preferences = getSharedPreferences(TRANSLATE_FILENAME, MODE_PRIVATE);		
		// Save the new google-api-key
	    SharedPreferences.Editor editor = preferences.edit();
		editor.putString("googleapikey", new_googlekey);
		Log.i("TranslateActivity", "onlick_save_key. Key="+new_googlekey);
	    editor.commit();
	    this.finish();
	    startActivity(new Intent(this, MainActivity.class));
	}
	public void onlick_howto_obtain_googletranslatekey(View v)
	{
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=-KHq094SeWU")));
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
