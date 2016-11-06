package com.awave.apps.droider.Feed.View;

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

import com.awave.apps.droider.DroiderBaseActivity;
import com.awave.apps.droider.Feed.Interactors.FeedOrientation;
import com.awave.apps.droider.Feed.OnTaskCompleted;
import com.awave.apps.droider.Feed.Presentor.FeedPresenterImpl;
import com.awave.apps.droider.Main.FeedRecyclerViewAdapter;
import com.awave.apps.droider.Model.FeedModel;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Utils;
import com.awave.apps.droider.databinding.FeedFragmentBinding;

import java.util.ArrayList;


public class FeedFragment extends android.app.Fragment implements
        FeedView, OnTaskCompleted, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Feed";
    public static LinearLayoutManager sLinearLayoutManager;
    public static StaggeredGridLayoutManager sStaggeredGridLayoutManager;
    private FeedPresenterImpl presenter;
    private FeedFragmentBinding binding;
    private FeedRecyclerViewAdapter feedRecyclerViewAdapter;
    private boolean isPodCast = false;

    public static FeedFragment instance(String feedUrl) {
        FeedFragment feedFragment = new FeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.EXTRA_ARTICLE_URL, feedUrl);
        feedFragment.setArguments(bundle);
        return feedFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.feed_fragment, container, false);
        presenter = new FeedPresenterImpl();
        presenter.attachView(FeedFragment.this, this, this);
        orientationDebugging();

        if (Utils.DROIDER_CAST_URL.equals(getArguments().getString(Utils.EXTRA_ARTICLE_URL)))
            isPodCast = true;

        swipeRefreshLayoutSetup();

        presenter.getDataWithClearing(getArguments().getString(Utils.EXTRA_ARTICLE_URL));

        return binding.getRoot();
    }


    @Override
    public void onLoadingFeed() {
        binding.feedSwipeRefresh.setRefreshing(true);
    }

    @Override
    public void onLoadComplete(ArrayList<FeedModel> list) {
        if (feedRecyclerViewAdapter == null) {
            binding.feedRecyclerView.setHasFixedSize(true);
            feedRecyclerViewAdapter = new FeedRecyclerViewAdapter(getActivity(), list, isPodCast);
            binding.feedRecyclerView.setAdapter(feedRecyclerViewAdapter);
            initLayoutManager();
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
                    presenter.getDataWithClearing(getArguments().getString(Utils.EXTRA_ARTICLE_URL));
                    FeedOrientation.nextPage_portrait = 1;
                    FeedOrientation.nextPage_landscape = 1;
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
        onLoadingFeed();
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            setDoubleColFeedMode();
        } else {
            setSingleColFeedMode();
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
                        presenter.loadMore(getArguments().getString(Utils.EXTRA_ARTICLE_URL)
                                + FeedOrientation.nextPage_landscape);
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
                        presenter.loadMore(getArguments().getString(Utils.EXTRA_ARTICLE_URL)
                                + FeedOrientation.nextPage_portrait);
                        onLoadingFeed();
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
