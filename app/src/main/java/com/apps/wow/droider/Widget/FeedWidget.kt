package com.apps.wow.droider.Widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.apps.wow.droider.Article.ArticleActivity
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Utils
import java.util.*

/**
 * Created by a.guzenko on 06.05.17.
 */

class FeedWidget : AppWidgetProvider() {

    internal val ACTION_ON_CLICK = "com.apps.wow.droider.Widget.itemonclick"

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d(LOG_TAG, "onEnabled")
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (appWidgetId in appWidgetIds) {
            val rv = RemoteViews(context.packageName, R.layout.widget)
            setList(rv, context, appWidgetId)
            setListClick(rv, context)
            appWidgetManager.updateAppWidget(appWidgetId, rv)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.last_news)
            Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds))
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds))
        update(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d(LOG_TAG, "onDisabled")
    }

    internal fun setList(rv: RemoteViews, context: Context, appWidgetId: Int) {
        val adapter = Intent(context, ListViewWidgetService::class.java)
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        rv.setRemoteAdapter(R.id.last_news, adapter)
    }

    internal fun setListClick(rv: RemoteViews, context: Context) {
        val listClickIntent = Intent(context, FeedWidget::class.java)
        listClickIntent.action = ACTION_ON_CLICK
        val listClickPIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, 0)
        rv.setPendingIntentTemplate(R.id.last_news, listClickPIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action.equals(ACTION_ON_CLICK, ignoreCase = true)) {
            val itemPos = intent.getIntExtra(ITEM_POSITION, -1)
            if (itemPos != -1) {
                try {
                    val articleIntent = Intent(context, ArticleActivity::class.java)
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_TITLE,
                            intent.getStringExtra(Utils.EXTRA_ARTICLE_TITLE))
                    articleIntent
                            .putExtra(Utils.EXTRA_ARTICLE_URL, intent.getStringExtra(Utils.EXTRA_ARTICLE_URL))
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_IMG_URL,
                            intent.getStringExtra(Utils.EXTRA_ARTICLE_IMG_URL))
                    articleIntent.putExtra(Utils.EXTRA_SHORT_DESCRIPTION, intent.getStringExtra(Utils.EXTRA_SHORT_DESCRIPTION))
                    articleIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(articleIntent)

                } catch (npe: NullPointerException) {
                    // Ошибка происходит если пытаться отправить пикчу
                    // в статью. Сначала он выкидывал NullPointerException
                    // на article в ArticleActivity. Я закомментил
                    // после этого ничего не открывалось
                    Toast.makeText(context, "Произошла ошибка при открытии статьи!",
                            Toast.LENGTH_LONG).show()
                    Log.e(TAG, "onClick: Failed to open ArticleActivity!", npe.cause)
                    npe.printStackTrace()
                }

            }
        }
    }

    companion object {

        internal val LOG_TAG = "myLogs"

        internal val ITEM_POSITION = "item_position"

        fun update(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            if (appWidgetManager != null) {
                Log.d(LOG_TAG, "update: ")
                val name = ComponentName(context, FeedWidget::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(name)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.last_news)
            }
        }
    }
}
