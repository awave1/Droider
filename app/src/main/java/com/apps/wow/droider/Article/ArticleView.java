package com.apps.wow.droider.Article;

import com.apps.wow.droider.Model.Post;
import com.arellomobile.mvp.MvpView;

import java.util.ArrayList;

/**
 * Created by Jackson on 14/05/2017.
 */

public interface ArticleView extends MvpView {
    void changeLoadingVisibility(boolean isVisible);

    void loadArticle(String articleHtml);

    void setupSimilar(ArrayList<Post> similar);

    void hideSimilar();

    void showErrorLoading(String errorHtml);

    void setupNecessaryFields(Post post);
}
