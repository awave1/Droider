package com.apps.wow.droider.Article;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.apps.wow.droider.Adapters.ArticleAdapter;
import com.apps.wow.droider.Model.Post;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by awave on 2016-12-30.
 */

class NewArticleParser extends AsyncTask<String, String, String> {
    private static final String TAG = "NewArticleParser";

    private ArticleActivity activity;

    private boolean isFromBrowser = false;
    private ArrayList<Post> mSimilar = new ArrayList<>();

    public NewArticleParser with(ArticleActivity a) {
        activity = a;
        return this;
    }

    @Override
    protected String doInBackground(String... strings) {
        String url = strings[0];
        String html;
        try {
            Document document = Jsoup.connect(url).timeout(10000).get();
            document.select(".article-gallery__photos__item__content").remove();

            Elements elements = document.select("article[id^=post] .article-body");
            Elements similarElements = document.select(".popular-slider__list a");


            Log.d(TAG, "doInBackground: similar el: " + similarElements.size());

            for (Element el : similarElements) {
                mSimilar.add(new Post(
                        el.select(".post-link__title").text(),
                        el.select(".post-link__picture__image_wide").attr("src"),
                        el.select(".popular-slider__item").attr("href")
                ));
            }

            html = setupHtml(elements.html());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: Failed to fetch html content", e);
            html = setupHtml("Почему-то не получилось загрузить страницу. Попробуйте открыть статью заново");
        }

        return html;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        activity.getProgressBar().setVisibility(View.GONE);

        try {
            activity.loadArticle(result);
            if (!mSimilar.isEmpty())
                activity.binding.similarArticles
                        .setAdapter(new ArticleAdapter(mSimilar));
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG, "onPostExecute: Error loading html content", e);
            activity.loadArticle("Почему-то не получилось загрузить страницу. Попробуйте открыть статью заново");
        }
    }

    private String setupHtml(String html) {
        String head =
                "<head>" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                        "<link href='https://fonts.googleapis.com/css?family=Roboto:300,700italic,300italic' rel='stylesheet' type='text/css'>" +
                        style() +
                "</head>";

        return "<html>" + head + "<body><div class=\"container\">" + html + "</div></body></html>";
    }

    private String style() {
        return
            "<style>" +
                "body { " +
                    "margin:0; padding-top:8dp; " +
                    "font-family:\"Roboto\", sans-serif; " +
                    "font-size: 14px; " +
                    "color:" + activity.getWebViewTextColor() +
                "}" +
                ".container { " +
                    "padding-left:10px; padding-right:10px; padding-bottom:10px;" +
                "}" +
                ".article_image { " +
                    "margin-left:-16px;margin-right:-16px;" +
                "}" +
                ".iframe_container { " +
                    "margin-left:-16px; margin-right:-16px; " +
                    "position:relative; overflow:hidden;" +
                "}" +
                "a { " +
                    "color:" + activity.getWebViewLinkColor() + ";" +
                "}" +
                "iframe { " +
                    "max-width: 100%; width: 100%; height: 260px; allowfullscreen; " +
                "}" +
                "img { " +
                    "max-width: 100%; width: 100vW; height: auto; margin-bottom:10px; " +
                "}" +
                "table { " +
                    "border-collapse: collapse;" +
                    "overflow: hidden" +
                "}" +
                "td { " +
                    "padding: 3px;" +
                "}" +
                ".article-gallery__photos .article-gallery__photos__list { " +
                    "list-style-type: none; padding:0;margin:0; " +
                "}" +
                ".article-gallery__thumb { " +
                    "display: none; " +
                "}" +
                ".article-table { " +
                    "position: relative;" +
                    "background:" + activity.getWebViewTableColor() + ";" +
                "}" +
                ".article-table__table { " +
                    "width: 100%;" +
                    "background: " + activity.getWebViewTableColor() + ";" +

                "}" +
                ".article-table__head { " +
                    "background: " + activity.getWebViewTableHeaderColor() + ";" +
                "}" +
                ".article-table__head__cell {" +
                    "font-weight: bold;" +
                "}" +
                ".article-tech__header {" +
                    "background: " + activity.getWebViewTableHeaderColor() + ";" +
                    "padding-top: 55px;"+
                    "padding-bottom: 15px;" +
                "}" +
                ".article-tech__header__picture {" +
                    "background: no-repeat 50% 50%/cover;" +
                    "position: absolute;" +
                "}" +
                ".article-tech__header__title {" +
                    "font-size: 21px;" +
                    "padding-left: 5px;" +
                "}" +
                ".article-tech__info { " +
                    "background: " + activity.getWebViewTableColor() + ";" +
                    "padding: 10px 5px;" +
                "}" +
                ".article-tech__info__title {" +
                    "font-weight: bold;" +
                    "font-size: 20px;" +
                "}" +
                ".article-tech__info__content__key {" +
                    "font-size: 15px;" +
                    "margin-left: 5px;" +
                    "font-weight: bold;" +
                "}" +
                ".article-tech__info__content__value {" +
                    "margin-left: 5px;" +
                    "padding-left: 0;" +
                    "float: none;" +
                "}" +
            "</style>";
    }
}
