package com.apps.wow.droider.Adapters

import android.content.Intent

/**
 * Created by Jackson on 04/06/2017.
 */

interface AdapterToFragmentRouter {
    fun startActivityFromAdapter(intent: Intent?)
}
