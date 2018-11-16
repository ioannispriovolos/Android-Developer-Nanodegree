package com.ioannispriovolos.android.bakingapp;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

public class WidgetRemote implements RemoteViewsService.RemoteViewsFactory{

    private static final String TAG = WidgetRemote.class.getSimpleName();
    Context mContext;
    private ArrayList<Ingredient> mIngredientModelArrayList = new ArrayList<>();

    public WidgetRemote(Context appContext){
        mContext = appContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        mIngredientModelArrayList = DetailActivity.mIngredientsArrayList;
    }

    @Override
    public int getCount() {

        if(mIngredientModelArrayList == null) return 0;
        return mIngredientModelArrayList.size();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (mIngredientModelArrayList == null) {

            return null;
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list);
        remoteViews.setTextViewText(R.id.widget_ingredient_title, mIngredientModelArrayList.get(position).getIngredient());
        remoteViews.setTextViewText(R.id.widget_quantity_text, mContext.getResources().getString(R.string.quantity_text));
        StringBuilder quantity_value = new StringBuilder("  ");
        quantity_value.append(mIngredientModelArrayList.get(position).getQuantity());
        quantity_value.append(" ");
        remoteViews.setTextViewText(R.id.widget_quantity_number, quantity_value.toString());
        remoteViews.setTextViewText(R.id.widget_ingredient_measured, mIngredientModelArrayList.get(position).getMeasure());
        return remoteViews;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public void onDestroy() {

    }
}
