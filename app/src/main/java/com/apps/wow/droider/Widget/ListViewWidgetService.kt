package com.apps.wow.droider.Widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.apps.wow.droider.Model.FeedModel
import com.apps.wow.droider.Model.Post
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Utils
import com.apps.wow.droider.api.DroiderApi
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by a.guzenko on 06.05.17.
 */

class ListViewWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return ListRemoteViewFactory(this.applicationContext, intent)
    }

    inner class ListRemoteViewFactory(internal var context: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory {

        private val TAG = "ListRemoteViewFactory"

        lateinit internal var data: ArrayList<Post>

        internal var widgetID: Int = 0

        lateinit internal var api: DroiderApi


        init {
            widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        override fun onCreate() {
            data = ArrayList<Post>()
            api = Utils.createRxService(DroiderApi::class.java, Utils.HOME_URL, true)
        }

        override fun onDataSetChanged() {
            api.getFeed(Utils.CATEGORY_MAIN, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT, 0)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<Response<FeedModel>>() {
                        override fun onCompleted() {

                        }

                        override fun onError(e: Throwable) {
                            Log.e(TAG, "onError: ", e)
                        }

                        override fun onNext(response: Response<FeedModel>) {
                            if (response.isSuccessful && !data.isEmpty()
                                    && data[0].titleValue != response.body()!!.posts[0].titleValue
                                    || data
                                    .isEmpty()) {

                                data.clear()
                                data.addAll(response.body()!!.posts)

                                FeedWidget.update(context)

                                for (i in 0..response.body()!!.posts.size - 1) {
                                    Log.d("TAG", "setSuccessfulView: " + response.body()!!.posts[i].titleValue!!)

                                }
                            }
                        }
                    })
        }

        override fun onDestroy() {}

        override fun getCount(): Int {
            return data.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            Log.d(TAG, "getViewAt: " + position)
            val rView = RemoteViews(context.packageName,
                    R.layout.widget_list_item)

            rView.setTextViewText(R.id.widget_title, data[position].titleValue)
            rView.setTextViewText(R.id.widget_description, data[position].descriptionValue)

            val clickIntent = Intent()
            clickIntent.putExtra(FeedWidget.ITEM_POSITION, position)
            clickIntent.putExtra(Utils.EXTRA_ARTICLE_TITLE, data[position].titleValue)
            clickIntent.putExtra(Utils.EXTRA_ARTICLE_URL, data[position].url)
            clickIntent.putExtra(Utils.EXTRA_ARTICLE_IMG_URL, data[position].pictureWide)
            clickIntent.putExtra(Utils.EXTRA_SHORT_DESCRIPTION, data[position].descriptionValue)
            rView.setOnClickFillInIntent(R.id.widget_card_container, clickIntent)
            return rView
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }
    }
}
