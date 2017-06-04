package com.apps.wow.droider

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.AttrRes
import android.support.annotation.StyleRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDelegate
import com.apps.wow.droider.Utils.Utils
import com.arellomobile.mvp.MvpAppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*



open class DroiderBaseActivity : MvpAppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtain the FirebaseAnalytics newInstance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    protected open fun themeSetup() {
        themesHashMap.put(getString(com.apps.wow.droider.R.string.pref_theme_entry_light),
                com.apps.wow.droider.R.style.RedTheme)
        themesHashMap.put(getString(com.apps.wow.droider.R.string.pref_theme_entry_dark),
                com.apps.wow.droider.R.style.RedThemeDark)
        themesHashMap.put(getString(com.apps.wow.droider.R.string.pref_theme_entry_adaptive),
                com.apps.wow.droider.R.style.AdaptiveTheme)

        val themeName = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(com.apps.wow.droider.R.string.pref_theme_key),
                        getString(com.apps.wow.droider.R.string.pref_theme_entry_light))

        try {
            activeTheme = themesHashMap[themeName] as Int
            setTheme(activeTheme)
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
            setTheme(com.apps.wow.droider.R.style.AdaptiveTheme)
        }

    }

    protected fun getThemeAttribute(@AttrRes attributeInt: Int, @StyleRes theme: Int): Int {
        // The attributes you want retrieved
        val attrs = intArrayOf(attributeInt)
        // Parse MyCustomStyle, using Context.obtainStyledAttributes()
        val attributesTypedArray = obtainStyledAttributes(theme, attrs)
        // Fetching the colors defined in your style
        val attributeValue = attributesTypedArray.getColor(0, -1)
        // OH, and don't forget to recycle the TypedArray
        attributesTypedArray.recycle()
        return attributeValue
    }

    fun initInternetConnectionDialog(context: Context) {

        AlertDialog.Builder(context).setTitle("Соединение нестабильно или прервано")
                .setMessage("Проверьте своё соединение с интернетом и перезайдите в приложение")

                .setPositiveButton("Перезайти") { _, _ ->
                    finish()
                    startActivity(intent)
                }.setNegativeButton("Выйти") { _, _ -> finish() }
                .setNeutralButton("Включить Wi-Fi?"
                ) { _, _ -> Utils.enableWiFi(context, true) }.create().show()
    }

    companion object {

        init {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        }

        var activeTheme: Int = 0

        var themesHashMap = HashMap<String, Int>()
    }
}
