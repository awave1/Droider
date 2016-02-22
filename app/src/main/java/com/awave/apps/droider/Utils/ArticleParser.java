package com.awave.apps.droider.Utils;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import com.awave.apps.droider.Main.AdapterMain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by awave on 2016-02-20.
 */
public class ArticleParser extends AsyncTask<String, Void, Void> {
    private static final String TAG = "ArticleParser";

    private WebView mArticle;
    private String html = "";

    public ArticleParser(WebView article){
        this.mArticle = article;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            Document document = Jsoup.connect(params[0]).get();
            Elements articleElements = document.select(".entry p");
            Elements articleImages = document.select(".entry img");
            Elements articleIframes = document.select(".entry iframe");

            articleImages.wrap("<div class=\"iframe_container\"></div>");
            articleIframes.wrap("<div class=\"article_image\"></div>");

            for (Element e: articleImages) {
                if (e.attr("src").equals(AdapterMain.getHeadImage())){
                    e.remove();
                    break;
                }
            }

            Log.d(TAG, "doInBackground: html before cleaning = " + articleElements.toString());

            articleElements.remove(0);
            articleElements.remove(1);

            html = this.setupHtml(articleElements.toString());
        }
        catch (IOException e){
            Log.e(TAG, "doInBackground: Failed to load article!", e.getCause());
            html = this.errorHtml();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mArticle.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", "");
    }

    private String setupHtml(String html) {
        String head = "<head>" +
                "<link href='https://fonts.googleapis.com/css?family=Roboto:300,700italic,300italic' rel='stylesheet' type='text/css'>" +
                "<style>" +
                "body{margin:0;padding:0;font-family:\"Roboto\", sans-serif;}" +
                ".container{padding-left:16px;padding-right:16px; padding-bottom:16px}" +
                ".article_image{margin-left:-16px;margin-right:-16px;}" +
                ".iframe_container{margin-left:-16px;margin-right:-16px;position:relative;overflow:hidden;}" +
                "iframe{max-width: 100%; width: 100%; height: 260px;}" +
                "img{max-width: 100%; width: auto; height: auto;}" +
                "</style></head>";
        return "<html>" + head + "<body><div class=\"container\">" + html + "</div></body></html>";
    }

    private String errorHtml(){
        String html = "<head>" +
                "<link href='https://fonts.googleapis.com/css?family=Roboto:300,700italic,300italic' rel='stylesheet' type='text/css'>" +
                "<style>" +
                "body{margin:0;padding:0;font-family:\"Roboto\", sans-serif;}" +
                "</style></head>"+
                "<h2>Что то пошло не так :(</h2>" +
                "<h3>Попробуй открыть заново</h3>";
        return html;
    }
}
