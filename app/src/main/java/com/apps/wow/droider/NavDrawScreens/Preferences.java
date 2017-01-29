package com.apps.wow.droider.NavDrawScreens;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.apps.wow.droider.R;


public class Preferences extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
