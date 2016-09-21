package com.awave.apps.droider;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

public class DroiderBaseActivity extends AppCompatActivity {

    private HashMap<String, Integer> themesHashMap;
    protected int activeTheme;

    protected void themeSetup() {
        if ((themesHashMap == null) || themesHashMap.isEmpty()) {
            themesHashMap = new HashMap<>();
            themesHashMap.put(getString(R.string.pref_theme_entry_red), R.style.RedTheme);
            themesHashMap.put(getString(R.string.pref_theme_entry_dark), R.style.RedThemeDark);
            themesHashMap.put(getString(R.string.pref_theme_entry_daytime), R.style.DayNightAuto);
        }
        String themeName = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(R.string.pref_theme_key), getString(R.string.pref_theme_entry_red));
        activeTheme = themesHashMap.get(themeName);
        setTheme(activeTheme);
    }

}
