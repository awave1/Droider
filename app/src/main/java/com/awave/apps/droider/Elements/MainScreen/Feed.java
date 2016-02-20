package com.awave.apps.droider.Elements.MainScreen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awave.apps.droider.Main.AdapterMain;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Feed.FeedItem;
import com.awave.apps.droider.Utils.Feed.FeedOrientation;
import com.awave.apps.droider.Utils.Feed.FeedParser;
import com.awave.apps.droider.Utils.Feed.OnTaskCompleted;
import com.awave.apps.droider.Utils.Helper;

import java.util.ArrayList;

/**
 * Created by awave on 2016-01-23.
 */
public class Feed extends android.app.Fragment implements OnTaskCompleted, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Feed";

    public static SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    public static LinearLayoutManager mLayoutManager;
    public static GridLayoutManager mGridLayoutManager;
    private AdapterMain adapter;
    private static ArrayList<FeedItem> items = new ArrayList<>();

    public static boolean isRefreshing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.feed_fragment, container, false);

        Log.d(TAG, "onCreateView: orientation = " + getActivity().getResources().getConfiguration().orientation);
        Log.d(TAG, "onCreateView: getArguments().getString(EXTRA_ARTICLE_URL) = " + getArguments().getString(Helper.EXTRA_ARTICLE_URL));

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.feed_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.feed_recycler_view);
        adapter = new AdapterMain(getActivity(), items);
        mRecyclerView.setHasFixedSize(true);

        initLayoutManager();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: orientation = " + getActivity().getResources().getConfiguration().orientation);
    }

    @Override
    public void onRefresh() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (Helper.isOnline(getActivity())) {
                    getFeeds(getArguments().getString(Helper.EXTRA_ARTICLE_URL));
                }
                else {
                    Helper.initInternetConnectionDialog(getActivity());
                }
            }
        });
    }

    @Override
    public void onTaskCompleted() {
        adapter.notifyDataSetChanged();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                adapter.notifyDataSetChanged();
                mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
                mRecyclerView.setLayoutManager(mGridLayoutManager);

                mRecyclerView.setAdapter(adapter);
                mRecyclerView.addOnScrollListener(new FeedOrientation(getActivity()) {
                    @Override
                    public void loadNextPage() {
                        loadMore(getArguments().getString(Helper.EXTRA_ARTICLE_URL) + FeedOrientation.nextPage_landscape);
                        onTaskCompleted();
                    }
                });

                getFeeds(getArguments().getString(Helper.EXTRA_ARTICLE_URL));
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                adapter.notifyDataSetChanged();
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.addOnScrollListener(new FeedOrientation(getActivity()) {
                    @Override
                    public void loadNextPage() {
                        loadMore(getArguments().getString(Helper.EXTRA_ARTICLE_URL) + FeedOrientation.nextPage_portrait);
                        onTaskCompleted();
                    }
                });

                getFeeds(getArguments().getString(Helper.EXTRA_ARTICLE_URL));
                break;
        }
    }

    private void initLayoutManager() {

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.setAdapter(adapter);
            mRecyclerView.addOnScrollListener(new FeedOrientation(getActivity()) {
                @Override
                public void loadNextPage() {
                    loadMore(getArguments().getString(Helper.EXTRA_ARTICLE_URL) + FeedOrientation.nextPage_portrait);
                    adapter.notifyDataSetChanged();
                }
            });
            getFeeds(getArguments().getString(Helper.EXTRA_ARTICLE_URL));
        }

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(mGridLayoutManager);

            mRecyclerView.setAdapter(adapter);
            mRecyclerView.addOnScrollListener(new FeedOrientation(getActivity()) {
                @Override
                public void loadNextPage() {
                    loadMore(getArguments().getString(Helper.EXTRA_ARTICLE_URL) + FeedOrientation.nextPage_landscape);
                    onTaskCompleted();
                }
            });
            getFeeds(getArguments().getString(Helper.EXTRA_ARTICLE_URL));
        }
    }

    public static Feed instance(String feedUrl){
        Feed feed = new Feed();
        Bundle bundle = new Bundle();
        bundle.putString(Helper.EXTRA_ARTICLE_URL, feedUrl);
        feed.setArguments(bundle);
        return feed;
    }

    public  void loadMore(String url) {
        new FeedParser(items, mSwipeRefreshLayout, getActivity(), this).execute(url);
    }

    private void getFeeds(String url) {
        items.clear();
        new FeedParser(items, mSwipeRefreshLayout, getActivity(), this).execute(url + 1);
    }
}
