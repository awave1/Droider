package com.apps.wow.droider.Widget;

import com.apps.wow.droider.Article.ArticleActivity;
import com.apps.wow.droider.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Arrays;

import static android.content.ContentValues.TAG;
import static com.apps.wow.droider.Utils.Utils.EXTRA_ARTICLE_IMG_URL;
import static com.apps.wow.droider.Utils.Utils.EXTRA_ARTICLE_TITLE;
import static com.apps.wow.droider.Utils.Utils.EXTRA_ARTICLE_URL;
import static com.apps.wow.droider.Utils.Utils.EXTRA_SHORT_DESCRIPTION;

/**
 * Created by a.guzenko on 06.05.17.
 */

public class FeedWidget extends AppWidgetProvider {

    static final String LOG_TAG = "myLogs";

    final String ACTION_ON_CLICK = "com.apps.wow.droider.Widget.itemonclick";

    final static String ITEM_POSITION = "item_position";

    private PendingIntent newPending;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(LOG_TAG, "onEnabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
            setList(rv, context, appWidgetId);
            setListClick(rv, context);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.last_news);
            Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds));
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
        update(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(LOG_TAG, "onDisabled");
    }

    public static void update(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (appWidgetManager != null) {
            Log.d(LOG_TAG, "update: ");
            ComponentName name = new ComponentName(context, FeedWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.last_news);
        }
    }

    void setList(RemoteViews rv, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, ListViewWidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        rv.setRemoteAdapter(R.id.last_news, adapter);
    }

    void setListClick(RemoteViews rv, Context context) {
        Intent listClickIntent = new Intent(context, FeedWidget.class);
        listClickIntent.setAction(ACTION_ON_CLICK);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, 0);
        rv.setPendingIntentTemplate(R.id.last_news, listClickPIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equalsIgnoreCase(ACTION_ON_CLICK)) {
            int itemPos = intent.getIntExtra(ITEM_POSITION, -1);
            if (itemPos != -1) {
                try {
                    Intent articleIntent = new Intent(context, ArticleActivity.class);
                    articleIntent.putExtra(EXTRA_ARTICLE_TITLE,
                            intent.getStringExtra(EXTRA_ARTICLE_TITLE));
                    articleIntent
                            .putExtra(EXTRA_ARTICLE_URL, intent.getStringExtra(EXTRA_ARTICLE_URL));
                    articleIntent.putExtra(EXTRA_ARTICLE_IMG_URL,
                            intent.getStringExtra(EXTRA_ARTICLE_IMG_URL));
                    articleIntent.putExtra(EXTRA_SHORT_DESCRIPTION, intent.getStringExtra(EXTRA_SHORT_DESCRIPTION));
                    articleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(articleIntent);

                } catch (NullPointerException npe) {
                    // Ошибка происходит если пытаться отправить пикчу
                    // в статью. Сначала он выкидывал NullPointerException
                    // на article в ArticleActivity. Я закомментил
                    // после этого ничего не открывалось
                    Toast.makeText(context, "Произошла ошибка при открытии статьи!",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onClick: Failed to open ArticleActivity!", npe.getCause());
                    npe.printStackTrace();
                }
            }
        }
    }
}
