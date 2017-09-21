package com.apps.wow.droider.Article

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Const
import com.arellomobile.mvp.MvpAppCompatFragment
import kotlinx.android.synthetic.main.article_card.*

/**
 * Created by Jackson on 20/09/2017.
 */
class ArticleForPlayerFragment : MvpAppCompatFragment() {

    private val TAG: String? = ArticleForPlayerFragment::class.java.name

    private var extras: Bundle? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.article_for_player, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        extras = activity.intent.extras ?: arguments

        articleHeader.text = extras?.getString(Const.EXTRA_ARTICLE_TITLE)
        articleShortDescription.text = extras?.getString(Const.EXTRA_SHORT_DESCRIPTION)

        setupArticleWebView(article)
        loadArticle(arguments.getString(Const.POST_HTML))
        articleProgressBar.visibility = View.GONE
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupArticleWebView(w: WebView) {
        Log.d(TAG, "setupArticleWebView: ")
        w.setBackgroundColor(ArticleFragment.webViewBackgroundColor)
        val settings = w.settings
        w.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: url: " + url)
                view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                return true
            }
        }

        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        settings.setAppCacheEnabled(true)
        settings.saveFormData = true
    }

    private fun loadArticle(articleHtml: String) {
        article.loadDataWithBaseURL("file:///android_asset/", articleHtml, "text/html", "UTF-8", "")
    }

    companion object {
        fun newInstance(html: String): ArticleForPlayerFragment {
            val fragment = ArticleForPlayerFragment()
            fragment.retainInstance = true
            val b = Bundle()
            b.putString(Const.POST_HTML, html)
            fragment.arguments = b
            return fragment
        }
    }
}