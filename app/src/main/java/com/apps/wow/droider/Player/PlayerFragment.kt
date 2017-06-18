package com.apps.wow.droider.Player

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.support.annotation.Nullable
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Const
import com.apps.wow.droider.Utils.Const.CAST_ID
import com.apps.wow.droider.Utils.Const.CAST_NAME
import com.apps.wow.droider.databinding.PodcastFragmentBinding
import com.google.android.exoplayer.ExoPlayer


/**
 * Created by Jackson on 15/01/2017.
 */

class PlayerFragment : android.support.v4.app.Fragment(), MainView {

    // Boolean for check if play/pause button is activated
    private var controlIsActivated = false
    private lateinit var binding: PodcastFragmentBinding
    private var player: Player? = null
    private var headsetPlugReceiver: MusicIntentReceiver? = null

    private lateinit var exoPlayer: ExoPlayer

    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        binding = PodcastFragmentBinding.inflate(inflater!!, container, false)


        binding.podcastName.text = arguments.getString(CAST_NAME)

        binding.controlButton.setOnClickListener {
            togglePlayPause()
        }

        binding.share.setOnClickListener {
            share()
        }

        binding.seekBar.setOnSeekbarChangeListener { p0 -> Player.exoPlayer?.seekTo(p0!!.toLong()) }

        return binding.root
    }

    private fun share() {
        if (podcastTitle != "") {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, podcastTitle + "\n"
                    + Const.PODCAST_PATH_SHARE.format(arguments.getString(CAST_ID)))
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, "Поделиться подкастом:"))
        } else {
            showToast("Откуда мне знать, что играет, если плеер выключено?")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (player == null || headsetPlugReceiver == null) {
            if (activity != null) {
                player = Player(Const.PODCAST_PATH_PLAYER.format(arguments.getString(CAST_ID)), activity, this)
            }
            headsetPlugReceiver = MusicIntentReceiver()
            val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
            activity.registerReceiver(headsetPlugReceiver, filter)
        }
    }

    override fun onDestroy() {
        activity.unregisterReceiver(headsetPlugReceiver)
        super.onDestroy()
    }

    private fun togglePlayPause() {
        if (activity != null) {
            if (!isControlActivated) {
                player?.start()
                setIsControlActivated(true)
                binding.controlButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.pause))
                vibrate()
            } else {
                player!!.stop()
                setIsControlActivated(false)
                refreshNotification()
                binding.controlButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.play))
                vibrate()
            }
        }
    }

    fun startPlayProgressUpdater() {
        binding.seekBar.minValue = Player.exoPlayer?.currentPosition!!.toFloat()
        val notification = Runnable { startPlayProgressUpdater() }
        Handler().postDelayed(notification, 1000)
    }

    // Service for background audio binding.playing
    fun startPlayerService() {
        //        if (getActivity() != null) {
        //            new BaseService(this); // just for working service with mvp
        //            Intent serviceIntent = new Intent(getActivity(), NotificationService.class);
        //            serviceIntent.setAction(Const.ACTION.STARTFOREGROUND_ACTION);
        //            getActivity().startService(serviceIntent);
        //        }
    }

    // Vibrate when click on control button
    fun vibrate() {
        if (activity != null)
            (activity.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(Const.VIBRATE_TIME)
    }

    override fun isControlActivated(): Boolean {
        return controlIsActivated
    }

    override fun setIsControlActivated(isActivated: Boolean) {
        controlIsActivated = isActivated
    }

    override fun setControlButtonImageResource(resource: Int) {
        binding.controlButton.setImageResource(resource)
    }

    override fun setVisibilityToControlButton(visibility: Int) {
        binding.controlButton.visibility = visibility
        binding.seekBar.minValue = Player.exoPlayer?.currentPosition!!.toFloat()
        binding.seekBar.maxValue = Player.exoPlayer?.duration!!.toFloat()
        startPlayProgressUpdater()
    }

    override fun getControlButton(): ImageButton {
        return binding.controlButton
    }

    override fun refreshNotification() {
        if (activity != null) {
            val broadcastIntent = Intent()
            broadcastIntent.action = Const.ACTION.BROADCAST_MANAGER_INTENT
            broadcastIntent.putExtra("TRACK", podcastTitle)
            activity.sendBroadcast(broadcastIntent)
        }
    }

    override fun showToast(text: String) {
        if (activity != null)
            Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }

    override fun getPodcastTitle(): String {
        return binding.podcastName.text.toString()
    }

    inner class MusicIntentReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                val state = intent.getIntExtra("state", -1)
                when (state) {
                    0 -> {
                        if (isControlActivated)
                            togglePlayPause()
                        Log.d(TAG, "Headset is unplugged")
                    }
                    1 -> Log.d(TAG, "Headset is plugged")
                    else -> Log.d(TAG, "I have no idea what the headset state is")
                }
            }
        }
    }

    companion object {

        /**
         * https://bitbucket.org/mrcpp/rapliveradio/src/7548be6d5b6b9330421e91f756150099d06b0c3d/app/src/main/java/radio/raplive/ru/rapliveradio/ActivityMain.java?at=master&fileviewer=file-view-default
         * https://tproger.ru/articles/android-online-radio/
         */


        fun newInstance(castId: String, castName: String): PlayerFragment {
            val fragment = PlayerFragment()
            fragment.retainInstance = true
            val b = Bundle()
            b.putString(CAST_ID, castId)
            b.putString(CAST_NAME, castName)
            fragment.arguments = b
            return fragment
        }
    }
}
