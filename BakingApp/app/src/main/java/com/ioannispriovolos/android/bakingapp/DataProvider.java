package com.ioannispriovolos.android.bakingapp;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;

public class DataProvider  {

    private static final int DELAY_MILLIS = 10000;

    public interface DataLoadedCallback{
        void onDone();
    }

    public static void downloadBackingData(Context context, final DataLoadedCallback dataLoadedCallback, @Nullable final Idling idlingResource) {

        if (idlingResource != null) {
            idlingResource.setIdleState(false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dataLoadedCallback != null) {
                        dataLoadedCallback.onDone();
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                }
            }, DELAY_MILLIS);
        }
    }
}

