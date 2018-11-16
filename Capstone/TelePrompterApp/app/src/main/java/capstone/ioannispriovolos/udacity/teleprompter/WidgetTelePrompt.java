package capstone.ioannispriovolos.udacity.teleprompter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

// https://developer.android.com/guide/topics/appwidgets/
public class WidgetTelePrompt extends AppWidgetProvider {

    private static String TEXTS;
    private static String NO_TEXTS;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String[] script) {

        TEXTS = context.getString(R.string.scripts_tag_main_to_detail);
        NO_TEXTS = context.getString(R.string.no_script_available);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tele_widget);
        if(script == null) {
            views.setTextViewText(R.id.appwidget_text, NO_TEXTS);
            return;
        }
        String widgetTitle = script[0].substring(1);
        String widgetText = script[1];

        views.setTextViewText(R.id.appwidget_title, widgetTitle);
        views.setTextViewText(R.id.appwidget_text,widgetText);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,0);
        views.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        WidgetService.setActionUpdateWidget(context);
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,String[] script) {

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, script);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {

    }
}
