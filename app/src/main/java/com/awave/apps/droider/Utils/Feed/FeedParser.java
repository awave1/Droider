package com.awave.apps.droider.Utils.Feed;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeedParser extends AsyncTask<String, Void, Void> {
    private static final String TAG = "FeedParser";

    private int count = 0;
    private String wrongMes = "";
    private Activity activity;
    private List<FeedItem> mFeedItems = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();
    private List<String> mUrlList = new ArrayList<>();
    private List<String> mDescrList = new ArrayList<>();
    private List<String> mImgUrlList = new ArrayList<>();
    private List<String> mYoutubeUrlList = new ArrayList<>();
    private boolean isPodcast = false;
    private OnTaskCompleted mOnTaskCompleted;

    public FeedParser(ArrayList<FeedItem> data, OnTaskCompleted onTaskCompleted, Activity activity, boolean isPodcast) {
        this.activity = activity;
        this.mFeedItems = data;
        this.mOnTaskCompleted = onTaskCompleted;
        this.isPodcast = isPodcast;
    }

    @Override
    protected Void doInBackground(String... params) {
        final Document[] document = new Document[1];
        document[0] = null;

        try {
            document[0] = Jsoup.connect(params[0]).timeout(30000).ignoreHttpErrors(true).get();
        } catch (IOException e) {
            e.printStackTrace();
            wrongMes = "Не получилось загрузить ещё статей\nПроверьте соединение и попробуйте перезайти в приложение";
        }
         if (document[0] != null) {
             Elements elements = document[0].select("div[id^=post]");
             Elements entry = document[0].select("div.entry");
             for (Element element : elements) {
                 mTitleList.add(element.getElementsByTag("a").attr("title"));
                 mUrlList.add(element.getElementsByTag("a").attr("href"));
                 mDescrList.add(element.getElementsByTag("p").text().substring(0, element.getElementsByTag("p").text().lastIndexOf(" ")));
                 if (!isPodcast)
                    mImgUrlList.add(element.getElementsByTag("img").attr("src"));
                 count++;
             }
             for (Element youtube : entry) {
                 mYoutubeUrlList.add(youtube.getElementsByTag("iframe").attr("src"));
             }
         }
        return null;
    }


    @Override
    protected void onPostExecute(final Void aVoid) {
        if(!wrongMes.equals("")) {
            new AlertDialog.Builder(activity).setTitle("Соединение нестабильно или прервано").setMessage("Проверьте своё соединение с интернетом и перезайдите в приложение")
                    .setNegativeButton("Выйти", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    })
                    .setPositiveButton("Перезайти", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                            activity.startActivity(activity.getIntent());
                        }
                    }).create().show();
        }
        else {
            for (int i = 0; i < count; i++) {
                FeedItem feedItem = new FeedItem();

                feedItem.setTitle(mTitleList.get(i));
                feedItem.setDescription(mDescrList.get(i));
                feedItem.setUrl(mUrlList.get(i));

                if(!isPodcast) {
                    if (mYoutubeUrlList.get(i).contains("youtube")) {
                        Log.d(TAG, "onPostExecute: set image from youtube");
                        feedItem.setImgUrl(Helper.getYoutubeImg(mYoutubeUrlList.get(i)));
                    } else if (mImgUrlList.get(i).contains("\u0060")) {
                        feedItem.setImgUrl(mImgUrlList.get(i).replace("\u0060", "%60"));
                    } else {
                        feedItem.setImgUrl(mImgUrlList.get(i));
                    }
                }
                else
                {
                    feedItem.setDrCastImg(R.drawable.dr_cast);
                }
                mFeedItems.add(feedItem);
            }

            mOnTaskCompleted.onTaskCompleted();
        }
    }
}
