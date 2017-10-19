package com.apps.wow.droider.Feed.Presenter

import com.apps.wow.droider.Feed.Interactors.FeedLoadingInteractor
import com.apps.wow.droider.Feed.View.FeedView
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import rx.android.schedulers.AndroidSchedulers

@InjectViewState
class FeedPresenter : MvpPresenter<FeedView>() {

    val mFeedLoadingInteractor = FeedLoadingInteractor()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadPopular()
    }

    fun loadData(category: String, slug: String, count: Int, offset: Int, clear: Boolean) {
        viewState.onLoadingFeed()
        mFeedLoadingInteractor.loadFeed(category, slug, count, offset)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ if (it.isSuccessful && it.body() != null) viewState.onLoadCompleted(it.body()!!, clear) },
                        { viewState.onLoadFailed() })
    }

    fun loadPopular() {
        viewState.onLoadingFeed()
        mFeedLoadingInteractor.loadPopular()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ if (it.isSuccessful) viewState.onPopularLoadCompleted(it.body()!!) })
    }
}
