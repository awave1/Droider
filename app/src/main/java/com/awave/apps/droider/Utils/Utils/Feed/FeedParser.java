package com.awave.apps.droider.Utils.Utils.Feed;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.awave.apps.droider.Elements.MainScreen.Feed;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Utils.FeedItem;
import com.awave.apps.droider.Utils.Utils.Helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by awave on 2016-01-23.
 */
public class FeedParser extends AsyncTask<String, Void, Void> {
    private String TAG = "FeedParser";
    private ArrayList<FeedItem> items = new ArrayList<>();
    private OnTaskCompleted onTaskCompleted;
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> link = new ArrayList<>();
    private ArrayList<String> description = new ArrayList<>();
    private ArrayList<String> img = new ArrayList<>();
    private ArrayList<String> youTubeLink = new ArrayList<>();
    private int count = 0;
    private Handler refreshHandler = new Handler();
    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public FeedParser(OnTaskCompleted onTaskCompleted, ArrayList<FeedItem> data, Context context, SwipeRefreshLayout refreshLayout) {
        this.onTaskCompleted = onTaskCompleted;
        this.items = data;
        this.context = context;
        this.mSwipeRefreshLayout = refreshLayout;
    }

    @Override
    protected Void doInBackground(final String... params) {

        final Document[] document = new Document[1];

        synchronized (this) {
            try {
                try {
                    document[0] = Jsoup.connect(params[0]).get();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();

                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                document[0] = Jsoup.connect(params[0]).get();
                            } catch (IOException  e1) {
                                e1.printStackTrace();
                            }
                        }
                    }.run();

                }
                Elements elements = document[0].select("div[id^=post]");
                Elements entry = document[0].select("div.entry");

                for (Element element : elements) {
                    title.add(element.getElementsByTag("a").attr("title"));
                    link.add(element.getElementsByTag("a").attr("href"));
                    description.add(element.getElementsByTag("p").text().substring(0, element.getElementsByTag("p").text().lastIndexOf(" ")));
                    img.add(element.getElementsByTag("img").attr("src"));

                    count++;
                }

                for (Element youtube : entry) {
                    youTubeLink.add(youtube.getElementsByTag("iframe").attr("src"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Something very bad...");
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        for (int i = 0; i < count; i++) {
            FeedItem item = new FeedItem();

            item.setTitle(title.get(i));
            item.setLink(link.get(i));
            item.setDescription(description.get(i));

            if (youTubeLink.get(i).contains("youtube")){
                Log.d(TAG, "onPostExecute: WAS HERE");
                item.setImg(Helper.getYoutubeImg(youTubeLink.get(i)));
            }
            else if (img.get(i).contains("\u0060")) {
                item.setImg(img.get(i).replace("\u0060", "%60"));
            }
            else {
                item.setImg(img.get(i));
            }
            items.add(item);
        }
        refreshHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        onTaskCompleted.onTaskComplete();

    }
}

