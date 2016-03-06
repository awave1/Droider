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

    public static SwipeRefreshLayout sSwipeRefreshLayout;
    public static LinearLayoutManager sLinearLayoutManager;
    public static StaggeredGridLayoutManager sStaggeredGridLayoutManager;
    private static ArrayList<FeedItem> sFeedItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private AdapterMain adapter;
    private static  String lastTitle;

    public static Feed instance(String feedUrl) {
        Feed feed = new Feed();
        Bundle bundle = new Bundle();
        bundle.putString(Helper.EXTRA_ARTICLE_URL, feedUrl);
        feed.setArguments(bundle);
        return feed;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.feed_fragment, container, false);

        Log.d(TAG, "onCreateView: orientation = " + getActivity().getResources().getConfiguration().orientation);
        Log.d(TAG, "onCreateView: getArguments().getString(EXTRA_ARTICLE_URL) = " + getArguments().getString(Helper.EXTRA_ARTICLE_URL));

        sSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.feed_swipe_refresh);
        sSwipeRefreshLayout.setOnRefreshListener(this);
        sSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark);
        sSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.feed_recycler_view);
        adapter = new AdapterMain(getActivity(), sFeedItems);
        mRecyclerView.setHasFixedSize(true);

        initLayoutManager();
        return v;
    }

    @Override
    public void onStop() {
//        lastTitle = sFeedItems.get(1).getTitle();
//        Log.d(TAG, "onCreateView: lastTitle " + lastTitle);
        //а надо ли запариваться с обновлением через рсс?? вроде никто не просил пока, да и это ресурсо затратно думаю
        super.onStop();
    }

    @Override
    public void onRefresh() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (Helper.isOnline(getActivity())) {
                    getFeeds(getArguments().getString(Helper.EXTRA_ARTICLE_URL));
                } else {
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
                sStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(sStaggeredGridLayoutManager);

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
                sLinearLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(sLinearLayoutManager);
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
            sLinearLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(sLinearLayoutManager);

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
            sStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(sStaggeredGridLayoutManager);

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

    public void loadMore(String url) {
        new FeedParser(sFeedItems, sSwipeRefreshLayout, getActivity(), this).execute(url);
    }

    private void getFeeds(String url) {
        sFeedItems.clear();
        new FeedParser(sFeedItems, sSwipeRefreshLayout, getActivity(), this).execute(url + 1);
    }
}
