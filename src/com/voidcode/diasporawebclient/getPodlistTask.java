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

import android.content.Context;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class getPodlistTask extends AsyncTask<Void,  Void, String[]> {
	protected Context context;
	private ProgressDialog dialog;
	
	public getPodlistTask(Context c)
	{
		context = c;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
        dialog.setMessage("Downloading podlist from poduptime.me");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
	}
	protected void onPostExecute(String[] pods) {	 
		dialog.dismiss();
	}
	@Override
	protected String[] doInBackground(Void... params) {
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
