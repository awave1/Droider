package com.apps.wow.droider.Article.repository

import com.apps.wow.droider.Article.ArticleModel
import rx.Observable

/**
 * Created by Jackson on 21/08/2017.
 */
class NetworkArticleDataStore(private val model: ArticleModel) : ArticleDataStore {

    override fun getArticle(url: String): Observable<String?> {
        return model.parseArticle(url)
    }
}