package com.voidcode.diasporawebclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

public class SetupInternetActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupinternet);
    }
	public void onclick_button_wireless_settings(View v)
	{
	    startActivity( new Intent(Settings.ACTION_WIRELESS_SETTINGS));
	}
	public void onclick_button_close(View v)
	{
	    this.finish();
	}
	//Screen orientation crashes app fix
	//http://jamesgiang.wordpress.com/2010/06/05/screen-orientation-crashes-my-app/
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}
}
