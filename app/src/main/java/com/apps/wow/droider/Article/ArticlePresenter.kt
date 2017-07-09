package com.apps.wow.droider.Article

import android.util.Log
import com.apps.wow.droider.DB.Article

import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.AppContext
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.realm.Realm

import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by Jackson on 14/05/2017.
 */

@InjectViewState
class ArticlePresenter : MvpPresenter<ArticleView>() {
    private var mUrl: String? = null

    private var mArticleModel: ArticleModel? = null

    fun provideData(mUrl: String, model: ArticleModel) {
        this.mUrl = mUrl
        mArticleModel = model
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d("TAG", "onFirstViewAttach: ")

    }

    fun parseArticle(mRealm: Realm) {
        if (mRealm.where(Article::class.java).equalTo("articleUrl", mUrl).findFirst() != null) {
            mRealm.where(Article::class.java)
                    .equalTo("articleUrl", mUrl).findFirst().articleHtml?.let { viewState.loadArticle(it) }
            Log.d("parseArticle", "mRealm: ")
            viewState.changeLoadingVisibility(false)

        } else {
            viewState.changeLoadingVisibility(true)
            mArticleModel!!.parseArticle(mUrl!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<String>() {
                        override fun onCompleted() {

                        }

                        override fun onError(e: Throwable) {
                            viewState.changeLoadingVisibility(false)
                            viewState.showErrorLoading(AppContext.context.getString(R.string.errorHtml))
                        }

                        override fun onNext(s: String) {
                            viewState.changeLoadingVisibility(false)
                            viewState.loadArticle(s)
                            loadSimilar()
                        }
                    })
        }
    }

    fun loadSimilar() {
        if (mArticleModel!!.similar != null) {
            viewState.setupSimilar(mArticleModel!!.similar!!)
        } else {
            viewState.hideSimilar()
        }
    }

    fun getPostDataForOutsideEvent() {
        mArticleModel!!.getPostDataForOutsideIntent(mUrl!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ post -> viewState.setupNecessaryFields(post) }
                ) { e -> Log.e(TAG, "getPostDataForOutsideEvent: ", e) }
    }

    companion object {

        val TAG = "ArticlePresenter"
    }
}

