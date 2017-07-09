package com.apps.wow.droider

import android.app.Application
import com.apps.wow.droider.Utils.AppContext
import com.facebook.drawee.backends.pipeline.Fresco
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Jackson on 14/05/2017.
 */

class DroiderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        AppContext(applicationContext)
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build())
    }
}
