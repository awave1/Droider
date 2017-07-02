package com.apps.wow.droider.Player

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import com.apps.wow.droider.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory


/**
 * Created by Jackson on 30/12/2016.
 */

class Player(URL: String, context: Context, view: MainView) {

    private var playbackPosition: Long? = null

    private var currentWindow: Int? = null


    init {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(context), DefaultTrackSelector(), DefaultLoadControl())
//        val mediaSource = buildMediaSource(Uri.parse(URL))
        val mediaSource = buildMediaSource(Uri.parse("https://cs1-64v4.vk-cdn.net/p20/53b7abb098aec3.mp3"))
        exoPlayer!!.prepare(mediaSource, true, false)
        mView = view
    }

    private fun buildMediaSource(uri: Uri?): MediaSource? {
        return ExtractorMediaSource(uri,
                DefaultHttpDataSourceFactory("user-agent"),
                DefaultExtractorsFactory(), null, null)
    }

    fun start() {
        if (!wasPaused) {
            stop()
        }
        if (playbackPosition != null && playbackPosition != 0.toLong() && !wasPaused) {
            exoPlayer?.seekTo(playbackPosition!!)
        }
        exoPlayer?.playWhenReady = true

        exoPlayer?.addListener(object : ExoPlayer.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                Log.d(TAG, "onPlaybackParametersChanged: ")
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.e(TAG, "onPlayerError: ", error)
                release()
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                // This state if player is ready to work and loaded all data
                Log.d(TAG, "onPlayerStateChanged: " + playbackState)
                if (playbackState == 3) {
                    mView.setVisibilityToControlButton(View.VISIBLE)
                    mView.setControlButtonImageResource(R.drawable.pause)
                    if (isPlaying)
                        mView.setupSeekbar()
                } else if (playbackState == 1) {
                    mView.setVisibilityToControlButton(View.VISIBLE)
                    mView.setControlButtonImageResource(R.drawable.play)
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.d(TAG, "onLoadingChanged: ")
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
                Log.d(TAG, "onTimelineChanged: ")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                Log.d(TAG, "onTracksChanged: ")
            }

            override fun onPositionDiscontinuity() {
                Log.d(TAG, "onPositionDiscontinuity: ")
            }

        })
    }

    fun stop() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer?.currentPosition
            currentWindow = exoPlayer?.currentWindowIndex
            wasPaused = false
            exoPlayer?.stop()
        }
    }

    fun pause() {
        if (exoPlayer != null) {
            exoPlayer?.playWhenReady = false
            pauseTime = exoPlayer?.currentPosition!! / 1000
            playbackPosition = exoPlayer?.currentPosition
            wasPaused = true
        }
    }

    fun release() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer?.currentPosition
            currentWindow = exoPlayer?.currentWindowIndex
            wasPaused = false
            exoPlayer?.release()
            exoPlayer = null
        }
    }


    companion object {
        lateinit var mView: MainView
        var pauseTime: Long? = 0
        private var wasPaused: Boolean = true
        @SuppressLint("StaticFieldLeak")
        var exoPlayer: SimpleExoPlayer? = null
        val isPlaying: Boolean
            get() = exoPlayer != null && exoPlayer!!.playbackState == 3
                    && exoPlayer!!.playWhenReady
    }
}