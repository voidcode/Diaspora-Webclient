package com.voidcode.diasporawebclient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
<<<<<<< HEAD
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
=======
import android.os.Bundle;
import android.util.Log;
>>>>>>> c7c776c4bac93c9538f07b0da9feccf776509ab6
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
	private String lvPods_arr[] = getPods();// Previously => {"https://joindiaspora.com", "https://diasp.eu", "https://diasp.org", "https://wk3.org","https://londondiaspora.org", "https://nerdpol.ch" ,"https://wk3.org" ,"https://diaspora.eigenlab.org" ,"https://ser.endipito.us" ,"https://my-seed.com" ,"https://foobar.cx" ,"https://diasp.eu.com" ,"https://diasp.de" ,"https://ottospora.nl" ,"https://stylr.net" ,"https://pod.matstace.me.uk" ,"https://loofi.de" ,"https://social.mathaba.net","https://ilikefreedom.org" ,"https://diaspora.podzimek.org" ,"https://group.lt","https://jauspora.com" ,"https://diaspora.f4n.de" ,"https://free-beer.ch","https://diasp0ra.ca" ,"https://diaspora.subsignal.org" ,"https://pod.geraspora.de","https://diaspora.sjau.ch" ,"https://poddery.com" ,"https://diaspor.at","https://diaspora.filundschmer.at" ,"https://spora.com.ua" ,"https://hasst-euch-alle.de","https://diasp.urbanabydos.ca" ,"https://dipod.org" ,"https://Nesc.io","https://dipod.es" ,"https://dipod.es" ,"https://pod.nocentre.net","https://mispora.net" ,"https://privit.us" ,"https://failure.net"};
	private EditText editTextCurrentpod;
	JSONArray jsonArray;
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
	//Screen orientation crashes app fix
	//http://jamesgiang.wordpress.com/2010/06/05/screen-orientation-crashes-my-app/
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
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
	// Handle the Back button in WebView, to back in history.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg){
        if((keyCode == KeyEvent.KEYCODE_BACK))
        {
            this.finish();
        	startActivity(new Intent(this, MainActivity.class));
        	return false;
        }
        else
        {
            return true;
        }
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
        startActivity(new Intent(this, MainActivity.class));
        Toast.makeText(getApplicationContext(), "Pod: "+new_currentpod, Toast.LENGTH_LONG).show();
	}
	
	public String [] getPods() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		List<String> list = null;
		try {
			HttpGet httpGet = new HttpGet("http://podupti.me/api.php?key=4r45tg&format=json");
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				//TODO  Notify User about failure
				Log.e("Diaspora-WebClient", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			//TODO handle network unreachable exception here
			e.printStackTrace();
		} catch (IOException e) {
			//TODO handle json buggy feed
			e.printStackTrace();
		}
		
		//Parse the JSON Data
		try {
			JSONObject j=new JSONObject(builder.toString());			
			JSONArray jr=j.getJSONArray("pods");
			Log.i("Diaspora-WebClient","Number of entries " + jr.length());
			list=new ArrayList<String>();
			for (int i = 0; i < jr.length(); i++) {
				JSONObject jo = jr.getJSONObject(i);
				Log.i("Diaspora-WebClient", jo.getString("domain"));
				String secure=jo.getString("secure");
				if(secure.equals("true"))
				list.add("https://"+jo.getString("domain"));				
				}

		}catch (Exception e) {
			//TODO Handle Parsing errors here	
			e.printStackTrace();
		}	
		return list.toArray(new String[list.size()]);
	}
}
