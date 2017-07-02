package com.apps.wow.droider.Player

/**
 * Created by Jackson on 30/12/2016.
 */

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews

import com.apps.wow.droider.Feed.FeedActivity
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Const


class NotificationService : Service() {

    var track = ""
    private var status: Notification? = null
    internal var isPause = true
    private var broadcastReceiver: BroadcastReceiver? = null
    private val mView: MainView = BaseService.mView
    private var player: Player? = null

    private var mUrl: String? = null

    private fun showNotification(pos: Int) {
        val views = RemoteViews(packageName,
                R.layout.player_notification)

        val notificationIntent = Intent(this, FeedActivity::class.java)
        notificationIntent.action = Intent.ACTION_MAIN
        notificationIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0)

        val playIntent = Intent(this, NotificationService::class.java)
        playIntent.action = Const.ACTION.PLAY_ACTION
        val pendingPlayIntent = PendingIntent.getService(this, 0,
                playIntent, 0)

        val closeIntent = Intent(this, NotificationService::class.java)
        closeIntent.action = Const.ACTION.STOPFOREGROUND_ACTION

        val pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        views.setOnClickPendingIntent(R.id.status_bar_play, pendingPlayIntent)

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent)

        if (track.isEmpty())
            views.setTextViewText(R.id.track_tv, mView.podcastTitle)
        else
            views.setTextViewText(R.id.track_tv, track)

        if (pos == 0) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.pause)
        }

        if (pos == 1) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.pause)
            mView.setControlButtonImageResource(R.drawable.pause)
            mView.setVisibilityToControlButton(View.GONE)
            mView.isControlActivated = true
        }
        if (pos == 2) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.play)
            mView.setControlButtonImageResource(R.drawable.play)
            mView.setVisibilityToControlButton(View.VISIBLE)
            mView.isControlActivated = false

        }

        // .setSmallIcon(R.mipmap.ic_logo) - почему-то без него не работает кастомный лэйаут

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            status = Notification.Builder(this)
                    .setCustomContentView(views)
                    .setSmallIcon(R.mipmap.ic_launcher_droider_app)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            status = Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_stat_notify_article)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build()
            status!!.contentView = views
        } else {
            status = Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_stat_notify_article)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .notification
            status!!.contentView = views
        }
        startForeground(Const.FOREGROUND_SERVICE, status)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (intent != null && intent.getStringExtra("TRACK") != null) {
                    track = intent.getStringExtra("TRACK")
                    showNotification(if (Player.isPlaying) 0 else 2)
                }
            }
        }
        val filter = IntentFilter(Const.ACTION.BROADCAST_MANAGER_INTENT)
        registerReceiver(broadcastReceiver, filter)


        mUrl = intent.getStringExtra(Const.CAST_URL)

        if (intent.action == Const.ACTION.STARTFOREGROUND_ACTION) {
            isPause = false
            showNotification(0)
            startHQorNot()

        } else if (intent.action == Const.ACTION.PLAY_ACTION) {
            if (!isPause) {
                showNotification(2)
                player!!.stop()
                player!!.release()
                isPause = true
            } else {
                showNotification(1)
                isPause = false
                startHQorNot()
            }
        } else if (intent.action == Const.ACTION.STOPFOREGROUND_ACTION) {
            if (mView.controlButton != null) {
                mView.setControlButtonImageResource(R.drawable.play)
                mView.setVisibilityToControlButton(View.VISIBLE)
                mView.isControlActivated = false
            }
            player!!.stop()
            player!!.release()
            stopForeground(true)
            stopSelf()
        }

        return Service.START_STICKY
    }

    private fun startHQorNot() {
        if (player == null) {
            player = Player(mUrl ?: "", this, mView)
        }
        player?.start()
    }
}
