package com.apps.wow.droider.Article.repository

import com.apps.wow.droider.DB.Article
import io.realm.Realm
import rx.Observable

/**
 * Created by Jackson on 21/08/2017.
 */
class DatabaseArticleDataStore(private val mRealm: Realm?) : ArticleDataStore {

    override fun getArticle(url: String): Observable<String?> {
        if (mRealm?.where(Article::class.java)?.equalTo("articleUrl", url)?.findFirst() != null) {
            return Observable.just(mRealm.where(Article::class.java)
                    ?.equalTo("articleUrl", url)?.findFirst()?.articleHtml)
        }
        return Observable.just(null)
    }
}