package com.apps.wow.droider.Feed.View;

import com.apps.wow.droider.Model.FeedModel;

public interface FeedView {
    void onLoadingFeed();

    void onLoadCompleted(FeedModel model, boolean clear);

    void onLoadCompleted(FeedModel model);

    void onLoadFailed();
}