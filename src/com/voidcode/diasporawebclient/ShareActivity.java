package com.voidcode.diasporawebclient;

import android.os.Bundle;

public class ShareActivity extends MainActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        	mWeb.loadUrl(main_domain+"/status_messages/new");
    }
}
