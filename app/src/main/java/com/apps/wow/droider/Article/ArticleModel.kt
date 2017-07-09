package com.apps.wow.droider.Article

import android.content.ContentValues.TAG
import android.text.TextUtils
import android.util.Log
import com.apps.wow.droider.DB.Article
import com.apps.wow.droider.Model.Post
import com.apps.wow.droider.Utils.AppContext
import io.realm.Realm
import org.jsoup.Jsoup
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by Jackson on 14/05/2017.
 */

class ArticleModel(val mWebViewTextColor: String,
                   val mWebViewLinkColor: String,
                   val mWebViewTableColor: String,
                   val mWebViewTableHeaderColor: String) {

    private val mSimilar = ArrayList<Post>()

    private var mHtml: String? = null

    private lateinit var id: String

    fun parseArticle(url: String): Observable<String> {
        return Observable.fromCallable<String> {
            val mDocument = Jsoup.connect(url).timeout(10000).get()
            mDocument.select(".article-gallery__photos__item__content").remove()

            var elements = mDocument.select("article[id^=post] .article-body")
            val similarElements = mDocument.select(".popular-slider__list a")


            Log.d(TAG, "doInBackground: similar el: " + similarElements.size)

            for (el in similarElements) {
                mSimilar.add(Post(title =
                el.select(".post-link__title").text(), pictureWide =
                el.select(".post-link__picture__image_wide").attr("src"), url =
                el.select(".popular-slider__item").attr("href")
                ))
            }

            elements.map {
                if (it.select("iframe").toString().contains("droidercast.podster.fm")) {
                    val title = mDocument.select("header h1").text().replace("/", " ").replace("#", "№")
                    val frameHtml = it.select("iframe").toString()
                    val indexOfId = frameHtml.indexOf("droidercast.podster.fm/") + 23
                    if (frameHtml.substring(indexOfId, indexOfId + 3)[2] == '/')
                        id = frameHtml.substring(indexOfId, indexOfId + 2)
                    else
                        id = frameHtml.substring(indexOfId, indexOfId + 3) // это на случай когда станет трёх значным номер подкаста

                    it.select("iframe").wrap("<p><a href='droider://player/$id/$title'><b>Слушай в приложении</b></a></p>").wrap("<p><br></p>")
                }
            }

            mHtml = this@ArticleModel.setupHtml(elements.html())

            Realm.init(AppContext.context)
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            val article: Article = realm.createObject(Article::class.java)
            article.articleHtml = mHtml
            article.articleUrl = url
            realm.commitTransaction()
            mHtml
        }.subscribeOn(Schedulers.io())
    }

    val similar: ArrayList<Post>?
        get() {
            if (!mSimilar.isEmpty())
                return mSimilar
            else
                return null
        }

    fun getPostDataForOutsideIntent(url: String): Observable<Post> {
        return Observable.fromCallable {
            val mDocument = Jsoup.connect(url).timeout(10000).get()
            mDocument.select(".article-gallery__photos__item__content").remove()

            var img = mDocument.select(".cover").attr("style")
            if (!TextUtils.isEmpty(img))
                img = img.substring(img.indexOf("(") + 1, img.lastIndexOf(")"))
            Log.d(TAG, "Background: : " + img)
            Log.d(TAG, "getPostDataForOutsideIntent: " + mDocument.select("header .headline__content__title").text())
            Log.d(TAG, "getPostDataForOutsideIntent: " + mDocument.select("header .headline__content__intro").text())

            Post(pictureWide = img, title = mDocument.select("header .headline__content__title").text(),
                    description = mDocument.select("header .headline__content__intro").text())
        }.subscribeOn(Schedulers.io())
    }

    //TODO extract to resources
    private fun setupHtml(html: String): String {
        val head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<link href='https://fonts.googleapis.com/css?family=Roboto:300,700italic,300italic' rel='stylesheet' type='text/css'>" +
                style() +
                "</head>"

        return "<html>$head<body><div class=\"container\">$html</div></body></html>"
    }

    private fun style(): String {
        return "<style>" +
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
                "</style>"
    }
}
