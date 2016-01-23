package com.awave.apps.droider.Elements.MainScreen;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.awave.apps.droider.R;


public class Preferences extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_pref);
    }

}

