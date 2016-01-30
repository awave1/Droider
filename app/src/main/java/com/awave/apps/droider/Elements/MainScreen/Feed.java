package com.awave.apps.droider.Elements.MainScreen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awave.apps.droider.Main.AdapterMain;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Utils.Feed.FeedParser;
import com.awave.apps.droider.Utils.Utils.Feed.OnTaskCompleted;
import com.awave.apps.droider.Utils.Utils.FeedItem;
import com.awave.apps.droider.Utils.Utils.Helper;

import java.util.ArrayList;

/**
 * Created by awave on 2016-01-23.
 */
public class Feed extends android.app.Fragment implements OnTaskCompleted, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Feed";

    public static SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private AdapterMain adapter;
    private DisplayMetrics metrics;
    private ArrayList<FeedItem> items = new ArrayList<>();

    private int previousTotal = 0;
    private boolean loading = true;
    private byte visibleThreshold = 5;
    byte firstVisibleItem, visibleItemCount, totalItemCount, nextPage = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.feed_fragment, container, false);
        Log.d(TAG, "onCreateView: orientation = " + getActivity().getResources().getConfiguration().orientation);
        Log.d(TAG, "onCreateView: getArguments().getString(EXTRA_FEED_URL) = " + getArguments().getString(Helper.EXTRA_FEED_URL));

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.feed_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.feed_recycler_view);
        adapter = new AdapterMain(getActivity(), items, metrics);
        this.initLayoutManager();
        mRecyclerView.setHasFixedSize(true);

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

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
                    mSwipeRefreshLayout.setRefreshing(true);
                    getFeeds(getArguments().getString(Helper.EXTRA_FEED_URL));
                }
                else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Helper.checkInternerConnection(getActivity());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onTaskComplete() {
        adapter.notifyDataSetChanged();
    }

    private RecyclerView.OnScrollListener onScrollPortrait = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            visibleItemCount = (byte) recyclerView.getChildCount();
            totalItemCount = (byte) mLayoutManager.getItemCount();
            firstVisibleItem = (byte) mLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                Log.i(TAG, "end called");
                nextPage++;
                // Do something
                loadMore(getArguments().getString(Helper.EXTRA_FEED_URL) + nextPage);
                loading = true;
            }
        }
    };

    private RecyclerView.OnScrollListener onScrollLandscape = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            visibleItemCount = (byte) recyclerView.getChildCount();
            totalItemCount = (byte) mGridLayoutManager.getItemCount();
            firstVisibleItem = (byte) mGridLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                Log.i(TAG, "end called");
                nextPage++;
                // Do something
                loadMore(getArguments().getString(Helper.EXTRA_FEED_URL) + nextPage);
                loading = true;
            }
        }
    };

    private void initLayoutManager() {

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.setAdapter(adapter);
            mRecyclerView.addOnScrollListener(onScrollPortrait);

            this.getFeeds(getArguments().getString(Helper.EXTRA_FEED_URL));
        }
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(mGridLayoutManager);

            mRecyclerView.setAdapter(adapter);
            mRecyclerView.addOnScrollListener(onScrollLandscape);

            this.getFeeds(getArguments().getString(Helper.EXTRA_FEED_URL));
        }
    }

    public static Feed instance(String feedUrl){
        Feed feed = new Feed();
        Bundle bundle = new Bundle();
        bundle.putString(Helper.EXTRA_FEED_URL, feedUrl);
        feed.setArguments(bundle);
        return feed;
    }

    private void loadMore(String url) {
        new FeedParser(this, items, getActivity()).execute(url);
    }

    private void getFeeds(String url) {
        items.clear();
        new FeedParser(this, items, getActivity()).execute(url + 1);
    }
}
