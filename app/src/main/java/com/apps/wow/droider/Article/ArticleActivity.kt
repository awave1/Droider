package com.apps.wow.droider.Article

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import com.apps.wow.droider.DroiderBaseActivity
import com.apps.wow.droider.Feed.FeedActivity
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.AppContext.Companion.context
import com.apps.wow.droider.Utils.Const
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class ArticleActivity : DroiderBaseActivity() {

    private lateinit var castId: String
    private lateinit var pd: ProgressDialog

    private lateinit var mUrl: String


    val WEB_TABLE_COLOR_LIGHT: String = "#f5f5f5"
    val WEB_TABLE_HEADER_COLOR_LIGHT: String = "#eeeeee"
    val WEB_TABLE_COLOR_DARK: String = "#212121"
    val WEB_TABLE_HEADER_COLOR_DARK: String = "#616161"

    override fun onCreate(savedInstanceState: Bundle?) {
        themeSetup()
        // Fix for Circular Reveal animation on Pre-Lollipop
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        pd = ProgressDialog(this)
        pd.setMessage("Подождите пожалуйста, наша нейронка парсит страницу")
        pd.show()

        mUrl = when {
            intent.extras != null && intent.extras!!.getString(Const.EXTRA_ARTICLE_URL) != null
            -> intent.extras!!.getString(Const.EXTRA_ARTICLE_URL)

            intent.data != null -> intent.data.toString()

            else -> PreferenceManager.getDefaultSharedPreferences(context).getString(Const.CAST_URL, "")
        }

        if (mUrl.isBlank()) {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        } else {
            val model = ArticleModel(ArticleFragment.webViewTextColor,
                    ArticleFragment.webViewLinkColor,
                    ArticleFragment.webViewTableColor,
                    ArticleFragment.webViewTableHeaderColor)

            model.parseArticle(mUrl).observeOn(AndroidSchedulers.mainThread()).subscribe({
                when {
                    model.castID.size == 1 -> replaceFragment(
                            PlayerFragment.newInstance(it, model.castID[0].toString(), model.castTitle))
                    model.castID.size > 1 -> podcastAlertDialogChooser(model.castID.toTypedArray(), it, model.castTitle)
                    else -> replaceFragment(ArticleFragment.newInstance())
                }
            }, { _ -> finish() })
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out)
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss()
        if (pd.isShowing)
            pd.dismiss()
    }

    private fun podcastAlertDialogChooser(list: Array<CharSequence>, html: String?, title: String?) {
        if (pd.isShowing)
            pd.dismiss()
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setItems(list, { _, i ->
            castId = list[i].toString()
            replaceFragment(PlayerFragment.newInstance(html, list[i].toString(), title))
        }).create().show()
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
            ArticleFragment.webViewTableColor = WEB_TABLE_COLOR_LIGHT
            ArticleFragment.webViewTableHeaderColor = WEB_TABLE_HEADER_COLOR_LIGHT
        } else {
            ArticleFragment.webViewTableColor = WEB_TABLE_COLOR_DARK
            ArticleFragment.webViewTableHeaderColor = WEB_TABLE_HEADER_COLOR_DARK
        }

        Timber.d("themeSetup: bg color: " + ArticleFragment.webViewBackgroundColor)
        Timber.d("themeSetup: webViewTextColor color: " + ArticleFragment.webViewTextColor)
        Timber.d("themeSetup: webViewLinkColor color: " + ArticleFragment.webViewLinkColor)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }
}