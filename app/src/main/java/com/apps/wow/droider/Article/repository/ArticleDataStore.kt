package com.apps.wow.droider.Article.repository

import rx.Observable

/**
 * Created by Jackson on 21/08/2017.
 */
interface ArticleDataStore {

    fun getArticle(url: String): Observable<String?>
}