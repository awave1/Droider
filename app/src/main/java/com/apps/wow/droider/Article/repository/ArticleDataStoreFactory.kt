package com.apps.wow.droider.Article.repository

import com.apps.wow.droider.Article.ArticleModel
import io.realm.Realm
import rx.Observable

/**
 * Created by Jackson on 21/08/2017.
 */

class ArticleDataStoreFactory {

    fun getArticle(url: String, realm: Realm?, model: ArticleModel): Observable<String?> {
        val html: Observable<String?> = DatabaseArticleDataStore(realm).getArticle(url)

        return if (html == Observable.just(null)) {
            NetworkArticleDataStore(model).getArticle(url)
        } else {
            html
        }
    }
}
