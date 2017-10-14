package com.apps.wow.droider.Feed.Interactors

import android.app.Activity
import android.content.res.Configuration
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import com.apps.wow.droider.Feed.FeedFragment
import com.apps.wow.droider.Utils.Const
import timber.log.Timber


abstract class FeedOrientation(private var mActivity: Activity?, private val swipeRefresher: SwipeRefreshLayout) : RecyclerView.OnScrollListener() {

    // Portrait
    private var previousTotal_portrait = 0
    private val visibleThreshold_portrait: Byte = 4
    private var firstVisibleItem_portrait: Byte = 0
    private var visibleItemCount_portrait: Byte = 0
    private var totalItemCount_portrait: Byte = 0
    private var isLoading_portrait = true

    // Landscape
    private var previousTotal_landscape = 0
    private val visibleThreshold_landscape: Byte = 3 // 3
    private var firstVisibleItem_landscape: IntArray? = null
    private var visibleItemCount_landscape: Byte = 0
    private var totalItemCount_landscape: Byte = 0
    private var isLoading_landscape: Boolean = false

    abstract fun loadNextPage()

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        if (newState == 0) {
            if (mActivity!!.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE)
                doubleColsLoading(mActivity, recyclerView, FeedFragment.sStaggeredGridLayoutManager)
            else
                singleColsLoading(mActivity, recyclerView, FeedFragment.sLinearLayoutManager)
        }
    }

    private fun singleColsLoading(activity: Activity?, recyclerView: RecyclerView?, layoutManager: LinearLayoutManager) {
        this.mActivity = activity
        visibleItemCount_portrait = recyclerView?.childCount!!.toByte()
        totalItemCount_portrait = layoutManager.itemCount.toByte()

        Timber.d("singleColsLoading: totalItemCount_portrait = %s", totalItemCount_portrait)
        firstVisibleItem_portrait = layoutManager.findFirstVisibleItemPosition().toByte()


        Timber.d("singleColsLoading: firstVisibleItem_portrait = %s", firstVisibleItem_portrait)

        if (isLoading_portrait && totalItemCount_portrait > previousTotal_portrait) {
            isLoading_portrait = false
            previousTotal_portrait = totalItemCount_portrait.toInt()
        }

        if (!isLoading_portrait && totalItemCount_portrait - visibleItemCount_portrait <= firstVisibleItem_portrait + visibleThreshold_portrait) {
            Timber.d("singleColsLoading: end has been reached, loading next page")
            offsetPortrait += Const.DEFAULT_COUNT
            loadNextPage()
            isLoading_portrait = true
        }

        if (firstVisibleItem_portrait + 2 == totalItemCount_portrait.toInt()) {
            Handler().post { swipeRefresher.isRefreshing = true }
        }
    }

    private fun doubleColsLoading(activity: Activity?, recyclerView: RecyclerView?, staggeredGridLayoutManager: StaggeredGridLayoutManager) {
        this.mActivity = activity
        visibleItemCount_landscape = recyclerView?.childCount!!.toByte()
        totalItemCount_landscape = staggeredGridLayoutManager.itemCount.toByte()

        Timber.d("doubleColsLoading: totalItemCount_landscape = " + totalItemCount_landscape)

        firstVisibleItem_landscape = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItem_landscape)

        Timber.d("singleColsLoading: firstVisibleItem_portrait (length) = %s", firstVisibleItem_landscape!!.size)
        Timber.d("doubleColsLoading: firstVisibleItem_landscape [0]/[1] = \n %s %s",
                 firstVisibleItem_landscape!![0], "/" + firstVisibleItem_landscape!![1])

        if (firstVisibleItem_landscape != null && firstVisibleItem_landscape!!.size > 0) {
            previousTotal_landscape = firstVisibleItem_landscape!![0]
        }

        if (isLoading_landscape && totalItemCount_landscape > previousTotal_landscape) {
            isLoading_landscape = false
            previousTotal_landscape = firstVisibleItem_landscape!![0]
        }

        if (!isLoading_landscape && totalItemCount_landscape - visibleItemCount_landscape <= previousTotal_landscape + visibleThreshold_landscape) {
            offsetLandscape += Const.DEFAULT_COUNT
            loadNextPage()
            isLoading_landscape = true
        }

        if (firstVisibleItem_landscape!![0] + 2 >= totalItemCount_landscape || firstVisibleItem_landscape!![1] + 2 >= totalItemCount_landscape) {
            Handler().post { swipeRefresher.isRefreshing = true }
        }
    }

    companion object {
        var offsetPortrait = 0
        var offsetLandscape = 1
    }
}
