package com.apps.wow.droider.Feed.Presenter;

import com.apps.wow.droider.Feed.OnTaskCompleted;
import com.apps.wow.droider.Feed.View.FeedView;
import com.apps.wow.droider.Model.FeedModel;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.api.DroiderApi;

import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FeedPresenterImpl implements FeedPresenter {

    private static final String TAG = "FeedPresenterImpl";
    FeedView mView;
    OnTaskCompleted taskCompleted;
    DroiderApi api;

    public void attachView(FeedView view, OnTaskCompleted onTaskCompleted) {
        this.mView = view;
        this.taskCompleted = onTaskCompleted;
    }

    @Override
    public void loadData(String category, String slug, int count, int offset, final boolean clear) {
        mView.onLoadingFeed();
        api = Utils.createRxService(DroiderApi.class, Utils.HOME_URL, true);

        api.getFeed(category, slug, count, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<FeedModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onLoadFailed();
                    }

                    @Override
                    public void onNext(Response<FeedModel> response) {
                        if (response.isSuccessful()) {
                            mView.onLoadCompleted(response.body(), clear);
                        }
                    }
                });
    }

    @Override
    public void loadPopular() {
        mView.onLoadingFeed();
        api = Utils.createRxService(DroiderApi.class, Utils.HOME_URL, true);

        api.getPopular()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<FeedModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
//                        mView.onLoadFailed();
                    }

                    @Override
                    public void onNext(Response<FeedModel> response) {
                        if (response.isSuccessful())
                            mView.onLoadCompleted(response.body());
                    }
                });
    }
}
