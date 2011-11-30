package com.voidcode.diasporawebclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SetupInternetActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupinternet);
    }
	public void onclick_settings_button_close(View v)
	{
		finish();
	}

}
