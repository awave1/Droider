package com.awave.apps.droider.Elements.MainScreen;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awave.apps.droider.R;


public class Preferences extends PreferenceFragment {

    int theme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String themeName = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("theme", "Светлая");
        if (themeName.equals("Светлая")) {
            theme = R.style.LightTheme;
        } else if (themeName.equals("Тёмная")) {
            theme = R.style.DarkTheme;
        }
        super.onCreate(savedInstanceState);
        getActivity().setTheme(theme);
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

