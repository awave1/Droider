package com.apps.wow.droider.Feed.Interactors;


import android.os.AsyncTask;
import android.util.Log;

import com.apps.wow.droider.Model.FeedModel;
import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class FeedParser extends AsyncTask<String, Void, Void> {
    private static final String TAG = "FeedParser";

    private boolean cannotConnect = false;
    private FeedModel feedModel;
    private ArrayList<FeedModel> feedList = new ArrayList<>();
    private boolean isPodcast = false;
    private OnLoadingInteractorFinishedListener listener;

    public FeedParser(ArrayList<FeedModel> data, OnLoadingInteractorFinishedListener listener, boolean isPodcast) {
        this.feedList = data;
        this.listener = listener;
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
            cannotConnect = true;
        }
        if (document[0] != null) {
            Elements elements = document[0].select("[id^=post]");
            for (Element element : elements) {
                feedModel = new FeedModel();
                feedModel.setTitle(element.getElementsByTag("a").attr("title"));
                feedModel.setDescription(element.getElementsByTag("p").text().substring(0, element.getElementsByTag("p").text().lastIndexOf(" ")));
                feedModel.setUrl(element.getElementsByTag("a").attr("href"));

                if (!isPodcast) {
                    try {
                        if (element.children().size() >= 3 && element.child(1).getElementsByTag("iframe").attr("src").contains("youtube")) {
                            Log.d(TAG, "onPostExecute: set image from youtube");
                            feedModel.setImgUrl(Utils.getYoutubeImg(element.child(1).getElementsByTag("iframe").attr("src")));
                        } else if (element.child(1).getElementsByTag("img").attr("src").contains("\u0060")) {
                            feedModel.setImgUrl(element.child(1).getElementsByTag("img").attr("src").replace("\u0060", "%60"));
                        } else {
                            feedModel.setImgUrl(element.child(1).getElementsByTag("img").attr("src"));
                        }
                    } catch (IndexOutOfBoundsException ioobe) {
                        ioobe.printStackTrace();
                    }
                } else
                    feedModel.setDrCastImg(R.drawable.dr_cast);

                feedList.add(feedModel);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(final Void aVoid) {
        if (cannotConnect)
            listener.onNetworkFailure();
        else
            listener.OnCompleted(feedList);
    }
}
