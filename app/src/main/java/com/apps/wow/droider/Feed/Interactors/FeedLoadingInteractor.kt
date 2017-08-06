package com.apps.wow.droider.Feed.Interactors

import com.apps.wow.droider.Model.FeedModel
import com.apps.wow.droider.Utils.Const
import com.apps.wow.droider.Utils.Utils
import com.apps.wow.droider.api.DroiderApi
import retrofit2.Response
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by Jackson on 05/08/2017.
 */
class FeedLoadingInteractor {

    internal var api: DroiderApi = Utils.createRxService(DroiderApi::class.java, Const.HOME_URL, true)

    fun loadFeed(category: String, slug: String, count: Int, offset: Int): Observable<Response<FeedModel>> {
        return api.getFeed(category, slug, count, offset)
                .subscribeOn(Schedulers.io())
    }

    fun loadPopular(): Observable<Response<FeedModel>> {
        return api.getPopular()
                .subscribeOn(Schedulers.io());
    }
}