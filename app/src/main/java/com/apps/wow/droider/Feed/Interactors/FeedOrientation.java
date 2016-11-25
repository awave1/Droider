package com.apps.wow.droider.Feed.Interactors;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.apps.wow.droider.Feed.View.FeedFragment;


public abstract class FeedOrientation extends RecyclerView.OnScrollListener {
    private static final String TAG = "FeedOrientation";
    private SwipeRefreshLayout swipeRefresher;
    public static short nextPage_portrait = 1;
    public static short nextPage_landscape = 1;
    private Activity mActivity;
    // Portrait
    private int previousTotal_portrait = 0;
    private byte visibleThreshold_portrait = 4;
    private byte firstVisibleItem_portrait;
    private byte visibleItemCount_portrait;
    private byte totalItemCount_portrait;
    private boolean isLoading_portrait = true;
    // Landscape
    private int previousTotal_landscape = 0;
    private byte visibleThreshold_landscape = 3; // 3
    private int[] firstVisibleItem_landscape;
    private byte visibleItemCount_landscape;
    private byte totalItemCount_landscape;
    private boolean isLoading_landscape;

    public FeedOrientation(Activity a, SwipeRefreshLayout swipeRefresher) {
        this.swipeRefresher = swipeRefresher;
        this.mActivity = a;
    }

    public abstract void loadNextPage();

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == 0) {
            if ((mActivity.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                doubleColsLoading(mActivity, recyclerView, FeedFragment.sStaggeredGridLayoutManager);
            } else {
                singleColsLoading(mActivity, recyclerView, FeedFragment.sLinearLayoutManager);
            }
        }
    }

    private void singleColsLoading(Activity activity, RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        this.mActivity = activity;
        visibleItemCount_portrait = (byte) recyclerView.getChildCount();
        totalItemCount_portrait = (byte) layoutManager.getItemCount();

        Log.d(TAG, "singleColsLoading: totalItemCount_portrait = " + totalItemCount_portrait);
        firstVisibleItem_portrait = (byte) layoutManager.findFirstVisibleItemPosition();


        Log.d(TAG, "singleColsLoading: firstVisibleItem_portrait = " + firstVisibleItem_portrait);

        if (isLoading_portrait && totalItemCount_portrait > previousTotal_portrait) {
            isLoading_portrait = false;
            previousTotal_portrait = totalItemCount_portrait;
        }

        if (!isLoading_portrait &&
                (totalItemCount_portrait - visibleItemCount_portrait) <= (firstVisibleItem_portrait + visibleThreshold_portrait)) {
            Log.d(TAG, "singleColsLoading: end has been reached, loading next page");
            nextPage_portrait++;
            loadNextPage();
            isLoading_portrait = true;
        }

        if (firstVisibleItem_portrait + 2 == totalItemCount_portrait) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    swipeRefresher.setRefreshing(true);
                }
            });
        }
    }

    private void doubleColsLoading(Activity activity, RecyclerView recyclerView, StaggeredGridLayoutManager staggeredGridLayoutManager) {
        this.mActivity = activity;
        visibleItemCount_landscape = (byte) recyclerView.getChildCount();
        totalItemCount_landscape = (byte) staggeredGridLayoutManager.getItemCount();

        Log.d(TAG, "doubleColsLoading: totalItemCount_landscape = " + totalItemCount_landscape);

        firstVisibleItem_landscape = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItem_landscape);

        Log.d(TAG, "singleColsLoading: firstVisibleItem_portrait (length) = " + firstVisibleItem_landscape.length);
        Log.d(TAG, "doubleColsLoading: firstVisibleItem_landscape [0]/[1] = \n"
                + firstVisibleItem_landscape[0] + "/" + firstVisibleItem_landscape[1]);

        if (firstVisibleItem_landscape != null && firstVisibleItem_landscape.length > 0) {
            previousTotal_landscape = firstVisibleItem_landscape[0];
        }

        if (isLoading_landscape && totalItemCount_landscape > previousTotal_landscape) {
            isLoading_landscape = false;
            previousTotal_landscape = firstVisibleItem_landscape[0];
        }

        if (!isLoading_landscape &&
                (totalItemCount_landscape - visibleItemCount_landscape) <= (previousTotal_landscape + visibleThreshold_landscape)) {
            nextPage_landscape++;
            loadNextPage();
            isLoading_landscape = true;
        }

        if (firstVisibleItem_landscape[0] + 2 >= totalItemCount_landscape || firstVisibleItem_landscape[1] + 2 >= totalItemCount_landscape) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    swipeRefresher.setRefreshing(true);
                }
            });
        }
    }
}
