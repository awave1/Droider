package com.apps.wow.droider.Feed.View;

import com.apps.wow.droider.Model.FeedModel;
import com.arellomobile.mvp.MvpView;

public interface FeedView extends MvpView {
    void onLoadingFeed();

    void onLoadCompleted(FeedModel model, boolean clear);

    void onLoadCompleted(FeedModel model);

    void onLoadFailed();
}