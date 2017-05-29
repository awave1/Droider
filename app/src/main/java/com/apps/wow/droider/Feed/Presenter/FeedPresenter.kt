package com.apps.wow.droider.Feed.Presenter

import com.apps.wow.droider.Feed.View.FeedView
import com.apps.wow.droider.Model.FeedModel
import com.apps.wow.droider.Utils.Utils
import com.apps.wow.droider.api.DroiderApi
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

@InjectViewState
class FeedPresenter : MvpPresenter<FeedView>() {
    internal var api: DroiderApi = Utils.createRxService(DroiderApi::class.java, Utils.HOME_URL, true)

    fun loadData(category: String, slug: String, count: Int, offset: Int, clear: Boolean) {
        viewState.onLoadingFeed()
        api.getFeed(category, slug, count, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Response<FeedModel>>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        viewState.onLoadFailed()
                    }

                    override fun onNext(response: Response<FeedModel>) {
                        if (response.isSuccessful) {
                            viewState.onLoadCompleted(response.body(), clear)
                        }
                    }
                })
    }

    fun loadPopular() {
        viewState.onLoadingFeed()
        api.getPopular()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Response<FeedModel>>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        //                        getViewState().onLoadFailed();
                    }

                    override fun onNext(response: Response<FeedModel>) {
                        if (response.isSuccessful)
                            viewState.onLoadCompleted(response.body())
                    }
                })
    }
}
