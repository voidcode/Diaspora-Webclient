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
import android.util.Log;
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
	public String lvPods_arr[] = getPods();
	private EditText editTextCurrentpod;
	private String needchoosepod ="You need to choose a pod";
	JSONArray jsonArray;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        editTextCurrentpod = (EditText) findViewById(R.id.editText_currentpod);       
        lvPods = (ListView) findViewById(R.id.listView_poduptime);
        // lvPods.setTextFilterEnabled(true);
        // lvPods.setFilterText("");
        lvPods.setFastScrollEnabled(true);
        
        //Add: onkeyup search , A fast find search on editTextCurrentpod, So user don´t have to scholl the podlist to finde a pod 
        editTextCurrentpod.addTextChangedListener(new TextWatcher() 
        { 
        	int i=0;
        	String filter_podurl_arr[] = new String[lvPods_arr.length];
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) 
		    {
		    	editTextCurrentpod.requestFocus();
		    }
		    public void onTextChanged(CharSequence s, int start, int before, int count) 
		    {
		    	//TODO 
		    	//try search on 'diasp.org' then listview show to or more 'diasp.org'´s
			    //mabay this is some to do with the sixe of the 'filter_podurl_arr[]' base on 'lvPods_arr.length'
		    	//or the loop mabay is OfByOne 
		    	i=0;
		    	 for(String podurl:lvPods_arr)
		    	 {
			    	if(podurl.startsWith(s.toString()))
			    	{
			    		Log.i("compareTo ["+i+"]", "podurl="+podurl);
			    		filter_podurl_arr[i] = podurl;
			    		i++;
			    	}
		    	 }
		    }
			public void afterTextChanged(Editable s) 
			{
				if(filter_podurl_arr.length >0)
					fillListview(filter_podurl_arr); //add reslut to listview					
			}
		});
        
        //TODO
        //need to find a way to show the podlist on inti
        //this fill listview with pods
        //but user can´t click on pods in listview, end in error 
        //fillListview(this.lvPods_arr);
        
        // SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
        // Toast.makeText(getApplicationContext(), preferences.getString("currentpod", needchoosepod), Toast.LENGTH_SHORT).show();
        //Inti fill listview with pods
        
        // if(preferences.getString("currentpod", needchoosepod).equals(needchoosepod))
        //	fillListview(this.lvPods_arr);
    }
	//Screen orientation crashes app fix
	//http://jamesgiang.wordpress.com/2010/06/05/screen-orientation-crashes-my-app/
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	} 
	public void fillListview(String _lvPods_arr[])
	{
        
        lvPods.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,_lvPods_arr));
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
