package com.apps.wow.droider.Article

import com.apps.wow.droider.Model.Post
import com.arellomobile.mvp.MvpView
import java.util.*

/**
 * Created by Jackson on 14/05/2017.
 */

interface ArticleView : MvpView {
    fun changeLoadingVisibility(isVisible: Boolean)

    fun loadArticle(articleHtml: String)

    fun setupSimilar(similar: ArrayList<Post>)

    fun hideSimilar()

    fun showErrorLoading(errorHtml: String)

    fun setupNecessaryFields(post: Post)
}
