package com.apps.wow.droider.Player

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.View
import com.apps.wow.droider.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import timber.log.Timber


/**
 * Created by Jackson on 30/12/2016.
 */

class Player(URL: String, context: Context, view: MainView) {

    private var currentWindow: Int? = null

    init {
        val defaultAllocator = DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE)
        val defaultLoadControl = DefaultLoadControl(defaultAllocator, 1000,
                1000, 1000.toLong(),
                1000.toLong())
        exoPlayer = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(context),
                DefaultTrackSelector(), defaultLoadControl)
        val mediaSource = buildMediaSource(Uri.parse(URL))
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
        if (pauseTime != 0L && !wasPaused) {
            exoPlayer?.seekTo(pauseTime)
        }
        exoPlayer?.playWhenReady = true

        exoPlayer?.addListener(object : ExoPlayer.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                Timber.d("onPlaybackParametersChanged: ")
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Timber.e(error, "onPlayerError: ")
                release()
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                // This state if player is ready to work and loaded all data
                Timber.d("onPlayerStateChanged: %s", playbackState)
                if (playbackState == 3) {
                    mView.setVisibilityToControlButton(View.VISIBLE)
                    mView.setControlButtonImageResource(R.drawable.pause)
                    if (!isPlayerUISetuped && isPlaying) {
                        mView.setupSeekbar()
                        isPlayerUISetuped = true
                    }
                } else if (playbackState == 1) {
                    mView.setVisibilityToControlButton(View.VISIBLE)
                    mView.setControlButtonImageResource(R.drawable.play)
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Timber.d("onLoadingChanged: ")
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
                Timber.d("onTimelineChanged: ")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                Timber.d("onTracksChanged: ")
            }

            override fun onPositionDiscontinuity() {
                Timber.d("onPositionDiscontinuity: ")
            }

        })
    }

    fun stop() {
        if (exoPlayer != null) {
            pauseTime = exoPlayer?.currentPosition!!
            currentWindow = exoPlayer?.currentWindowIndex
            wasPaused = false
            exoPlayer?.stop()
        }
    }

    fun pause() {
        if (exoPlayer != null) {
            exoPlayer?.playWhenReady = false
            pauseTime = exoPlayer?.currentPosition!!
            wasPaused = true
        }
    }

    fun release() {
        if (exoPlayer != null) {
            pauseTime = exoPlayer?.currentPosition!!
            currentWindow = exoPlayer?.currentWindowIndex
            wasPaused = false
            exoPlayer?.release()
            exoPlayer = null
        }
    }


    companion object {
        lateinit var mView: MainView
        var pauseTime: Long = 0
        private var wasPaused = true
        @SuppressLint("StaticFieldLeak")
        var exoPlayer: SimpleExoPlayer? = null
        private var isPlayerUISetuped = false
        val isPlaying: Boolean
            get() = exoPlayer != null && exoPlayer!!.playbackState == 3
                    && exoPlayer!!.playWhenReady
    }
}