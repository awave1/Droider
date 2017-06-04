package com.apps.wow.droider.NavDrawScreens

import android.os.Bundle
import android.preference.PreferenceFragment

import com.apps.wow.droider.R


class Preferences : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        addPreferencesFromResource(R.xml.main_pref)

        findPreference("theme").setOnPreferenceChangeListener { _, _ ->
            activity.finish()
            startActivity(activity.intent)
            true
        }
    }
}
