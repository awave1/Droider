package com.apps.wow.droider.Adapters

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
import com.apps.wow.droider.Feed.FeedFragment
import com.apps.wow.droider.R
import java.util.*

class NotifyService : Service() {
    private var sp: SharedPreferences? = null
    private val myTimer = Timer()
    private var notificationManager: NotificationManager? = null

    override fun onCreate() {
        sp = PreferenceManager.getDefaultSharedPreferences(this)
        super.onCreate()
        notificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isPushEnabled) {
            try {
                myTimer.schedule(object : TimerTask() {
                    override fun run() {
                        Notify()
                    }
                }, NewPush(Interval().toLong()), NewPush(Interval().toLong()))
                Log.d("NOTIFY", "Repeat in " + Interval() + "hour")
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

        }

        return Service.START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun Interval(): Byte {
        if (java.lang.Byte.parseByte(sp!!.getString("interval", "3")).toInt() == 3)
            return 3
        else if (java.lang.Byte.parseByte(sp!!.getString("interval", "6")).toInt() == 6)
            return 6
        else if (java.lang.Byte.parseByte(sp!!.getString("interval", "12")).toInt() == 12)
            return 12
        else if (java.lang.Byte.parseByte(sp!!.getString("interval", "24")).toInt() == 24)
            return 24
        else
            return 1
    }

    private val isPushEnabled: Boolean
        get() = sp!!.getBoolean("notify", false)

    private fun NewPush(time: Long): Long {
        return time * 1000 * 60 * 60
    }  //time * 1000 * 60 * 60 = time часов

    private fun Notify() {
        Thread(Runnable {
            val builder = NotificationCompat.Builder(
                    applicationContext
            )
            val notifyIntent = Intent(applicationContext, FeedFragment::class.java)
            val contextIntent = PendingIntent.getActivity(
                    applicationContext, 0, notifyIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT
            )

            builder.setContentIntent(contextIntent)
                    .setSmallIcon(R.mipmap.ic_stat_notify_article)
                    .setTicker(getString(R.string.app_name))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(getString(R.string.notification_text))

            val notification = builder.build()

            notification.defaults = Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS

            notificationManager!!.notify(NotifyService.NOTIFY_ID, notification)
        }).start()
    }

    companion object {
        private val NOTIFY_ID = 101
    }
}
