package com.apps.wow.droider.Feed.Interactors;

import com.apps.wow.droider.Model.FeedModel;

import java.util.ArrayList;


public interface OnLoadingInteractorFinishedListener {
    void OnCompleted(ArrayList<FeedModel> list);
    void onNetworkFailure();
}
