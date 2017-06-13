package com.apps.wow.droider.Utils

/**
 * Created by Jackson on 30/12/2016.
 */

object Const {

    /**
     * mp3:"https://droidercast.podster.fm/15/download/audio.mp3?media=player",
     * download:"https://droidercast.podster.fm/15/download/audio.mp3?download=yes&media=file",
     */


    val PODCAST_PATH_PLAYER = "https://droidercast.podster.fm/%s/download/audio.mp3?media=player"

    val PODCAST_PATH_DOWNLOAD = "https://droidercast.podster.fm/%s/download/audio.mp3?download=yes&media=file"

    val PODCAST_PATH_SHARE = "https://droidercast.podster.fm/%s"


    val VIBRATE_TIME: Long = 100

    val CAST_ID = "DR_CAST_ID"

    val CAST_NAME = "DR_CAST_NAME"

    interface ACTION {
        companion object {
            val MAIN_ACTION = "com.jassdev.apps.andrroider.uradio.action.main"
            val PLAY_ACTION = "com.jassdev.apps.andrroider.uradio.action.play"
            val STARTFOREGROUND_ACTION = "com.jassdev.apps.andrroider.uradio.action.startforeground"
            val STOPFOREGROUND_ACTION = "com.jassdev.apps.andrroider.uradio.action.stopforeground"
            val BROADCAST_MANAGER_INTENT = "com.jassdev.apps.andrroider.uradio.action.updateNotification"
        }
    }

    var FOREGROUND_SERVICE = 101
}