package com.apps.wow.droider.api

import com.apps.wow.droider.Model.FeedModel

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by Jackson on 20/12/2016.
 */

interface DroiderApi {

    @GET("/wp-content/themes/droider/feed.php")
    fun getFeed(@Query("category") category: String,
                @Query("slug") slug: String,
                @Query("count") count: Int,
                @Query("offset") offset: Int): Observable<Response<FeedModel>>

    @GET("/wp-content/themes/droider/feed.php?category=0&slug=promotion")
    fun getPopular(): Observable<Response<FeedModel>>
}
