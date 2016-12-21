package com.apps.wow.droider.Feed.View;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.apps.wow.droider.Adapters.FeedRecyclerViewAdapter;
import com.apps.wow.droider.DroiderBaseActivity;
import com.apps.wow.droider.Feed.Interactors.FeedOrientation;
import com.apps.wow.droider.Feed.OnTaskCompleted;
import com.apps.wow.droider.Feed.Presentor.FeedPresenterImpl;
import com.apps.wow.droider.Model.NewFeedModel;
import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.databinding.FeedFragmentBinding;

import okhttp3.internal.Util;


public class FeedFragment extends android.app.Fragment implements
        FeedView, OnTaskCompleted, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Feed";
    public static LinearLayoutManager sLinearLayoutManager;
    public static StaggeredGridLayoutManager sStaggeredGridLayoutManager;
    private FeedPresenterImpl presenter;
    private FeedFragmentBinding binding;
    private FeedRecyclerViewAdapter feedRecyclerViewAdapter;
    private boolean isPodCast = false;

    private String currentCategory;

    public static FeedFragment instance(String category) {
        FeedFragment feedFragment = new FeedFragment();
        Bundle bundle = new Bundle();
        // todo remove this
        bundle.putString(Utils.EXTRA_ARTICLE_URL, category);

        bundle.putString(Utils.EXTRA_CATEGORY, category);
        feedFragment.setArguments(bundle);
        feedFragment.setRetainInstance(true);
        return feedFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.feed_fragment, container, false);
        presenter = new FeedPresenterImpl();
        presenter.attachView(FeedFragment.this, this, this);
        orientationDebugging();

        //if (Utils.DROIDER_CAST_URL.equals(getArguments().getString(Utils.EXTRA_ARTICLE_URL)))
        //    isPodCast = true;

        swipeRefreshLayoutSetup();
        currentCategory = this.getArguments().getString(Utils.EXTRA_CATEGORY);
        presenter.loadData(currentCategory, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT, 0);

        //presenter.getDataWithClearing(getArguments().getString(Utils.EXTRA_ARTICLE_URL));

        return binding.getRoot();
    }


    @Override
    public void onLoadingFeed() {
        binding.feedSwipeRefresh.setRefreshing(true);
    }

    @Override
    public void onLoadComplete(NewFeedModel model) {
        if (feedRecyclerViewAdapter == null) {
            Log.d(TAG, "onLoadComplete: is null");


            binding.feedRecyclerView.setHasFixedSize(true);
            feedRecyclerViewAdapter = new FeedRecyclerViewAdapter(model, isPodCast);
            binding.feedRecyclerView.setAdapter(feedRecyclerViewAdapter);
            initLayoutManager();
        } else {
            feedRecyclerViewAdapter.getFeedModel().getPosts().addAll(model.getPosts());
            feedRecyclerViewAdapter.notifyDataSetChanged();
        }
        onTaskCompleted();
    }

    @Override
    public void onLoadFailed() {
        onTaskCompleted();
        ((DroiderBaseActivity) getActivity()).initInternetConnectionDialog(getActivity());
    }


    private void swipeRefreshLayoutSetup() {
        binding.feedSwipeRefresh.setOnRefreshListener(this);
        binding.feedSwipeRefresh.setColorSchemeResources(
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark);
        binding.feedSwipeRefresh.setSize(SwipeRefreshLayout.DEFAULT);
    }

    @Override
    public void onRefresh() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (Utils.isOnline(getActivity())) {
                    FeedOrientation.nextPage_portrait = 1;
                    FeedOrientation.nextPage_landscape = 1;

                    FeedOrientation.offsetPortrait = 0;
                    FeedOrientation.offsetLandscape = 0;

                    feedRecyclerViewAdapter.getFeedModel().getPosts().clear();
                    feedRecyclerViewAdapter.notifyDataSetChanged();

                    if ((getActivity().getResources().getConfiguration().screenLayout &
                            Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE)
                        presenter.loadData(currentCategory, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT, FeedOrientation.offsetLandscape);
                    else
                        presenter.loadData(currentCategory, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT, FeedOrientation.offsetPortrait);
                    if (!feedRecyclerViewAdapter.getFeedModel().getPosts().isEmpty())
                        onTaskCompleted();

                } else {
                    ((DroiderBaseActivity) getActivity()).initInternetConnectionDialog(getActivity());
                }
            }
        });
    }

    @Override
    public synchronized void onTaskCompleted() {
        feedRecyclerViewAdapter.notifyDataSetChanged();
        if (binding.feedSwipeRefresh.isRefreshing())
            binding.feedSwipeRefresh.setRefreshing(false);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                binding.feedSwipeRefresh.setRefreshing(true);
            }
        });
    }

    private void initLayoutManager() {
        if (isAdded() && getActivity() != null) {
            onLoadingFeed();
            if ((getActivity().getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                setDoubleColFeedMode();
            } else {
                setSingleColFeedMode();
            }
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initLayoutManager();
                }
            }, 500);
        }
    }

    private void setDoubleColFeedMode() {
        sStaggeredGridLayoutManager = new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL);
        binding.feedRecyclerView.setLayoutManager(sStaggeredGridLayoutManager);
        binding.feedRecyclerView.addOnScrollListener(
                new FeedOrientation(getActivity(), binding.feedSwipeRefresh) {
                    @Override
                    public void loadNextPage() {
                        presenter.loadData(Utils.CATEGORY_MAIN, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT, FeedOrientation.offsetLandscape);
                        onLoadingFeed();
                    }
                });
    }

    private void setSingleColFeedMode() {
        sLinearLayoutManager = new LinearLayoutManager(getActivity());
        binding.feedRecyclerView.setLayoutManager(sLinearLayoutManager);
        binding.feedRecyclerView.addOnScrollListener(
                new FeedOrientation(getActivity(), binding.feedSwipeRefresh) {
                    @Override
                    public void loadNextPage() {
                        presenter.loadData(currentCategory, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT, FeedOrientation.offsetPortrait);
                        onLoadingFeed();
                        if (!feedRecyclerViewAdapter.getFeedModel().getPosts().isEmpty())
                            onTaskCompleted();
                    }
                });
    }

    private void orientationDebugging() {
        Log.d(TAG, "onCreateView: orientation = " + getActivity().getResources()
                .getConfiguration().orientation);
        Log.d(TAG, "onCreateView: getArguments().getString(EXTRA_ARTICLE_URL) = " + getArguments()
                .getString(Utils.EXTRA_ARTICLE_URL));
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
