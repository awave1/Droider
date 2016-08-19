package com.awave.apps.droider.Elements.MainScreen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awave.apps.droider.Main.FeedRecyclerViewAdapter;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Feed.FeedItem;
import com.awave.apps.droider.Utils.Feed.FeedOrientation;
import com.awave.apps.droider.Utils.Feed.FeedParser;
import com.awave.apps.droider.Utils.Feed.OnTaskCompleted;
import com.awave.apps.droider.Utils.Helper;

import java.util.ArrayList;


public class Feed extends android.app.Fragment implements
        OnTaskCompleted, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Feed";

    private static SwipeRefreshLayout sSwipeRefreshLayout;
    public static LinearLayoutManager sLinearLayoutManager;
    public static StaggeredGridLayoutManager sStaggeredGridLayoutManager;
    private static ArrayList<FeedItem> sFeedItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private FeedRecyclerViewAdapter feedRecyclerViewAdapter;
    private boolean isPodcast = false;

    public static Feed instance(String feedUrl) {
        Feed feed = new Feed();
        Bundle bundle = new Bundle();
        bundle.putString(Helper.EXTRA_ARTICLE_URL, feedUrl);
        feed.setArguments(bundle);
        return feed;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        orientationDebugging();

        if (Helper.DROIDER_CAST_URL.equals(getArguments().getString(Helper.EXTRA_ARTICLE_URL)))
            isPodcast = true;

        swipeRefreshLayoutSetup(view);
        recyclerViewSetup(view);
        initLayoutManager();
        return view;
    }

    private void recyclerViewSetup(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.feed_recycler_view);
        feedRecyclerViewAdapter = new FeedRecyclerViewAdapter(getActivity(), sFeedItems, isPodcast);
        mRecyclerView.setHasFixedSize(true);
    }

    private void orientationDebugging() {
        Log.d(TAG, "onCreateView: orientation = " + getActivity().getResources()
                .getConfiguration().orientation);
        Log.d(TAG, "onCreateView: getArguments().getString(EXTRA_ARTICLE_URL) = " + getArguments()
                .getString(Helper.EXTRA_ARTICLE_URL));
    }

    private void swipeRefreshLayoutSetup(View v) {
        sSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.feed_swipe_refresh);
        sSwipeRefreshLayout.setOnRefreshListener(this);
        sSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark);
        sSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
    }

    @Override
    public void onRefresh() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (Helper.isOnline(getActivity())) {
                    initLayoutManager();
                } else {
                    Helper.initInternetConnectionDialog(getActivity());
                }
            }
        });
    }

    @Override
    public synchronized void onTaskCompleted() {
        feedRecyclerViewAdapter.notifyDataSetChanged();
        if (sSwipeRefreshLayout.isRefreshing())
            sSwipeRefreshLayout.setRefreshing(false);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                sSwipeRefreshLayout.setRefreshing(true);
            }
        });
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                setOrientationLandscape();
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                setOrientationPortrait();
                break;
        }
    }

    private void initLayoutManager() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                sSwipeRefreshLayout.setRefreshing(true);
            }
        });

        Configuration configuration = getActivity().getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setOrientationPortrait();
        }
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setOrientationLandscape();
        }
    }

    private void setOrientationLandscape() {
        sStaggeredGridLayoutManager = new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sStaggeredGridLayoutManager);
        mRecyclerView.setAdapter(feedRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(
                new FeedOrientation(getActivity(), sSwipeRefreshLayout) {
                    @Override
                    public void loadNextPage() {
                        loadMore(getArguments().getString(Helper.EXTRA_ARTICLE_URL)
                                + FeedOrientation.nextPage_landscape);
                        onTaskCompleted();
                    }
                });

        getFeeds(getArguments().getString(Helper.EXTRA_ARTICLE_URL));
    }

    private void setOrientationPortrait() {
        sLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(sLinearLayoutManager);
        mRecyclerView.setAdapter(feedRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(
                new FeedOrientation(getActivity(), sSwipeRefreshLayout) {
                    @Override
                    public void loadNextPage() {
                        loadMore(getArguments().getString(Helper.EXTRA_ARTICLE_URL)
                                + FeedOrientation.nextPage_portrait);
                        onTaskCompleted();
                    }
                });
        getFeeds(getArguments().getString(Helper.EXTRA_ARTICLE_URL));
    }

    private void loadMore(String url) {
        new FeedParser(sFeedItems, this, getActivity(), isPodcast).execute(url);
    }

    private void getFeeds(String url) {
        sFeedItems.clear();
        new FeedParser(sFeedItems, this, getActivity(), isPodcast).execute(url + 1);
    }
}
