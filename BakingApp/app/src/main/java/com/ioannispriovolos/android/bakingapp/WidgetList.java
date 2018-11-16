package com.ioannispriovolos.android.bakingapp;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetList extends RemoteViewsService {

    private static final String TAG = WidgetList.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new WidgetRemote(this.getApplicationContext()) ;
    }
}