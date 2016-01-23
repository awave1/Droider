package com.awave.apps.droider.Elements.MainScreen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.awave.apps.droider.Utils.Utils.Feed.FeedParser;
import com.awave.apps.droider.Utils.Utils.Feed.OnTaskCompleted;
import com.awave.apps.droider.Utils.Utils.FeedItem;

import java.util.ArrayList;


public class Games extends Fragment implements OnTaskCompleted, SwipeRefreshLayout.OnRefreshListener {
    private static final String FEED = "http://droider.ru/category/apps_and_games/games/page/";
    private static final String TAG = "Games";

    private SwipeRefreshLayout mSwipeRefreshLayout;
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
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mSwipeRefreshLayout = new SwipeRefreshLayout(getActivity());
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mRecyclerView = new RecyclerView(getActivity());
        mRecyclerView.setHasFixedSize(true);

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        adapter = new AdapterMain(getActivity(),items, metrics);

        this.initLayoutManager();

        return mSwipeRefreshLayout;
    }

    @Override
    public void onTaskComplete() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
        this.getFeeds(FEED);
    }

    private RecyclerView.OnScrollListener onScrollPortrait = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            visibleItemCount =(byte) recyclerView.getChildCount();
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
                loadMore(FEED + nextPage);
                loading = true;
            }
        }
    };

    private RecyclerView.OnScrollListener onScrollLandscape = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            visibleItemCount =(byte) recyclerView.getChildCount();
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
                loadMore(FEED + nextPage);
                loading = true;
            }
        }
    };

    private void loadMore(String url) {
        new FeedParser(this,items,getActivity()).execute(url);
    }

    private void getFeeds(String url) {
        items.clear();
        new FeedParser(this, items, getActivity()).execute(url + 1);
    }

    private void initLayoutManager(){
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.setAdapter(adapter);
            mRecyclerView.addOnScrollListener(onScrollPortrait);
            mSwipeRefreshLayout.addView(mRecyclerView, ViewGroup.LayoutParams.MATCH_PARENT);

            this.getFeeds(FEED);
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(mGridLayoutManager);

            mRecyclerView.setAdapter(adapter);
            mRecyclerView.addOnScrollListener(onScrollLandscape);
            mSwipeRefreshLayout.addView(mRecyclerView, ViewGroup.LayoutParams.MATCH_PARENT);

            this.getFeeds(FEED);
        }
    }
}
