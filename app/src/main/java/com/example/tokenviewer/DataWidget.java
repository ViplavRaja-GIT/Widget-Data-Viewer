package com.example.tokenviewer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.tokenviewer.services.WidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class DataWidget extends AppWidgetProvider {

    public static String REFRESH_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equalsIgnoreCase(REFRESH_ACTION)) {
            Log.e("Receive", intent.getAction());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, DataWidget.class));
            for (int appWidgetId : appWidgetIds) {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_config_list);
            }
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        Intent clickIntent = new Intent(context, DataWidget.class);
        clickIntent.setAction(REFRESH_ACTION);
        PendingIntent clickPendingIntent = getPenIntent(context);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.data_widget);
        views.setRemoteAdapter(R.id.widget_config_list, intent);
        views.setEmptyView(R.id.widget_config_list, R.id.appwidget_text);
        views.setOnClickPendingIntent(R.id.refresh_widget, getPenIntent(context));
        views.setPendingIntentTemplate(R.id.widget_config_list, clickPendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_config_list);
    }

    static private PendingIntent getPenIntent(Context context) {
        Intent intent = new Intent(context, DataWidget.class);
        intent.setAction(REFRESH_ACTION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
    }
}