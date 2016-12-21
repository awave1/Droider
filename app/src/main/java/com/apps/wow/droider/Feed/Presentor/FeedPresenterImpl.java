package com.apps.wow.droider.Feed.Presentor;

import android.util.Log;

import com.apps.wow.droider.DroiderBaseActivity;
import com.apps.wow.droider.Feed.OnTaskCompleted;
import com.apps.wow.droider.Feed.View.FeedFragment;
import com.apps.wow.droider.Feed.View.FeedView;
import com.apps.wow.droider.Model.FeedModel;
import com.apps.wow.droider.Model.NewFeedModel;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.api.DroiderApi;
import java.util.ArrayList;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FeedPresenterImpl implements FeedPresenter {

    private static final String TAG = "FeedPresenterImpl";
  FeedView view;
  FeedFragment feedFragment;
  OnTaskCompleted taskCompleted;
  boolean isPodCast;
  ArrayList<FeedModel> list = new ArrayList<>();
  DroiderApi api;

  public void attachView(FeedFragment fragment, FeedView view, OnTaskCompleted onTaskCompleted) {
    this.view = view;
    this.taskCompleted = onTaskCompleted;
    this.feedFragment = fragment;
  }

  @Override
  public void loadData(String category, String slug, int count, int offset) {
    api = Utils.createRxService(DroiderApi.class, Utils.HOME_URL, true);

    api.getFeed(category, slug, count, offset)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Response<NewFeedModel>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            ((DroiderBaseActivity) feedFragment.getActivity()).initInternetConnectionDialog(
                feedFragment.getActivity());
          }

          @Override public void onNext(Response<NewFeedModel> response) {
            if (response.isSuccessful()) {
              view.onLoadComplete(response.body());
            }
          }
        });
  }

  @Override public void loadMore(String url) {

  }

  @Override public void getDataWithClearing(String url) {

  }
}
