package com.apps.wow.droider

import android.app.Application

import com.apps.wow.droider.Utils.AppContext
import com.facebook.drawee.backends.pipeline.Fresco

/**
 * Created by Jackson on 14/05/2017.
 */

class DroiderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        AppContext(applicationContext)
    }
}
