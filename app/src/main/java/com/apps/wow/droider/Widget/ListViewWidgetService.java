package com.apps.wow.droider.Widget;

import com.apps.wow.droider.Model.FeedModel;
import com.apps.wow.droider.Model.Post;
import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.api.DroiderApi;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.apps.wow.droider.Utils.Utils.EXTRA_ARTICLE_IMG_URL;
import static com.apps.wow.droider.Utils.Utils.EXTRA_ARTICLE_TITLE;
import static com.apps.wow.droider.Utils.Utils.EXTRA_ARTICLE_URL;
import static com.apps.wow.droider.Utils.Utils.EXTRA_SHORT_DESCRIPTION;

/**
 * Created by a.guzenko on 06.05.17.
 */

public class ListViewWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory(this.getApplicationContext(), intent);
    }

    public class ListRemoteViewFactory implements RemoteViewsFactory {

        private final String TAG = "ListRemoteViewFactory";

        ArrayList<Post> data;

        Context context;

        int widgetID;

        DroiderApi api;


        public ListRemoteViewFactory(Context ctx, Intent intent) {
            context = ctx;
            widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            data = new ArrayList<>();
            api = Utils.createRxService(DroiderApi.class, Utils.HOME_URL, true);
        }

        @Override
        public void onDataSetChanged() {
            api.getFeed(Utils.CATEGORY_MAIN, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT, 0)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Response<FeedModel>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: ", e);
                        }

                        @Override
                        public void onNext(Response<FeedModel> response) {
                            if ((response.isSuccessful() && !data.isEmpty()
                                    && response.body().getPosts() != null && !data.get(0).getTitle()
                                    .equals(response.body().getPosts().get(0).getTitle())) || data
                                    .isEmpty()) {

                                data.clear();
                                data.addAll(response.body().getPosts());

                                FeedWidget.update(context);

                                for (int i = 0; i < response.body().getPosts().size(); i++) {
                                    Log.d("TAG", "setSuccessfulView: " + response.body().getPosts()
                                            .get(i).getTitle());

                                }
                            }
                        }
                    });
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.d(TAG, "getViewAt: " + position);
            RemoteViews rView = new RemoteViews(context.getPackageName(),
                    R.layout.widget_list_item);

            rView.setTextViewText(R.id.widget_title, data.get(position).getTitle());
            rView.setTextViewText(R.id.widget_description, data.get(position).getDescription());

            Intent clickIntent = new Intent();
            clickIntent.putExtra(FeedWidget.ITEM_POSITION, position);
            clickIntent.putExtra(EXTRA_ARTICLE_TITLE, data.get(position).getTitle());
            clickIntent.putExtra(EXTRA_ARTICLE_URL, data.get(position).getUrl());
            clickIntent.putExtra(EXTRA_ARTICLE_IMG_URL, data.get(position).getPictureWide());
            clickIntent.putExtra(EXTRA_SHORT_DESCRIPTION, data.get(position).getDescription());
            rView.setOnClickFillInIntent(R.id.widget_card_container, clickIntent);
            return rView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
