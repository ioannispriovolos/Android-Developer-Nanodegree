package com.ioannispriovolos.android.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

// http://www.vogella.com/tutorials/AndroidServices/article.html
public class WidgetService extends IntentService {

    private static final String TAG = WidgetService.class.getSimpleName();
    public static final String ACTION_UPDATE_INGREDIENTS = "com.ioannispriovolos.android.bakingapp.widget.action.update_ingredients";

    public WidgetService(){

        super("IngredientWidgetService");
    }

    public static void startActionUpdateIngredientsList(Context context){

        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENTS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null){
            final String action = intent.getAction();
            if (ACTION_UPDATE_INGREDIENTS.equals(action)){
                handleActionUpdateIngredients();
            }
        }
    }

    private void handleActionUpdateIngredients(){

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredient_widget_list);
        WidgetProvider.updateIngredientsWidget(this, appWidgetManager, DetailActivity.mRecipeTitle, appWidgetIds);
    }
}