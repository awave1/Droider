package com.apps.wow.droider.Player

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Vibrator
import android.support.annotation.Nullable
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidslidr.Slidr
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Const
import com.apps.wow.droider.Utils.Const.CAST_ID
import com.apps.wow.droider.Utils.Const.CAST_NAME
import com.apps.wow.droider.Utils.IOScheduler
import com.apps.wow.droider.databinding.PodcastFragmentBinding
import rx.subjects.PublishSubject
import java.util.concurrent.TimeUnit


/**
 * Created by Jackson on 15/01/2017.
 */

class PlayerFragment : android.support.v4.app.Fragment(), MainView {

    private lateinit var binding: PodcastFragmentBinding
    private var player: Player? = null
    private var headsetPlugReceiver: MusicIntentReceiver? = null
    private val ps: PublishSubject<Boolean> = PublishSubject.create<Boolean>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        binding = PodcastFragmentBinding.inflate(inflater!!, container, false)


        binding.podcastName.text = arguments.getString(CAST_NAME)

        binding.controlButton.setOnClickListener {
            togglePlayPause()
        }

        binding.share.setOnClickListener {
            share()
        }


        binding.slider.setListener(object : Slidr.Listener {
            override fun bubbleClicked(slidr: androidslidr.Slidr?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun valueChanged(slidr: androidslidr.Slidr?, currentValue: Float) {
                Log.d(javaClass.name, "valueChanged: " + Player.pauseTime?.let { formatText(it) })
                Log.d(javaClass.name, "valueChanged currentValue : " + formatText(currentValue.toLong()))
                if (currentValue > Player.pauseTime!! + 5) {// +2 for corner cases(maloli)
                    Player.pauseTime = slidr?.currentValue!!.toLong()
                    Player.exoPlayer?.seekTo(Player.pauseTime!!)
                }
            }
        })

        binding.slider.setTextMin(formatText(0L))

        controlButton = binding.controlButton
        podcastTitle = binding.podcastName.text.toString()

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
            showToast("чёт не получилось запомнить название подкаста, со мной такое бывает")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (player == null || headsetPlugReceiver == null) {
            if (activity != null && !Player.isPlaying) {
                player = Player(Const.PODCAST_PATH_PLAYER.format(arguments.getString(CAST_ID)), activity, this)
            }
            headsetPlugReceiver = MusicIntentReceiver()
            val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
            activity.registerReceiver(headsetPlugReceiver, filter)
            psSubscription()
            binding.slider.setTextFormatter { "" }
        }
    }

    override fun onDestroy() {
        try {
            activity.unregisterReceiver(headsetPlugReceiver)
            if (!Player.isPlaying)
                Player.exoPlayer?.release()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } catch (iae: IllegalArgumentException) {
            iae.printStackTrace()
        }
        super.onDestroy()
    }

    private fun togglePlayPause() {
        if (activity != null) {
            if (!isControlActivated || !Player.isPlaying) {
                startPlayerService()
                isControlActivated = true
                binding.controlButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.pause))
                vibrate()
            } else {
                player!!.pause()
                isControlActivated = false
                refreshNotification()
                binding.controlButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.play))
                vibrate()
            }
        }
    }

    override fun setupSeekbar() {

        val millis = Player.exoPlayer?.duration!!

        Log.d(javaClass.name, "duration: " + formatText(millis))

        binding.slider.max = millis.toFloat()
        binding.slider.setMin(0F)
        Player.exoPlayer?.duration?.toFloat()?.let { binding.slider.max = it }
        binding.slider.setTextFormatter { formatText(it.toLong()) }

        binding.slider.setTextMax(Player.exoPlayer?.duration?.let { formatText(it) })

        startPlayProgressUpdater()
    }

    fun formatText(millis: Long) =
            if (TimeUnit.MILLISECONDS.toHours(millis) > 0)
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1))
            else
                String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1))


    fun startPlayProgressUpdater() {
        if (Player.isPlaying) {
            Log.d(javaClass.name, "Time in sec: " + Player.pauseTime)
            ps.onNext(true)
        }
    }

    fun psSubscription() {
        ps.delay(1L, TimeUnit.SECONDS).onBackpressureLatest().retry().compose(IOScheduler())
                .subscribe({
                    Player.pauseTime = Player.pauseTime!!.plus(1000L)
                    binding.slider.currentValue = Player.pauseTime!!.toFloat()
                    binding.slider.setTextMin(formatText(Player.pauseTime!!))
                    startPlayProgressUpdater()
                }, { Log.e(javaClass.name, "in Observable", it) })
    }

    // Service for background audio binding.playing
    fun startPlayerService() {
        if (activity != null) {
            BaseService(this) // just for working service with mvp
            val serviceIntent: Intent = Intent(activity, NotificationService::class.java)
            serviceIntent.action = Const.ACTION.STARTFOREGROUND_ACTION
            serviceIntent.putExtra(Const.CAST_URL, Const.PODCAST_PATH_PLAYER.format(arguments.getString(CAST_ID)))
            activity.startService(serviceIntent)
        }
    }

    // Vibrate when click on control button
    fun vibrate() {
        if (activity != null)
            (activity.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(Const.VIBRATE_TIME)
    }

    override fun setControlButtonImageResource(resource: Int) {
        binding.controlButton.setImageResource(resource)
    }

    override fun setVisibilityToControlButton(visibility: Int) {
        binding.controlButton.visibility = visibility

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


    override var isControlActivated: Boolean = false
    override lateinit var controlButton: ImageButton
    override lateinit var podcastTitle: String

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
