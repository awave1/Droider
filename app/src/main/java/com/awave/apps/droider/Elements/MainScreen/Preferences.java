package com.awave.apps.droider.Elements.MainScreen;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.awave.apps.droider.R;


public class Preferences extends PreferenceFragment {

    public static final String THEME_RED = "RedTheme";
    public static final String THEME_LIGHT = "LightTheme";
    public static final String THEME_DARK = "DarkTheme";
    public static final String THEME_DAYTIME = "DayNightAuto";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_pref);

        findPreference("theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getActivity().finish();
                startActivity(getActivity().getIntent());
                return true;
            }
        });
    }
}
