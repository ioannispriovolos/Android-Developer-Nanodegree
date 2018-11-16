package capstone.ioannispriovolos.udacity.teleprompter;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import timber.log.Timber;

// https://www.journaldev.com/20735/android-intentservice-broadcastreceiver
public class WidgetService extends IntentService {

    ArrayList<Text> Texts;
    Text mTexts;

    public static String ACTION_UPDATE_WIDGET ;
    public WidgetService() {
        super("WidgetService");
        Timber.plant(new Timber.DebugTree());
    }

    public static void setActionUpdateWidget(Context context){

        ACTION_UPDATE_WIDGET = context.getString(R.string.action_update_widget);
        Intent intent = new Intent(context,WidgetService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent != null) {
            String action = intent.getAction();
            if(action.equals(ACTION_UPDATE_WIDGET)){
                handleActionUpdateWidget();
            }
        }
        else {
            handleActionUpdateWidget();
        }
    }

    private void handleActionUpdateWidget() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetTelePrompt.class));
        new fetch().execute();
        if(Texts != null && Texts.size() > 0) {
            mTexts = Texts.get(0);
        }
        else {
            WidgetTelePrompt.updateWidget(this, appWidgetManager, appWidgetIds,null);
            return;
        }

        String[] script = new String[3];
        script[0] = mTexts.title;
        script[1] = mTexts.desc;
        script[2] = String.valueOf(mTexts.id);
        WidgetTelePrompt.updateWidget(this, appWidgetManager, appWidgetIds,script);
    }

    private void extractData(Cursor cursor) {

        Texts = new ArrayList<>();
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                String title = cursor.getString(cursor.getColumnIndex(TextContract.TextData.TITLE));
                Long id = cursor.getLong(cursor.getColumnIndex(TextContract.TextData._ID));
                String desc = cursor.getString(cursor.getColumnIndex(TextContract.TextData.DESCRIPTION));
                Texts.add(new Text(title, desc,id));
                Timber.d(title);
            }
            while(cursor.moveToNext());
        }
    }

    public class fetch extends AsyncTask<Void,Void,Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return getContentResolver().query(TextContract.TextData.CONTENT_URI,null,null,null,null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            extractData(cursor);
        }
    }
}