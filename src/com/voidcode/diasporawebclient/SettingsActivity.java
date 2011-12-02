package com.voidcode.diasporawebclient;


import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	//public EditText editTextCurrentpod;
	public ListView lvPods;
	public static final String SETTINGS_FILENAME="settings";
	public static final String defaultPod = "https://diasp.eu"; // This is the default-pod
	// TODO
	// Make a JsonPaser to read all pods on podupti.me
	// Then remove all pod there is not 'https://' urls from this list
	// The jsondata the paser need to read from: http://podupti.me/api.php?key=4r45tg&format=json
	// Add then all the pods url´s to the lvPods_arr[] array
	private String lvPods_arr[] = {"https://joindiaspora.com", "https://diasp.eu", "https://diasp.org", "https://wk3.org","https://londondiaspora.org", "https://nerdpol.ch" ,"https://wk3.org" ,"https://diaspora.eigenlab.org" ,"https://ser.endipito.us" ,"https://my-seed.com" ,"https://foobar.cx" ,"https://diasp.eu.com" ,"https://diasp.de" ,"https://ottospora.nl" ,"https://stylr.net" ,"https://pod.matstace.me.uk" ,"https://loofi.de" ,"https://social.mathaba.net","https://ilikefreedom.org" ,"https://diaspora.podzimek.org" ,"https://group.lt","https://jauspora.com" ,"https://diaspora.f4n.de" ,"https://free-beer.ch","https://diasp0ra.ca" ,"https://diaspora.subsignal.org" ,"https://pod.geraspora.de","https://diaspora.sjau.ch" ,"https://poddery.com" ,"https://diaspor.at","https://diaspora.filundschmer.at" ,"https://spora.com.ua" ,"https://hasst-euch-alle.de","https://diasp.urbanabydos.ca" ,"https://dipod.org" ,"https://Nesc.io","https://dipod.es" ,"https://dipod.es" ,"https://pod.nocentre.net","https://mispora.net" ,"https://privit.us" ,"https://failure.net"};
	private EditText editTextCurrentpod;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
        editTextCurrentpod = (EditText) findViewById(R.id.editText_currentpod);        
        editTextCurrentpod.setText(preferences.getString("currentpod", defaultPod));
        
        //Fill listview with pods
        fillListview();
    }
	@Override
	public void onPause()
	{
		super.onPause();
		this.finish();
	}
	public void fillListview()
	{
        lvPods = (ListView) findViewById(R.id.listView_poduptime);
        lvPods.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,lvPods_arr));
        lvPods.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				//onclick put select pod to editTextCurrentpod 	
			    editTextCurrentpod.setText(lvPods.getItemAtPosition(position).toString());
			}
        });
	}
	public void Onclick_SaveSettings(View v) throws IOException
	{
		//get userinput
		String new_currentpod = editTextCurrentpod.getText().toString();
		
		SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
		
		// Save the new currentpod
        SharedPreferences.Editor editor = preferences.edit();
		editor.putString("currentpod", new_currentpod);
        editor.commit();
        this.finish();
        // to reload webview with the new pod
        startActivity(new Intent(this, MainActivity.class));
        
        Toast.makeText(getApplicationContext(), "Pod: "+new_currentpod, Toast.LENGTH_LONG).show();
	}
}
