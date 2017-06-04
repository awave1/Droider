package com.apps.wow.droider.Feed

import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.apps.wow.droider.Adapters.ArticleSimilarAdapter
import com.apps.wow.droider.Adapters.FeedAdapter
import com.apps.wow.droider.BuildConfig
import com.apps.wow.droider.DroiderBaseActivity
import com.apps.wow.droider.Feed.Interactors.FeedOrientation
import com.apps.wow.droider.Feed.Presenter.FeedPresenter
import com.apps.wow.droider.Feed.View.FeedView
import com.apps.wow.droider.Model.FeedModel
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Utils
import com.apps.wow.droider.databinding.FeedFragmentBinding
import com.arellomobile.mvp.MvpFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType


class FeedFragment : MvpFragment(), FeedView, OnTaskCompleted, SwipeRefreshLayout.OnRefreshListener {

    @InjectPresenter(type = PresenterType.GLOBAL)
    lateinit var presenter: FeedPresenter

    private var binding: FeedFragmentBinding? = null

    private var feedAdapter: FeedAdapter? = null

    private var currentCategory: String? = null

    private var currentSlug: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FeedFragmentBinding>(inflater, R.layout.feed_fragment, container, false)
        orientationDebugging()

        swipeRefreshLayoutSetup()
        currentCategory = arguments.getString(Utils.EXTRA_CATEGORY)
        currentSlug = arguments.getString(Utils.EXTRA_SLUG)
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        presenter.loadData(currentCategory!!, currentSlug!!, Utils.DEFAULT_COUNT, 0, true)
        presenter.loadPopular()
    }

    override fun onLoadingFeed() {
        binding!!.feedSwipeRefresh.isRefreshing = true
    }

    override fun onLoadCompleted(model: FeedModel, clear: Boolean) {
        //потому что при переходе на другой фрагмент и этот фрагмент не удаляется, благодаря setRetainInstance(true);
        // но все данные прикреплённые к ресайлеру удаляются, так как вью инфлейтится заново
        if (feedAdapter == null || clear) {
            Log.d(TAG, "onLoadCompleted: is null")
            FeedOrientation.offsetPortrait = 0
            FeedOrientation.offsetLandscape = 0

            binding!!.feedRecyclerView.setHasFixedSize(true)
            feedAdapter = FeedAdapter(model)
            binding!!.feedRecyclerView.adapter = feedAdapter
            initLayoutManager()
        } else {
            feedAdapter!!.feedModel.posts.addAll(model.posts)
        }
        onTaskCompleted()
    }

    override fun onLoadCompleted(model: FeedModel) {
        val parentActivity = activity as FeedActivity
        val layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()

        parentActivity.binding?.popularNews?.layoutManager = layoutManager
        parentActivity.binding?.popularNews?.adapter = ArticleSimilarAdapter(model.posts)
        try {
            snapHelper.attachToRecyclerView(parentActivity.binding?.popularNews)
        } catch (ise: IllegalStateException) {
            ise.printStackTrace()
        }

        onTaskCompleted()
    }

    override fun onLoadFailed() {
        onTaskCompleted()
        if (activity != null) {
            (activity as DroiderBaseActivity).initInternetConnectionDialog(activity)
        }
    }

    override fun onRefresh() {
        if (activity != null && activity.resources != null &&
                activity.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            presenter.loadData(currentCategory!!, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT,
                    FeedOrientation.offsetLandscape, true)
        } else {
            presenter.loadData(currentCategory!!, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT,
                    FeedOrientation.offsetPortrait, true)
        }
        presenter.loadPopular()
    }

    private fun swipeRefreshLayoutSetup() {
        binding!!.feedSwipeRefresh.setOnRefreshListener(this)
        binding!!.feedSwipeRefresh.setColorSchemeResources(android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark, android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark)
        binding!!.feedSwipeRefresh.setSize(SwipeRefreshLayout.DEFAULT)
    }


    @Synchronized override fun onTaskCompleted() {
        if (feedAdapter != null) {
            feedAdapter!!.notifyDataSetChanged()
            if (binding!!.feedSwipeRefresh.isRefreshing) {
                binding!!.feedSwipeRefresh.isRefreshing = false
            }
        } else {
            onRefresh()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding!!.feedSwipeRefresh.isRefreshing = true
    }

    private fun initLayoutManager() {
        if (isAdded && activity != null) {
            if (activity.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                setDoubleColFeedMode()
            } else {
                setSingleColFeedMode()
            }
        } else {
            Handler().postDelayed({ this.initLayoutManager() }, 500)
        }
    }

    private fun setDoubleColFeedMode() {
        sStaggeredGridLayoutManager = StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL)
        binding!!.feedRecyclerView.layoutManager = sStaggeredGridLayoutManager
        binding!!.feedRecyclerView
                .addOnScrollListener(object : FeedOrientation(activity, binding!!.feedSwipeRefresh) {
                    override fun loadNextPage() {
                        presenter
                                .loadData(Utils.CATEGORY_MAIN, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT,
                                        FeedOrientation.offsetLandscape, false)
                        onLoadingFeed()
                    }
                })
    }

    private fun setSingleColFeedMode() {
        sLinearLayoutManager = LinearLayoutManager(activity)
        binding!!.feedRecyclerView.layoutManager = sLinearLayoutManager
        binding!!.feedRecyclerView
                .addOnScrollListener(object : FeedOrientation(activity, binding!!.feedSwipeRefresh) {
                    override fun loadNextPage() {
                        presenter.loadData(currentCategory!!, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT,
                                FeedOrientation.offsetPortrait, false)
                        onLoadingFeed()
                        if (!feedAdapter!!.feedModel.posts.isEmpty()) {
                            onTaskCompleted()
                        }
                    }
                })
    }

    private fun orientationDebugging() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreateView: orientation = " + activity.resources
                    .configuration.orientation)
        }
    }

    companion object {

        private val TAG = "Feed"

        lateinit var sLinearLayoutManager: LinearLayoutManager

        lateinit var sStaggeredGridLayoutManager: StaggeredGridLayoutManager

        fun newInstance(category: String, slug: String): FeedFragment {
            val feedFragment = FeedFragment()
            val bundle = Bundle()
            bundle.putString(Utils.EXTRA_CATEGORY, category)
            bundle.putString(Utils.EXTRA_SLUG, slug)
            feedFragment.arguments = bundle
            feedFragment.retainInstance = true
            return feedFragment
        }
    }
}
