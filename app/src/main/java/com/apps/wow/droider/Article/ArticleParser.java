package com.apps.wow.droider.Article;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v8.renderscript.RSIllegalArgumentException;
import android.util.Log;
import android.view.View;
import com.apps.wow.droider.Utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class ArticleParser extends AsyncTask<String, Integer, String> {

    private Activity activity;
    private ArticleActivity Article = new ArticleActivity();

    private String html = "";
    private String img = "";
    private String title = "";
    private String descr = "";
    private Bitmap mBitmap = null;
    private boolean outIntent;
    private boolean isYoutube;
    private String YouTubeVideoURL;
    private Elements elements;
    private String TAG = "ArticleParser";


    ArticleParser(Activity a) {
        this.activity = a;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Log.d(TAG, "doInBackground: url: " + strings[0]);
            Document document = Jsoup.connect(strings[0]).timeout(10000).get();
//            elements = document.select(".entry p, .entry ul li, .entry ol li");
            elements = document.select("article[id^=post] .article-body");

//            isYoutube = elements.get(1).html().contains("iframe");
            setIsYoutube(isYoutube);
            Log.d(TAG, "doInBackground: isYoutube  " + isYoutube);

            Elements imgs = document.select(".entry img");
            Elements iframe = document.select(".entry iframe, .entry p iframe ");
            Elements titleDiv = document.select(".headline__content__title");

            iframe.wrap("<div class=\"iframe_container\"></div>");
            imgs.wrap("<div class=\"article_image\"></div>");

            if (isYoutube) {
                YouTubeVideoURL = Utils.trimYoutubeId(elements.get(1).select(".iframe_container iframe").attr("src"));
                Log.d(TAG, "doInBackground: YouTubeVideoURL  " + YouTubeVideoURL);
                elements.get(1).select(".iframe_container").remove();
            }
            if (outIntent) {
                this.title = titleDiv.text();
//                if (isYoutube) {
//                    img = Utils.getYoutubeImg(elements.get(1).select(".iframe_container iframe").attr("src"));
//                } else {
//                    img = elements.get(1).select(".article_image img").attr("src");
//                }
//                descr = elements.get(0).text() + " " + elements.get(2).text();
//
//                Picasso.with(activity).load(img).into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        mBitmap = bitmap;
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//                        Log.e(TAG, "doInBackground: Error fetching bitmap from url! (url: " + img + " )");
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                });
            }

//                Log.d(TAG, "doInBackground: " + elements.toString());
//            elements.remove(0);
//                Log.d(TAG, "doInBackground: without element(0) " + elements.toString());
//            if (!elements.isEmpty() && elements.get(1).hasText()) {
//                    Log.d(TAG, "doInBackground: HASTEXT" + elements.get(1).hasText());
//                elements.remove(1);
//                    Log.d(TAG, "doInBackground:  without element(1) " + elements.toString());
//            }
            html = setupHtml(elements.toString());
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch html", e.getCause());
            html = setupHtml("Почему-то не получилось загрузить страницу. Попробуйте заново открыть статью");
        }
        return "";
    }

    @Override
    protected void onPostExecute(String aVoid) {
        Article.getProgressBar().setVisibility(View.GONE);
        if (outIntent) {
            try {
                //ошибка вылетала(переполнение памяти из-за блюра) когда открываешь статью(к примеру ту же самую) через "открыть в браузере"
                if (Article.isBlur)
                    Article.getArticleImg().setImageBitmap(Utils.applyBlur(mBitmap, activity));
                else
                    Article.getArticleImg().setImageBitmap(mBitmap);
            } catch (NullPointerException | RSIllegalArgumentException npe) {
                npe.printStackTrace();
                Article.getArticleImg().setImageBitmap(mBitmap);
            }
            Article.getArticleHeader().setText(this.title);
            Article.setArticleTitle(title);
            Article.getmArticleShortDescription().setText(this.descr);
        }

        try {
            Article.getmArticle().loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", "");
            Log.d(TAG, "onPostExecute: " + html);
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG, "onPostExecute: Error loading html content", e.getCause());
        }
    }

    private String setupHtml(String html) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<link href='https://fonts.googleapis.com/css?family=Roboto:300,700italic,300italic' rel='stylesheet' type='text/css'>" +
                "<style>" +
                "body{margin:0;padding-top:8dp;font-family:\"Roboto\", sans-serif; font-size: 14px; color:" + Article.webViewTextColor + "}" +
                ".container{padding-left:16px;padding-right:16px; padding-bottom:36px;}" +
                ".article_image{margin-left:-16px;margin-right:-16px;}" +
                ".iframe_container{margin-left:-16px;margin-right:-16px;position:relative;overflow:hidden;}" +
                "a {color:" + Article.webViewLinkColor + ";}" +
                "iframe{max-width: 100%; width: 100%; height: 260px; allowfullscreen; }" +
                "img{max-width: 100%; width: 100vW; height: auto;}" +
                "</style></head>";
        return "<html>" + head + "<body><div class=\"container\">" + html + "</div></body></html>";
    }

    boolean isYoutube() {
        Log.d(TAG, "isYoutube: " + isYoutube);
        return isYoutube;
    }

    private void setIsYoutube(boolean youtube) {
        isYoutube = youtube;
    }

    String getYouTubeVideoURL() {
        return YouTubeVideoURL;
    }

    void isOutIntent(boolean isOut) {
        this.outIntent = isOut;
    }


}