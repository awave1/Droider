package com.apps.wow.droider.Player

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import com.apps.wow.droider.R
import com.google.android.exoplayer.*


/**
 * Created by Jackson on 30/12/2016.
 */

class Player(URL: String, context: Context, view: MainView) {

    init {
        val URI = Uri.parse(URL)
        val sampleSource = FrameworkSampleSource(context, URI, null)
        release()
        audioRenderer = MediaCodecAudioTrackRenderer(sampleSource, null, true)
        exoPlayer = ExoPlayer.Factory.newInstance(1)
        mView = view
    }

    fun start() {
        stop()
        exoPlayer?.prepare(audioRenderer)
        exoPlayer?.setPlayWhenReady(true)
        exoPlayer!!.addListener(object : ExoPlayer.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                // This state if player is ready to work and loaded all data
                Log.d(TAG, "onPlayerStateChanged: " + playbackState)
                if (playbackState == 3 || playbackState == 4) {
                    mView.setVisibilityToControlButton(View.VISIBLE)
                    mView.setControlButtonImageResource(R.drawable.pause)
                } else if (playbackState == 1) {
                    mView.setVisibilityToControlButton(View.VISIBLE)
                    mView.setControlButtonImageResource(R.drawable.play)
                }
            }

            override fun onPlayWhenReadyCommitted() {
//                exoPlayer!!.seekTo(123456L)
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                Log.e(TAG, "onPlayerError: ", error)
                release()
            }
        })
    }

    val isPlaying: Boolean
        get() = exoPlayer != null && exoPlayer!!.getPlaybackState() > 1 && exoPlayer!!.getPlaybackState() < 5

    fun stop() {
        if (exoPlayer != null) {
            exoPlayer!!.stop()
        }
    }

    fun release() {
        if (exoPlayer != null) {
            exoPlayer!!.release()
        }
    }


    companion object {
        lateinit var mView : MainView
        var exoPlayer: ExoPlayer? = null
        lateinit internal var audioRenderer: TrackRenderer
    }
}