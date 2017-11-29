package com.apps.wow.droider.Article

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidslidr.Slidr
import com.apps.wow.droider.Player.BaseService
import com.apps.wow.droider.Player.MainView
import com.apps.wow.droider.Player.NotificationService
import com.apps.wow.droider.Player.Player
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Const
import com.apps.wow.droider.Utils.Const.CAST_ID
import com.apps.wow.droider.Utils.Const.CAST_NAME
import com.apps.wow.droider.Utils.Const.POST_HTML
import com.apps.wow.droider.databinding.PodcastFragmentBinding
import org.jetbrains.anko.support.v4.browse
import rx.Observable
import rx.Subscription
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * Created by Jackson on 15/01/2017.
 */

class PlayerFragment : Fragment(), MainView {

    private lateinit var binding: PodcastFragmentBinding
    private var player: Player? = null
    private var headsetPlugReceiver: MusicIntentReceiver? = null
    private var playerSubscription: Subscription? = null
    private lateinit var serviceIntent: Intent
    private var lastTime: Float = 0.0F

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        binding = PodcastFragmentBinding.inflate(inflater!!, container, false)

        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Const.CAST_URL,
                arguments.getString(CAST_ID)).apply()

        controlButton = binding.controlButton

        binding.podcastName.text = arguments.getString(CAST_NAME)

        binding.controlButton.setOnClickListener {
            turnLoadingAnimation(true)
            togglePlayPause()
        }

        binding.share.setOnClickListener { share() }
        binding.download.setOnClickListener { download() }

        binding.slider.setListener(object : Slidr.Listener {
            override fun bubbleClicked(slidr: androidslidr.Slidr?) {
            }

            override fun valueChanged(slidr: androidslidr.Slidr?, currentValue: Float) {
                // +-5 for corner cases
                if ((currentValue > (Player.pauseTime + 5)) || (currentValue < (Player.pauseTime - 5))) {
                    Player.exoPlayer?.seekTo(currentValue.toLong())
                }
            }
        })

        binding.slider.setTextMin(formatText(0L))

        podcastTitle = binding.podcastName.text.toString()

        if (Player.isPlaying) {
            binding.controlButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.pause))
            isControlActivated = true
            setupSeekbar()
        } else {
            binding.controlButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.play))
        }

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

    private fun download() {
        browse(Const.PODCAST_PATH_DOWNLOAD.format(arguments.getString(CAST_ID)))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (player == null || headsetPlugReceiver == null) {
            if (activity != null && !Player.isPlaying) {
                initPlayer()
            }
            headsetPlugReceiver = MusicIntentReceiver()
            val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
            activity.registerReceiver(headsetPlugReceiver, filter)
        }
        binding.slider.setTextFormatter { "" }

        setupBottomSheet()
    }

    private fun initPlayer() {
        player = Player(Const.PODCAST_PATH_PLAYER.format(arguments.getString(CAST_ID)), activity, this)
    }

    override fun onDestroy() {
        try {
            if (!Player.isPlaying) {
                activity.unregisterReceiver(headsetPlugReceiver)
                activity.stopService(serviceIntent)
                Player.exoPlayer?.release()
                PreferenceManager.getDefaultSharedPreferences(context).edit().remove(Const.CAST_URL)
                        .apply()
            }
            if (playerSubscription != null && !playerSubscription!!.isUnsubscribed)
                playerSubscription!!.unsubscribe()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    private fun togglePlayPause() {
        if (activity != null) {
            if (!isControlActivated || !Player.isPlaying) {
                if (player == null) {
                    initPlayer()
                }
                player?.start()
                startPausePlayerService(true)
                if (lastTime > 0.0F)
                    Player.exoPlayer?.seekTo(lastTime.toLong())
                isControlActivated = true
                binding.controlButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.pause))
                vibrate()
            } else {
                turnLoadingAnimation(false)
                pausePlayer()
                isControlActivated = false
                startPausePlayerService(false)
                binding.controlButton.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.play))
                vibrate()
            }
        }
    }

    override fun setupSeekbar() {
        val millis = Player.exoPlayer?.duration

        if (millis != null) {
            binding.slider.max = millis.toFloat()
        }
        binding.slider.setMin(0F)

        Player.exoPlayer?.duration?.toFloat()?.let { binding.slider.max = it }

        binding.slider.setTextFormatter { formatText(it.toLong()) }
        binding.slider.setTextMax(Player.exoPlayer?.duration?.let { formatText(it) })

        turnLoadingAnimation(false)

        if (playerSubscription != null && !playerSubscription!!.isUnsubscribed)
            playerSubscription!!.unsubscribe()

        startPlayProgressUpdater()
    }

    private fun formatText(millis: Long) =
            if (TimeUnit.MILLISECONDS.toHours(millis) > 0)
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1))
            else
                String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1))

    override fun startOrResume() {
        player?.start()
    }

    private fun startPlayProgressUpdater() {
        if (Player.isPlaying) {
            Timber.d("Time in sec: %s", Player.pauseTime)
            playerSubscription = Observable.interval(1000L, TimeUnit.MILLISECONDS)
                    .timeInterval().subscribe({
                if (Player.isPlaying) {
                    Player.exoPlayer?.currentPosition?.let {
                        Player.pauseTime = it
                        binding.slider.currentValue = it.toFloat()
                        binding.slider.setTextMin(formatText(it))
                    }

                }
            }, { it.printStackTrace() })
        }
    }

    // Service for background audio binding.playing
    private fun startPausePlayerService(startService: Boolean) {
        if (activity != null) {
            BaseService(this) // just for working service with mvp
            serviceIntent = Intent(activity, NotificationService::class.java)
            serviceIntent.action = if (startService) Const.ACTION.STARTFOREGROUND_ACTION else Const.ACTION.PLAY_ACTION
            activity.startService(serviceIntent)
        }
    }

    // Vibrate when click on control button
    private fun vibrate() {
        if (activity != null)
            (activity.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(Const.VIBRATE_TIME)
    }

    override fun setControlButtonImageResource(resource: Int) {
        binding.controlButton.setImageResource(resource)
    }

    override fun setVisibilityToControlButton(visibility: Int) {
        binding.controlButton.visibility = visibility

    }

    override fun showToast(text: String) {
        if (activity != null)
            Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }

    override fun pausePlayer() {
        player?.pause()
    }

    override fun stopAndReleasePlayer() {
        player?.stop()
        player?.release()
    }

    inner class MusicIntentReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                val state = intent.getIntExtra("state", -1)

                // wtf is 0 and 1 ?!
                when (state) {
                    0 -> {
                        if (isControlActivated)
                            togglePlayPause()
                        Timber.d("Headset is unplugged")
                    }
                    1 -> Timber.d("Headset is plugged")
                    else -> Timber.d("I have no idea what the headset state is")
                }
            }
        }
    }

    private fun turnLoadingAnimation(on: Boolean) {
        binding.loadAnimation.visibility = if (on) View.VISIBLE else View.GONE
    }

    private fun setupBottomSheet() {
        fragmentManager.beginTransaction().replace(R.id.podcastPostContainer,
                ArticleForPlayerFragment.newInstance(arguments.getString(POST_HTML))).commit()
    }


    override var isControlActivated: Boolean = false
    override lateinit var controlButton: ImageButton
    override lateinit var podcastTitle: String

    companion object {

        /**
         * https://bitbucket.org/mrcpp/rapliveradio/src/7548be6d5b6b9330421e91f756150099d06b0c3d/app/src/main/java/radio/raplive/ru/rapliveradio/ActivityMain.java?at=master&fileviewer=file-view-default
         * https://tproger.ru/articles/android-online-radio/
         */

        fun newInstance(html: String?, castId: String, castName: String?): PlayerFragment {
            val fragment = PlayerFragment()
            fragment.retainInstance = true
            val b = Bundle()
            b.putString(CAST_ID, castId)
            b.putString(CAST_NAME, castName)
            b.putString(POST_HTML, html)
            fragment.arguments = b
            return fragment
        }
    }
}
