package com.apps.wow.droider.Feed.Presenter;

import com.apps.wow.droider.Feed.OnTaskCompleted;
import com.apps.wow.droider.Feed.View.FeedView;
import com.apps.wow.droider.Model.FeedModel;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.api.DroiderApi;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class FeedPresenter extends MvpPresenter<FeedView> {

    private static final String TAG = "FeedPresenter";
    OnTaskCompleted taskCompleted;
    DroiderApi api;

    public void setOnTaskCompleted(OnTaskCompleted onTaskCompleted) {
        this.taskCompleted = onTaskCompleted;
    }

    public void loadData(String category, String slug, int count, int offset, final boolean clear) {
        getViewState().onLoadingFeed();
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
                        getViewState().onLoadFailed();
                    }

                    @Override
                    public void onNext(Response<FeedModel> response) {
                        if (response.isSuccessful()) {
                            getViewState().onLoadCompleted(response.body(), clear);
                        }
                    }
                });
    }

    public void loadPopular() {
        getViewState().onLoadingFeed();
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
//                        getViewState().onLoadFailed();
                    }

                    @Override
                    public void onNext(Response<FeedModel> response) {
                        if (response.isSuccessful())
                            getViewState().onLoadCompleted(response.body());
                    }
                });
    }
}
