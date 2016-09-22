package com.awave.apps.droider;

import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import java.util.HashMap;

public class DroiderBaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    protected int activeTheme;
    private HashMap<String, Integer> themesHashMap;

    protected void themeSetup() {
        if ((themesHashMap == null) || themesHashMap.isEmpty()) {
            themesHashMap = new HashMap<>();
            themesHashMap.put(getString(R.string.pref_theme_entry_light), R.style.RedTheme);
            themesHashMap.put(getString(R.string.pref_theme_entry_dark), R.style.RedThemeDark);
            themesHashMap.put(getString(R.string.pref_theme_entry_adaptive), R.style.AdaptiveTheme);
        }
        String themeName = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(R.string.pref_theme_key), getString(R.string.pref_theme_entry_light));
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
}
