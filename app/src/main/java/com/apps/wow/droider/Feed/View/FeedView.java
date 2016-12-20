package com.apps.wow.droider.Feed.View;

import com.apps.wow.droider.Model.NewFeedModel;

public interface FeedView {
    void onLoadingFeed();
    void onLoadComplete(NewFeedModel model);
    void onLoadFailed();
}