package com.apps.wow.droider.Article;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.apps.wow.droider.Model.Post;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import rx.Observable;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by Jackson on 14/05/2017.
 */

public class ArticleModel {

    Document mDocument;
    private ArrayList<Post> mSimilar = new ArrayList<>();

    private String mHtml;

    private String mWebViewTextColor;
    private String mWebViewLinkColor;
    private String mWebViewTableColor;
    private String mWebViewTableHeaderColor;

    public ArticleModel(Builder builder) {
        mWebViewTextColor = builder.getWebViewTextColor();
        mWebViewLinkColor = builder.getWebViewLinkColor();
        mWebViewTableColor = builder.getWebViewTableColor();
        mWebViewTableHeaderColor = builder.getWebViewTableHeaderColor();
    }

    public Observable<String> parseArticle(String url) {
        return Observable.fromCallable(() -> {
            mDocument = Jsoup.connect(url).timeout(10000).get();
            mDocument.select(".article-gallery__photos__item__content").remove();

            Elements elements = mDocument.select("article[id^=post] .article-body");
            Elements similarElements = mDocument.select(".popular-slider__list a");


            Log.d(TAG, "doInBackground: similar el: " + similarElements.size());

            for (Element el : similarElements) {
                mSimilar.add(new Post(
                        el.select(".post-link__title").text(),
                        el.select(".post-link__picture__image_wide").attr("src"),
                        el.select(".popular-slider__item").attr("href")
                ));
            }
            mHtml = ArticleModel.this.setupHtml(elements.html());
            return mHtml;
        }).subscribeOn(Schedulers.io());
    }

    @Nullable
    public ArrayList<Post> getSimilar() {
        if (!mSimilar.isEmpty())
            return mSimilar;
        else
            return null;
    }

    public Observable<Post> getPostDataForOutsideIntent(String url) {
        return Observable.fromCallable(() -> {
            mDocument = Jsoup.connect(url).timeout(10000).get();
            mDocument.select(".article-gallery__photos__item__content").remove();

            String img = mDocument.select(".cover").attr("style");
            if (!TextUtils.isEmpty(img))
                img = img.substring(img.indexOf("(") + 1, img.lastIndexOf(")"));
            Log.d(TAG, "Background: : " + img);
            Log.d(TAG, "getPostDataForOutsideIntent: " + mDocument.select("header .headline__content__title").text());
            Log.d(TAG, "getPostDataForOutsideIntent: " + mDocument.select("header .headline__content__intro").text());

            return new Post(img, mDocument.select("header .headline__content__title").text(),
                    mDocument.select("header .headline__content__intro").text(), url);
        }).subscribeOn(Schedulers.io());
    }

    //TODO extract to resources
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
                        "font-size: 18px; " +
                        "color:" + mWebViewTextColor +
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
                        "color:" + mWebViewLinkColor + ";" +
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
                        "background:" + mWebViewTableColor + ";" +
                        "}" +
                        ".article-table__table { " +
                        "width: 100%;" +
                        "background: " + mWebViewTableColor + ";" +

                        "}" +
                        ".article-table__head { " +
                        "background: " + mWebViewTableHeaderColor + ";" +
                        "}" +
                        ".article-table__head__cell {" +
                        "font-weight: bold;" +
                        "}" +
                        ".article-tech__header {" +
                        "background: " + mWebViewTableHeaderColor + ";" +
                        "padding-top: 55px;" +
                        "padding-bottom: 15px;" +
                        "}" +
                        ".article-tech__header__picture {" +
                        "background: no-repeat 50% 50%/cover;" +
                        "position: absolute;" +
                        "}" +
                        ".article-tech__header__title {" +
                        "font-size: 23px;" +
                        "padding-left: 5px;" +
                        "}" +
                        ".article-tech__info { " +
                        "background: " + mWebViewTableColor + ";" +
                        "padding: 10px 5px;" +
                        "}" +
                        ".article-tech__info__title {" +
                        "font-weight: bold;" +
                        "font-size: 22px;" +
                        "}" +
                        ".article-tech__info__content__key {" +
                        "font-size: 17px;" +
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

    public static class Builder {

        private String mWebViewTextColor;
        private String mWebViewLinkColor;
        private String mWebViewTableColor;
        private String mWebViewTableHeaderColor;

        public Builder setWebViewTextColor(String webViewTextColor) {
            mWebViewTextColor = webViewTextColor;
            return this;
        }

        public Builder setWebViewLinkColor(String webViewLinkColor) {
            mWebViewLinkColor = webViewLinkColor;
            return this;
        }

        public Builder setWebViewTableColor(String webViewTableColor) {
            mWebViewTableColor = webViewTableColor;
            return this;
        }

        public Builder setWebViewTableHeaderColor(String webViewTableHeaderColor) {
            mWebViewTableHeaderColor = webViewTableHeaderColor;
            return this;
        }

        public String getWebViewTextColor() {
            return mWebViewTextColor;
        }

        public String getWebViewLinkColor() {
            return mWebViewLinkColor;
        }

        public String getWebViewTableColor() {
            return mWebViewTableColor;
        }

        public String getWebViewTableHeaderColor() {
            return mWebViewTableHeaderColor;
        }

        public ArticleModel build() {
            return new ArticleModel(this);
        }
    }
}
