package com.apps.wow.droider.Player


import android.widget.ImageButton

/**
 * Created by Jackson on 06/01/2017.
 */

interface MainView {

    var isControlActivated: Boolean

    fun setControlButtonImageResource(resource: Int)

    fun setVisibilityToControlButton(visibility: Int)

    val controlButton: ImageButton

    fun refreshNotification()

    fun showToast(text: String)

    val podcastTitle: String

    fun setupSeekbar()
}
