package com.apps.wow.droider.player

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.apps.wow.droider.R

class PlayerActivity : AppCompatActivity() {

    var castName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val podsterNumber = intent.data.pathSegments.get(0)
        if (intent.data.pathSegments.size > 1) {
            castName = intent.data.pathSegments.get(1)
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.contentPanel, PlayerFragment.newInstance(podsterNumber, castName ?: "Droider Cast"))
                .commitNowAllowingStateLoss()
        Log.d("tag", intent.data.lastPathSegment)
    }
}
