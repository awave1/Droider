package com.awave.apps.droider.Feed.Interactors;

import com.awave.apps.droider.Model.FeedModel;

import java.util.ArrayList;


public interface OnLoadingInteractorFinishedListener {
    void OnCompleted(ArrayList<FeedModel> list);
    void onNetworkFailure();
}
