package com.voidcode.diasporawebclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SetupInternetActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupinternet);
    }
	public void onclick_button_close(View v)
	{
		Toast.makeText(getApplicationContext(), "CLOSE APPEN", Toast.LENGTH_LONG).show();
	    finish();
	}

}
