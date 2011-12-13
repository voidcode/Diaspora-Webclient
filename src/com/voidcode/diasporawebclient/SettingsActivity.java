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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	public static final String SETTINGS_FILENAME="settings";
	public ListView lvPods;
	public String lvPods_arr[] = getPods();
	private EditText editTextCurrentpod;
	JSONArray jsonArray;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        editTextCurrentpod = (EditText) findViewById(R.id.editText_currentpod);       
        lvPods = (ListView) findViewById(R.id.listView_poduptime);
              
        //show the currentpod to user
        SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
        editTextCurrentpod.setText(preferences.getString("currentpod", "You need to choose a pod"));
        editTextCurrentpod.selectAll();
        
        //fill listview with pods form http://podupti.me
        fillListview(this.lvPods_arr);
        
        //podsearch, A fast find search on editTextCurrentpod, So user don´t have to scholl the podlist to finde a pod 
        editTextCurrentpod.addTextChangedListener(new TextWatcher() 
        { 
        	List<String> filter_podurl_list = null;
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) 
		    {
		    }
		    public void onTextChanged(CharSequence s, int start, int before, int count) 
		    {
		    	filter_podurl_list=new ArrayList<String>();
		    	for(String podurl:lvPods_arr)
		    	{
			    	if(podurl.startsWith(s.toString()))
			    	{
			    		filter_podurl_list.add(podurl);
			    	}
		    	 }
		    }
			public void afterTextChanged(Editable s) 
			{
				///add reslut to listview
				if(!filter_podurl_list.equals(null))
					fillListview(filter_podurl_list.toArray(new String[filter_podurl_list.size()]));
			}
		});
    }
	public void fillListview(String _lvPods_arr[])
	{
        lvPods.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _lvPods_arr));
        lvPods.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				//onclick put select pod to editTextCurrentpod 	
			    editTextCurrentpod.setText(lvPods.getItemAtPosition(position).toString());
			}
        });
	}
	//Screen orientation crashes app fix
	//http://jamesgiang.wordpress.com/2010/06/05/screen-orientation-crashes-my-app/
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
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
	public void Onclick_SaveSettings(View v) throws IOException
	{
		//get userinput
		String new_currentpod = editTextCurrentpod.getText().toString();
		//if user has added a pod
		if(!new_currentpod.equals(""))
		{
			// Save the new currentpod
			SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("currentpod", new_currentpod);
			editor.commit();
			//goto MainActivity
			this.finish();
			startActivity(new Intent(this, MainActivity.class));
		}
		else
			Toast.makeText(getApplicationContext(), "You need to choose a pod", Toast.LENGTH_SHORT).show();
	}
	//simple json parsing, to retrieve list of pods from podupti.me
	//https://github.com/voidcode/Diaspora-Webclient/pull/4
	//by: https://github.com/vipulnsward
	public String [] getPods()  
	{
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