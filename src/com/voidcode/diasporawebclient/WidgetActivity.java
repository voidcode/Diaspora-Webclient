package com.voidcode.diasporawebclient;

import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import com.voidcode.diasporawebclient.R;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetActivity extends AppWidgetProvider {
	public static String MAIN_ACTION;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ShareActivity
            Intent share_intent = new Intent(context, ShareActivity.class);
            PendingIntent share_pendingIntent = PendingIntent.getActivity(context, 0, share_intent, 0);
            
            // Create an Intent to launch MainActivity
            Intent main_intent = new Intent(context, MainActivity.class);
            PendingIntent main_pendingIntent = PendingIntent.getActivity(context, 0, main_intent, 0);
            
            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widget_button_stream, main_pendingIntent);
            views.setOnClickPendingIntent(R.id.widget_button_share, share_pendingIntent);
            
            
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
