package com.apps.wow.droider;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.apps.wow.droider.Utils.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;

public class DroiderBaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    protected int activeTheme;
    private FirebaseAnalytics mFirebaseAnalytics;
    private HashMap<String, Integer> themesHashMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the FirebaseAnalytics newInstance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    protected void themeSetup() {
        themesHashMap.put(getString(com.apps.wow.droider.R.string.pref_theme_entry_light), com.apps.wow.droider.R.style.RedTheme);
        themesHashMap.put(getString(com.apps.wow.droider.R.string.pref_theme_entry_dark), com.apps.wow.droider.R.style.RedThemeDark);
        themesHashMap.put(getString(com.apps.wow.droider.R.string.pref_theme_entry_adaptive), com.apps.wow.droider.R.style.AdaptiveTheme);

        String themeName = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(com.apps.wow.droider.R.string.pref_theme_key), getString(com.apps.wow.droider.R.string.pref_theme_entry_light));

        try {
            activeTheme = themesHashMap.get(themeName);
            setTheme(activeTheme);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            setTheme(com.apps.wow.droider.R.style.AdaptiveTheme);
        }
    }

    protected int getThemeAttribute(@AttrRes int attributeInt, @StyleRes int theme) {
        // The attributes you want retrieved
        int[] attrs = {attributeInt};
        // Parse MyCustomStyle, using Context.obtainStyledAttributes()
        TypedArray attributesTypedArray = obtainStyledAttributes(theme, attrs);
        // Fetching the colors defined in your style
        int attributeValue = attributesTypedArray.getColor(0, -1);
        // OH, and don't forget to recycle the TypedArray
        attributesTypedArray.recycle();
        return attributeValue;
    }

    public void initInternetConnectionDialog(final Context context) {

        new AlertDialog.Builder(context).setTitle("Соединение нестабильно или прервано")
                .setMessage("Проверьте своё соединение с интернетом и перезайдите в приложение")

                .setPositiveButton("Перезайти", (dialog, which) -> {
                    finish();
                    startActivity(getIntent());
                })
                .setNegativeButton("Выйти", (dialog, which) -> finish())
                .setNeutralButton("Включить Wi-Fi?", (dialog, which) -> Utils.enableWiFi(context, true))
                .create().show();
    }
}
