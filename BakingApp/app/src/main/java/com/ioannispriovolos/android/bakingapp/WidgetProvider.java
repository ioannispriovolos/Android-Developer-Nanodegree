package com.ioannispriovolos.android.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    // https://developer.android.com/guide/topics/appwidgets/
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String cakeName, int appWidgetId){

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
        StringBuilder title = new StringBuilder(cakeName);
        title.append(" - Ingredients");
        remoteViews.setTextViewText(R.id.widget_title_tv, title.toString());
        Intent intent = new Intent(context, WidgetList.class);
        remoteViews.setRemoteAdapter(R.id.ingredient_widget_list, intent);
        remoteViews.setEmptyView(R.id.ingredient_widget_list, R.id.empty_view);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    public static void updateIngredientsWidget(Context context, AppWidgetManager appWidgetManager, String cakeName, int[] appWidgetIds){

        for (int appWidgetId : appWidgetIds){
            updateAppWidget(context, appWidgetManager,cakeName, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}