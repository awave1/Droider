package com.apps.wow.droider.NavDrawScreens

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.apps.wow.droider.Feed.FeedActivity
import com.apps.wow.droider.Feed.Interactors.FeedLoadingInteractor
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Const
import java.util.*


class NotifyService : Service() {
    private lateinit var mSharedPrefs: SharedPreferences
    private val mTimer = Timer()
    val mFeedLoadingInteractor = FeedLoadingInteractor()

    private var mPostTitle: String? = null


    override fun onCreate() {
        super.onCreate()
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isPushEnabled) {
            try {
                mTimer.schedule(object : TimerTask() {
                    override fun run() {
                        loadLastPost()
                    }
                }, newPush(interval().toLong()), newPush(interval().toLong()))
                Log.d("NOTIFY", "Repeat in " + interval() + "hour")
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

        } else {
            stopSelf()
        }

        return Service.START_REDELIVER_INTENT
    }

    private fun loadLastPost() {
        mFeedLoadingInteractor.loadFeed(Const.CATEGORY_MAIN, Const.SLUG_MAIN, 1, 0).subscribe({
            if (it.isSuccessful && !it.body()?.posts?.isEmpty()!!) {
                if (mSharedPrefs.getString(Const.PREF_NAME_URL, null) != it.body()?.posts?.get(0)?.url) {
                    mSharedPrefs.edit().putString(Const.PREF_NAME_URL, it.body()?.posts?.get(0)?.url).apply()
                    mPostTitle = it.body()?.posts?.get(0)?.titleValue
                    buildAndSendNotification()
                }
            }
        })
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun interval(): Byte {
        if (mSharedPrefs.getString("interval", "3").toInt() == 3)
            return 3
        else if (mSharedPrefs.getString("interval", "6").toInt() == 6)
            return 6
        else if (mSharedPrefs.getString("interval", "12").toInt() == 12)
            return 12
        else if (mSharedPrefs.getString("interval", "24").toInt() == 24)
            return 24
        else
            return 1
    }

    private val isPushEnabled: Boolean
        get() = mSharedPrefs.getBoolean("notify", false)

    //time * 1000 * 60 * 60 = time часов
    private fun newPush(time: Long): Long {
        return time * 1000 * 60 * 60
    }

    private fun buildAndSendNotification() {

        val channelId = "com.apps.wow.droider"
        val builder = NotificationCompat.Builder(applicationContext, channelId)

        val notifyIntent = Intent(applicationContext, FeedActivity::class.java)
        val contextIntent = PendingIntent.getActivity(
                applicationContext, 0, notifyIntent,
                PendingIntent.FLAG_CANCEL_CURRENT)

        builder.setContentIntent(contextIntent)
                .setSmallIcon(R.mipmap.ic_stat_notify_article)
                .setTicker(getString(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(mPostTitle)
                .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)

        val notification = builder.build()

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(Const.NOTIFY_ID, notification)
    }
}
