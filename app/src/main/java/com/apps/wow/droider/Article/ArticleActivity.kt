package com.apps.wow.droider.Article

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import com.apps.wow.droider.DroiderBaseActivity
import com.apps.wow.droider.Player.PlayerFragment
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Const
import rx.android.schedulers.AndroidSchedulers

class ArticleActivity : DroiderBaseActivity() {

    private val TAG = "ArticleActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        themeSetup()

        // Fix for Circular Reveal animation on Pre-Lollipop
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container)

        if (supportFragmentManager.findFragmentById(R.id.container) == null) {

            val mUrl = if (intent.extras != null && intent.extras!!.getString(Const.EXTRA_ARTICLE_URL) != null)
                intent.extras!!.getString(Const.EXTRA_ARTICLE_URL)
            else
                intent.data.toString()

            val model = ArticleModel(null, null, null, null)

            model.parseArticle(mUrl).observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (model.castID != null) {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container, PlayerFragment.newInstance(model.castID!!, model.castTitle))
                            .commitAllowingStateLoss()
                } else {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.container, ArticleFragment.newInstance())
                            .commitAllowingStateLoss()
                }
            })

        }
    }

    override fun themeSetup() {
        super.themeSetup()
        ArticleFragment.currentNightMode = resources.configuration.uiMode
        ArticleFragment.webViewBackgroundColor = getThemeAttribute(android.R.attr.colorForegroundInverse,
                DroiderBaseActivity.Companion.activeTheme)
        ArticleFragment.webViewTextColor = "#" + Integer
                .toHexString(getThemeAttribute(android.R.attr.textColorPrimary, DroiderBaseActivity.Companion.activeTheme))
                .substring(2)
        ArticleFragment.webViewLinkColor = "#" + Integer
                .toHexString(getThemeAttribute(R.attr.colorPrimary, DroiderBaseActivity.Companion.activeTheme)).substring(2)

        if (DroiderBaseActivity.Companion.activeTheme == R.style.RedTheme) {
            ArticleFragment.webViewTableColor = "#F5F5F5"
            ArticleFragment.webViewTableHeaderColor = "#EEEEEE"
        } else {
            ArticleFragment.webViewTableHeaderColor = "#212121"
            ArticleFragment.webViewTableColor = "#616161"
        }

        Log.d(TAG, "themeSetup: bg color: " + ArticleFragment.webViewBackgroundColor)
        Log.d(TAG, "themeSetup: webViewTextColor color: " + ArticleFragment.webViewTextColor)
        Log.d(TAG, "themeSetup: webViewLinkColor color: " + ArticleFragment.webViewLinkColor)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}