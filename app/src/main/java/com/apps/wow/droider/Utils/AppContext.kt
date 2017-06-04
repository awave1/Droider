package com.apps.wow.droider.Utils

import android.content.Context

/**
 * Created by Jackson on 14/05/2017.
 */

class AppContext(mContext: Context) {

    init {
        Companion.context = mContext
    }

    companion object {
        lateinit var context: Context
            internal set
    }
}
