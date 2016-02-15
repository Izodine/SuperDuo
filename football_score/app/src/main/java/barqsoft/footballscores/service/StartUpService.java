package barqsoft.footballscores.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.widget.FootballWidgetProvider;

/**
 * Created by root on 11/10/15.
 */
public class StartUpService extends IntentService {

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    public StartUpService(){ super("StartUpService"); }


    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        Cursor cursor = getApplicationContext().getContentResolver().query(
                DatabaseContract.BASE_CONTENT_URI,
                null,
                null,
                null,
                null);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FootballWidgetProvider.class));
        for(int id: appWidgetIds){

            RemoteViews views = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                int newSize = appWidgetManager.getAppWidgetOptions(id).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

                if(newSize < 110){
                    views = new RemoteViews(this.getPackageName(), R.layout.widget_layout_small);
                }
                else if(newSize >= 110 && newSize < 220){
                    views = new RemoteViews(this.getPackageName(), R.layout.widget_layout_medium);
                }else if(newSize >= 220){
                    views = new RemoteViews(this.getPackageName(), R.layout.widget_layout_large);
                }
            }
            StringBuffer outString = new StringBuffer();
            if(cursor == null | (cursor != null ? cursor.getCount() : 0) < 1){
                outString.append(getString(R.string.no_data_found_text));
            }
            else {
                cursor.moveToFirst();

                outString.append(cursor.getString(COL_HOME))
                        .append(" vs ")
                        .append(cursor.getString(COL_AWAY))
                        .append("\n")
                        .append(cursor.getString(COL_MATCHTIME))
                        .append(Utilies.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
            }
            cursor.close();
            views.setTextViewText(R.id.widgetTextView, outString);
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(id, views);

        }

    }
}
