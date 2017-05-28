package com.apps.wow.droider.Article;

import android.util.Log;

import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.AppContext;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Jackson on 14/05/2017.
 */

@InjectViewState
public class ArticlePresenter extends MvpPresenter<ArticleView> {

    public static final String TAG = "ArticlePresenter";
    private String mUrl;

    private ArticleModel mArticleModel;

    public ArticlePresenter() {
    }

    public void provideData(String mUrl, ArticleModel model) {
        this.mUrl = mUrl;
        mArticleModel = model;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Log.d("TAG", "onFirstViewAttach: ");
    }

    public void parseArticle() {
        getViewState().changeLoadingVisibility(true);
        mArticleModel.parseArticle(mUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getViewState().changeLoadingVisibility(false);
                        getViewState().showErrorLoading(AppContext.getContext().getString(R.string.errorHtml));
                    }

                    @Override
                    public void onNext(String s) {
                        getViewState().changeLoadingVisibility(false);
                        getViewState().loadArticle(s);
                        loadSimilar();
                    }
                });
    }

    public void loadSimilar() {
        if (mArticleModel.getSimilar() != null) {
            getViewState().setupSimilar(mArticleModel.getSimilar());
        } else {
            getViewState().hideSimilar();
        }
    }

    public void getPostDataForOutsideEvent() {
        mArticleModel.getPostDataForOutsideIntent(mUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(post -> getViewState().setupNecessaryFields(post),
                        e -> Log.e(TAG, "getPostDataForOutsideEvent: ", e));
    }
}

