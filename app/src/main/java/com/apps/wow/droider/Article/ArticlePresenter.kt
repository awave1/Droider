package com.apps.wow.droider.Article

import android.util.Log
import com.apps.wow.droider.Article.repository.ArticleDataStoreFactory
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.AppContext
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.realm.Realm
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

/**
 * Created by Jackson on 14/05/2017.
 */

@InjectViewState
class ArticlePresenter : MvpPresenter<ArticleView>() {

    lateinit var mUrl: String
    lateinit var mArticleModel: ArticleModel

    fun provideData(mUrl: String, model: ArticleModel) {
        this.mUrl = mUrl
        mArticleModel = model
    }

    fun parseArticle(mRealm: Realm?) {
        ArticleDataStoreFactory().getArticle(mUrl, mRealm, mArticleModel)
                .subscribe({
                    viewState.changeLoadingVisibility(false)
                    viewState.loadArticle(it!!)
                    loadSimilar()
                }, {
                    viewState.changeLoadingVisibility(false)
                    viewState.showErrorLoading(AppContext.context.getString(R.string.errorHtml))
                })
    }

    private fun loadSimilar() {
        if (mArticleModel.similar != null)
            viewState.setupSimilar(mArticleModel.similar!!)
        else
            viewState.hideSimilar()
    }

    fun getPostDataForOutsideEvent() {
        mArticleModel.getPostDataForOutsideIntent(mUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ post -> viewState.setupNecessaryFields(post) }
                ) { e -> Timber.e(e, "getPostDataForOutsideEvent: ") }
    }
}

