package com.apps.wow.droider;

import com.google.firebase.analytics.FirebaseAnalytics;

import android.content.Context;
import android.content.DialogInterface;
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

import java.util.HashMap;

public class DroiderBaseActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    protected int activeTheme;
    private HashMap<String, Integer> themesHashMap;

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the FirebaseAnalytics newInstance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    protected void themeSetup() {
        if ((themesHashMap == null) || themesHashMap.isEmpty()) {
            themesHashMap = new HashMap<>();
            themesHashMap.put(getString(com.apps.wow.droider.R.string.pref_theme_entry_light), com.apps.wow.droider.R.style.RedTheme);
            themesHashMap.put(getString(com.apps.wow.droider.R.string.pref_theme_entry_dark), com.apps.wow.droider.R.style.RedThemeDark);
            themesHashMap.put(getString(com.apps.wow.droider.R.string.pref_theme_entry_adaptive), com.apps.wow.droider.R.style.AdaptiveTheme);
        }
        String themeName = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(com.apps.wow.droider.R.string.pref_theme_key), getString(com.apps.wow.droider.R.string.pref_theme_entry_light));
        activeTheme = themesHashMap.get(themeName);
        setTheme(activeTheme);
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

                .setPositiveButton("Перезайти", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                startActivity(getIntent());
                            }
                        })
                .setNegativeButton("Выйти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNeutralButton("Включить Wi-Fi?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.enableWiFi(context, true);
                    }
                })
                .create().show();
    }
}
