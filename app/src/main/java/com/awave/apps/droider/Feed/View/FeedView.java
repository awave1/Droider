package com.awave.apps.droider.Feed.View;

import com.awave.apps.droider.Model.FeedModel;

import java.util.ArrayList;

public interface FeedView {
    void onLoadingFeed();
    void onLoadComplete(ArrayList<FeedModel> list);
    void onLoadFailed();
}