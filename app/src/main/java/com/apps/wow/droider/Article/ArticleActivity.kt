package com.apps.wow.droider.Article

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import com.apps.wow.droider.DroiderBaseActivity
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Const
import rx.android.schedulers.AndroidSchedulers

class ArticleActivity : DroiderBaseActivity() {

    private val TAG = "ArticleActivity"

    private lateinit var castId: String

    private lateinit var pd: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        themeSetup()
        // Fix for Circular Reveal animation on Pre-Lollipop
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        pd = ProgressDialog(this)
        pd.setMessage("Подождите пожалуйста, наша нейронка парсит страницу")
        pd.show()

        val mUrl = if (intent.extras != null && intent.extras!!.getString(Const.EXTRA_ARTICLE_URL) != null)
            intent.extras!!.getString(Const.EXTRA_ARTICLE_URL)
        else
            intent.data.toString()

        val model = ArticleModel(ArticleFragment.webViewTextColor, ArticleFragment.webViewLinkColor, ArticleFragment.webViewTableColor, ArticleFragment.webViewTableHeaderColor)
        model.parseArticle(mUrl).observeOn(AndroidSchedulers.mainThread()).subscribe({
            when {
                model.castID.size == 1 -> replaceFragment(PlayerFragment.newInstance(it,
                        model.castID[0].toString(), model.castTitle))
                model.castID.size > 1 -> podcastsAlertDialogChooser(model.castID.toTypedArray(), it, model.castTitle)
                else -> replaceFragment(ArticleFragment.newInstance())
            }
        })
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

    private fun podcastsAlertDialogChooser(list: Array<CharSequence>, html: String?, title: String?) {
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
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }
}