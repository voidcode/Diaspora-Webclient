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
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
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
	private String lvPods_arr[] = getPods();
	private EditText editTextCurrentpod;
	JSONArray jsonArray;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
        editTextCurrentpod = (EditText) findViewById(R.id.editText_currentpod);        
        editTextCurrentpod.setText(preferences.getString("currentpod", "You need to choose a pod"));       
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
				//Log.e("Diaspora-WebClient", "Failed to download file");
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
			//Log.i("Diaspora-WebClient","Number of entries " + jr.length());
			list=new ArrayList<String>();
			for (int i = 0; i < jr.length(); i++) {
				JSONObject jo = jr.getJSONObject(i);
				//Log.i("Diaspora-WebClient", jo.getString("domain"));
				String secure=jo.getString("secure");
				if(secure.equals("true"))
					list.add(jo.getString("domain"));				
				}

		}catch (Exception e) {
			//TODO Handle Parsing errors here	
			e.printStackTrace();
		}	
		return list.toArray(new String[list.size()]);
	}
}
