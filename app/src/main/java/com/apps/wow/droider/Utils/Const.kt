package com.apps.wow.droider.Utils

/**
 * Created by Jackson on 30/12/2016.
 */

object Const {

    val HOME_URL = "http://droider.ru"

    val SLUG_MAIN = "main"
    val SLUG_ANDROID = "android"
    val SLUG_APPLE = "apple"
    val SLUG_GAGETS = "gadgets"
    val SLUG_NEW_GAMES = "new_games"
    val SLUG_FROM_INTERNET = "from_internet"
    val SLUG_VIDEO = "video"
    val SLUG_PODCAST = "podcast"

    val CATEGORY_MAIN = "0"
    val CATEGORY_ANDROID = "17459"
    val CATEGORY_APPLE = "17460"
    val CATEGORY_GAGETS = "17461"
    val CATEGORY_NEW_GAMES = "17932"
    val CATEGORY_FROM_INTERNET = "17463"
    val CATEGORY_VIDEO = "260"
    val CATEGORY_PODCAST = "17931"

    val DEFAULT_COUNT = 20

    val EXTRA_ARTICLE_TITLE = "com.apps.wow.droider.Elements.EXTRA_ARTICLE_TITLE"
    val EXTRA_ARTICLE_URL = "com.apps.wow.droider.Elements.EXTRA_ARTICLE_URL"
    val EXTRA_CATEGORY = "com.apps.wow.droider.CATEGORY"
    val EXTRA_SLUG = "com.apps.wow.droider.EXTRA_SLUG"
    val EXTRA_SHORT_DESCRIPTION = "com.awave.droider.Elements.EXTRA_SHORT_DESCRIPTION"
    val EXTRA_ARTICLE_X_TOUCH_COORDINATE = "EXTRA_ARTICLE_X_TOUCH_COORDINATE"
    val EXTRA_ARTICLE_Y_TOUCH_COORDINATE = "EXTRA_ARTICLE_Y_TOUCH_COORDINATE"
    val EXTRA_ARTICLE_IMG_URL = "EXTRA_ARTICLE_IMG_URL"
    var CIRCULAR_REVIVAL_ANIMATION_RADIUS = 100

    var FOREGROUND_SERVICE = 101

    val NOTIFY_ID = 2017

    val PREF_NAME_URL = "URL"
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

    val CAST_URL = "CAST_URL"

    interface ACTION {
        companion object {
            val MAIN_ACTION = "com.jassdev.apps.andrroider.uradio.action.main"
            val PLAY_ACTION = "com.jassdev.apps.andrroider.uradio.action.play"
            val STARTFOREGROUND_ACTION = "com.jassdev.apps.andrroider.uradio.action.startforeground"
            val STOPFOREGROUND_ACTION = "com.jassdev.apps.andrroider.uradio.action.stopforeground"
            val BROADCAST_MANAGER_INTENT = "com.jassdev.apps.andrroider.uradio.action.updateNotification"
        }
    }

}