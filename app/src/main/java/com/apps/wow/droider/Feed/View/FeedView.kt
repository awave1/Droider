package com.apps.wow.droider.Feed.View

import com.apps.wow.droider.Model.FeedModel
import com.arellomobile.mvp.MvpView

interface FeedView : MvpView {
    fun onLoadingFeed()

    fun onLoadCompleted(model: FeedModel, clear: Boolean)

    fun onPopularLoadCompleted(model: FeedModel)

    fun onLoadFailed()
}