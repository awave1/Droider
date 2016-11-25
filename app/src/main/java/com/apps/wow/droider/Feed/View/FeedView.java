package com.apps.wow.droider.Feed.View;

import com.apps.wow.droider.Model.FeedModel;

import java.util.ArrayList;

public interface FeedView {
    void onLoadingFeed();
    void onLoadComplete(ArrayList<FeedModel> list);
    void onLoadFailed();
}