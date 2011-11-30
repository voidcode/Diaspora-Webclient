package com.voidcode.diasporawebclient;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class ShareActivity extends MainActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        	startDiasporaBrowser("/status_messages/new");
    }
}
