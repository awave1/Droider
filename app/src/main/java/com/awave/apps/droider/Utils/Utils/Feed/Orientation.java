package com.awave.apps.droider.Utils.Utils.Feed;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.awave.apps.droider.Elements.MainScreen.Feed;

public abstract class Orientation extends RecyclerView.OnScrollListener {

    public Orientation(Activity activity)
    {
        this.activity = activity;
    }

    Activity activity;

    //for Portrait Orientation
    private int previousTotalPort = 0;
    private boolean loadingPort = true;
    private byte visibleThresholdPort = 5;
    byte firstVisibleItemPort, visibleItemCountPort, totalItemCountPort;
    public static short nextPagePort = 1;

    //for Landscape Orientation
    private int previousTotalLand = 0;
    private boolean loadingLand = true;
    private byte visibleThresholdLand = 3;
    byte firstVisibleItemLand, visibleItemCountLand, totalItemCountLand;
    public static short nextPageLand = 1;

    public abstract void loadingMore();


    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == 0) {
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                PortraitOrientation(activity, recyclerView, Feed.mLayoutManager);
            else if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                LandscapeOrientation(activity, recyclerView, Feed.mGridLayoutManager);
        }
    }

    public void PortraitOrientation(Activity activity, RecyclerView recyclerView, LinearLayoutManager mLayoutManager)
    {
        this.activity = activity;
        visibleItemCountPort = (byte) recyclerView.getChildCount();
        totalItemCountPort = (byte) mLayoutManager.getItemCount();
        Log.d("TOTALITEMCOUNTPortrait", totalItemCountPort + "");
        firstVisibleItemPort = (byte) mLayoutManager.findFirstVisibleItemPosition();
        Log.d("firstVisibleItemPort", firstVisibleItemPort+"");
        if (loadingPort) {
            if (totalItemCountPort > previousTotalPort) {
                loadingPort = false;
                previousTotalPort = totalItemCountPort;
            }
        }
        if (!loadingPort && (totalItemCountPort - visibleItemCountPort)
                <= (firstVisibleItemPort + visibleThresholdPort)) {
            // End has been reached

            nextPagePort++;
            loadingMore();
            loadingPort = true;
        }
    }

    public void LandscapeOrientation(Activity activity, RecyclerView recyclerView, GridLayoutManager mGridLayoutManager)
    {
        this.activity = activity;
        visibleItemCountLand = (byte) recyclerView.getChildCount();
        totalItemCountLand = (byte) mGridLayoutManager.getItemCount();
        Log.d("TOTALITEMCOUNTLandscape", totalItemCountLand+"");

        firstVisibleItemLand = (byte) mGridLayoutManager.findFirstVisibleItemPosition();
        Log.d("firstVisibleItemLand", firstVisibleItemLand+"");
        if (loadingLand) {
            if (totalItemCountLand > previousTotalLand) {
                loadingLand = false;
                previousTotalLand = totalItemCountLand;
            }
        }
        if (!loadingLand && (totalItemCountLand - visibleItemCountLand)
                <= (firstVisibleItemLand + visibleThresholdLand)) {
            // End has been reached

            nextPageLand++;
            loadingMore();
            loadingLand = true;
        }
    }
}
