package com.voidcode.diasporawebclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class PodSettingsActivity extends Activity {
	public static final String SETTINGS_FILENAME="settings";
	public ListView lvPods;
	public String lvPods_arr[];
	private EditText editTextCurrentpod;
	JSONArray jsonArray;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        if(isNetworkAvailable())
        { 
				try {
					//load pods from poduptime.me as a AsyncTask
					
					lvPods_arr = new getPodlistTask(this).execute().get();
					
					Log.i("lvPods_arr", String.valueOf(lvPods_arr.length) );
					
					super.onCreate(savedInstanceState);
		        	setContentView(R.layout.podsettings);
					
					//load elements from layout file podsettings.xml
					editTextCurrentpod = (EditText) findViewById(R.id.editText_currentpod);       
			        lvPods = (ListView) findViewById(R.id.listView_poduptime);
			              
			        //show the currentpod to user
			        SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, MODE_PRIVATE);
			        editTextCurrentpod.setText(preferences.getString("currentpod", "You need to choose a pod"));
			        editTextCurrentpod.selectAll();
			        
			        //check if user have be get the dialog info box
			        if(preferences.getBoolean("has_show_dialog", false) == false)
			        {
				        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
						alert.setTitle(R.string.podsettings_dialog_title);
						alert.setMessage(R.string.podsettings_dialog_text);
						alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						});
						alert.show();
						//this ensure the user only see the dialogbox ones
						SharedPreferences.Editor editor = preferences.edit();
						editor.putBoolean("has_show_dialog", true);
						editor.commit();
			        }
			        
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
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
        }
        else //user have NO internet
        {
        	this.finish();
    		startActivity(new Intent(this, SetupInternetActivity.class));
        }
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
	
	private boolean isNetworkAvailable()
	{
		ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileInfo = connec.getNetworkInfo(0);
		NetworkInfo wifiInfo = connec.getNetworkInfo(1);
		NetworkInfo wimaxInfo = connec.getNetworkInfo(6);
		if (wimaxInfo!=null) {
			return mobileInfo.isConnected() || wifiInfo.isConnected()|| wimaxInfo.isConnected();
		}
		else {
			return mobileInfo.isConnected() || wifiInfo.isConnected();
		}
	}
}