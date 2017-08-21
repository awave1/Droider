package com.apps.wow.droider.Article

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.support.v7.content.res.AppCompatResources
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.apps.wow.droider.Adapters.ArticleSimilarAdapter
import com.apps.wow.droider.DroiderBaseActivity
import com.apps.wow.droider.Model.Post
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.BitmapLoaded
import com.apps.wow.droider.Utils.Const
import com.apps.wow.droider.Utils.Utils
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import io.codetail.animation.ViewAnimationUtils
import io.realm.Realm
import kotlinx.android.synthetic.main.article.*
import java.util.*

/**
 * Created by Jackson on 21/08/2017.
 */

class ArticleFragment : MvpAppCompatFragment(), AppBarLayout.OnOffsetChangedListener, ArticleView {

    var hasBlur: Boolean = false

    private val youtubeFrame: FrameLayout? = null

    private var isPalette: Boolean = false

    private var extras: Bundle? = null

    private var isAnimationPlayed = false

    private var mUrl: String? = null

    private lateinit var mRealm: Realm

    @InjectPresenter(type = PresenterType.GLOBAL)
    lateinit var mArticlePresenter: ArticlePresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.article, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        Realm.init(context)
        mRealm = Realm.getDefaultInstance()
        getSharedPreferences()

        extras = activity.intent.extras ?: arguments

        mUrl = if (extras != null && extras!!.getString(Const.EXTRA_ARTICLE_URL) != null)
            extras!!.getString(Const.EXTRA_ARTICLE_URL)
        else
            activity.intent.data.toString()

        viewInitialisation()

        toolbarSetup()
        ArticleParserSetup()
        backgroundTintColorSetup()
        appbarArticle.addOnOffsetChangedListener(this)

        this.calculateMinimumHeight()
        this.setupArticleWebView(article)
    }

    private fun getSharedPreferences() {
        hasBlur = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("beta_enableBlur", false)
        isPalette = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("palette", false)
    }

    override fun onStart() {
        super.onStart()
        if (!isAnimationPlayed) {
            try {
                playActivityAnimation()
            } catch (e: UnsupportedOperationException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }

            isAnimationPlayed = true
        }
    }

    override fun onPause() {
        super.onPause()
        articleBackgroundNSV.isNestedScrollingEnabled = true
    }

    private fun playActivityAnimation() {
        val animatedView = articleCoordinatorLayout
        animatedView.post {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                createFadeAnimation(animatedView)
            } else {
                //should fix
                //Exception java.lang.IllegalStateException: Cannot start this animator on a detached view!
                if (ViewCompat.isAttachedToWindow(animatedView)) {
                    createCircularRevealAnimation(animatedView)
                }
            }
        }
    }

    private fun createFadeAnimation(animatedView: View) {
        val objectAnimator = ObjectAnimator.ofFloat(animatedView, "alpha", 0f, 1f)
        objectAnimator.duration = 320
        objectAnimator.start()
    }

    private fun createCircularRevealAnimation(animatedView: View) {
        animatedView.visibility = View.VISIBLE
        val extras = activity.intent.extras
        var touchXCoordinate: Float
        var touchYCoordinate: Float
        try {
            touchXCoordinate = extras.getFloat(Const.EXTRA_ARTICLE_X_TOUCH_COORDINATE, 0f)
            touchYCoordinate = extras.getFloat(Const.EXTRA_ARTICLE_Y_TOUCH_COORDINATE, 0f)
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
            touchXCoordinate = 0f
            touchYCoordinate = 0f
        }

        val animator = ViewAnimationUtils.createCircularReveal(animatedView, touchXCoordinate.toInt(), touchYCoordinate.toInt(),
                0f, Const.CIRCULAR_REVIVAL_ANIMATION_RADIUS.toFloat())
        animator.interpolator = AccelerateInterpolator()
        animator.duration = 320
        animator.start()
    }

    private fun backgroundTintColorSetup() {
        if (DroiderBaseActivity.Companion.activeTheme != R.style.AdaptiveTheme) {
            articleBackgroundTintView.background = AppCompatResources.getDrawable(context,
                    if (DroiderBaseActivity.Companion.activeTheme == R.style.RedTheme)
                        R.drawable.article_background_tint_light
                    else
                        R.drawable.article_background_tint_dark)
        } else {
            val tintDrawable = AppCompatResources
                    .getDrawable(context, R.drawable.article_background_tint_dark)
            tintDrawable?.setColorFilter(
                    (activity as DroiderBaseActivity).getThemeAttribute(android.R.attr.colorBackground, DroiderBaseActivity.Companion.activeTheme),
                    PorterDuff.Mode.SRC_ATOP)
            articleBackgroundTintView.background = tintDrawable
        }
    }


    private fun ArticleParserSetup() {
        /** Проверка как мы попали в статью  */
        intentExtraChecking()
    }

    private fun intentExtraChecking() {
        if (Intent.ACTION_VIEW == activity.intent.action) {
            mArticlePresenter.provideData(activity.intent.data.toString(), setupArticleModel())
            mArticlePresenter.getPostDataForOutsideEvent()

        } else {
            Log.d(TAG, "intentExtraChecking: inner")
            mArticlePresenter.provideData(mUrl!!, setupArticleModel())

            articleHeader.text = extras?.getString(Const.EXTRA_ARTICLE_TITLE)
            articleShortDescription.text = extras?.getString(Const.EXTRA_SHORT_DESCRIPTION)

            articleHeaderImg.setImageURI(extras?.getString(Const.EXTRA_ARTICLE_IMG_URL))
        }

        mArticlePresenter.parseArticle(mRealm)

        //            new Handler().postDelayed(() -> {
        //                if (ArticleParser.isYoutube()) {
        //                    /** а зачем зря тратить память как говорится, поэтому находим этот фрагмент только когда он точно нужен **/
        //                    youtubeFrame = (FrameLayout) findViewById(R.castID.YouTubeFrame);
        //                    setupYoutubePlayer();
        //                }
        //            }, 750);

    }

    private fun viewInitialisation() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            articleCoordinatorLayout.alpha = 0f
        } else {
            articleCoordinatorLayout.visibility = View.INVISIBLE
        }
    }

    private fun toolbarSetup() {

        (activity as ArticleActivity).setSupportActionBar(toolbarArticle)
        if ((activity as ArticleActivity).supportActionBar != null) {
            (activity as ArticleActivity).supportActionBar!!.setDisplayShowTitleEnabled(true)
            (activity as ArticleActivity).supportActionBar!!.title = ""
            (activity as ArticleActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
            (activity as ArticleActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)

            toolbarArticle.setNavigationOnClickListener { activity.finish() }
        }
    }

    @Synchronized private fun setupYoutubePlayer() {
        youtubeFrame!!.visibility = View.VISIBLE
        Log.d(TAG, "setupYoutubePlayer: ")
        val youtubeFragment = YouTubePlayerSupportFragment.newInstance()
        youtubeFragment.initialize(YOUTUBE_API_KEY, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider,
                                                 youTubePlayer: YouTubePlayer, wasResumed: Boolean) {
                //                youTubePlayer.cueVideo(ArticleParser.getYouTubeVideoURL());
            }

            override fun onInitializationFailure(provider: YouTubePlayer.Provider,
                                                 youTubeInitializationResult: YouTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure: ")

            }
        })
        fragmentManager.beginTransaction().replace(R.id.YouTubeFrame, youtubeFragment)
                .commit()
    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (Math.abs(verticalOffset) >= appBarLayout.bottom) {
            setupPaletteBackground(false)
        } else {
            collapsingToolbar.title = ""
            setupPaletteBackground(true)
            (activity as ArticleActivity).supportActionBar!!.setHomeButtonEnabled(true)
            (activity as ArticleActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_article, menu)

        menu?.getItem(0)?.icon = ResourcesCompat
                .getDrawable(resources, R.drawable.ic_open_in_browser_white_24dp, null)
        menu?.getItem(1)?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_share_white_24dp, null)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when (item.itemId) {
            R.id.action_open_in_browser -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(mUrl)))
            R.id.action_share -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        articleHeader.text.toString() + ":  " + mUrl)
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, "Отправить ссылку на статью"))
            }
        }
        return true
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupArticleWebView(w: WebView) {
        Log.d(TAG, "setupArticleWebView: ")
        w.setBackgroundColor(webViewBackgroundColor)
        val settings = w.settings
        w.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: url: " + url)
                if (url.matches(("(http(s?):/)(/[^/]+)+" + "\\.(?:jpg|gif|png)").toRegex())) {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .addToBackStack("image_prev")
                            .replace(R.id.image_preview, ImagePreviewFragment.newInstance(url))
                            .commit()
                    articleBackgroundNSV.isNestedScrollingEnabled = false
                } else {
                    view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }

                return true
            }
        }

        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        settings.setAppCacheEnabled(true)
        settings.saveFormData = true
    }

    private fun calculateMinimumHeight() {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val screenHeight = metrics.heightPixels
        var actionBarHeight = 0
        val tv = TypedValue()

        if (activity.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue
                    .complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
        articleRelLayout.minimumHeight = screenHeight - actionBarHeight
    }

    private fun setupPaletteBackground(isTransparent: Boolean) {
        if (isPalette && (DroiderBaseActivity.Companion.activeTheme == R.style.RedTheme || currentNightMode == Configuration.UI_MODE_NIGHT_NO)) {
            try {

                Utils.drawableToBitmap(articleHeaderImg.drawable)
                var p: Palette
                Utils.convertImageUrlToBitmap(extras?.getString(Const.EXTRA_ARTICLE_IMG_URL)!!, context, object : BitmapLoaded {
                    override fun readyToUse(bitmap: Bitmap) {
                        p = Palette.Builder(bitmap).generate()

                        if (p.lightVibrantSwatch != null && !isTransparent) {
                            toolbarArticle.setBackgroundColor(p.lightVibrantSwatch!!.rgb)
                            articleBackgroundNSV
                                    .setBackgroundColor(p.lightVibrantSwatch!!.rgb)
                            Log.d(TAG, "onCreate: color from bitmap: " + p.lightVibrantSwatch!!.rgb
                                    + "")
                        } else {
                            toolbarArticle.setBackgroundColor(Color.TRANSPARENT)
                            articleBackgroundNSV
                                    .setBackgroundColor(p.lightVibrantSwatch!!.rgb)
                            Log.d(TAG, "onCreate: else color from bitmap:TRANSPARENT ")
                        }
                    }
                })

            } catch (e: NullPointerException) {
                handlePaltetException(isTransparent, e)
            } catch (ise: IllegalStateException) {
                handlePaltetException(isTransparent, ise)
            }
        }
    }

    private fun handlePaltetException(isTransparent: Boolean, e: Exception) {
        if (isTransparent) {
            toolbarArticle.setBackgroundColor(Color.TRANSPARENT)
        } else {
            Log.e(TAG,
                    "onCreate: Переход по ссылке с заблюренной картинкой или Palette не может понять какой LightVibrantSwatch() ",
                    e.cause)
        }
    }


    override fun changeLoadingVisibility(isVisible: Boolean) {
        articleProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun loadArticle(articleHtml: String) {
        article
                .loadDataWithBaseURL("file:///android_asset/", articleHtml, "text/html", "UTF-8", "")
    }

    override fun setupSimilar(similar: ArrayList<Post>) {
        similarArticles.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                false)
        similarArticles.adapter = ArticleSimilarAdapter(similar)
    }

    override fun hideSimilar() {
        similarArticlesContainer.visibility = View.GONE
    }

    override fun showErrorLoading(errorHtml: String) {
        loadArticle(errorHtml)
    }

    override fun setupNecessaryFields(post: Post) {
        articleHeader.text = post.titleValue
        articleShortDescription.text = post.descriptionValue

        if (!TextUtils.isEmpty(post.pictureWide))
            articleHeaderImg.setImageURI(post.pictureWide)

    }

    private fun setupArticleModel(): ArticleModel {
        return ArticleModel(webViewTextColor!!, webViewLinkColor!!, webViewTableColor!!, webViewTableHeaderColor!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    companion object {

        private val TAG = "ArticleFragment"

        private val YOUTUBE_API_KEY = "AIzaSyBl-6eQJ9SgBSznqnQV6ts_5MZ88o31sl4"

        var webViewTextColor: String? = null

        var webViewLinkColor: String? = null

        var webViewTableColor: String? = null

        var webViewTableHeaderColor: String? = null

        var webViewBackgroundColor: Int = 0

        var currentNightMode: Int = 0

        fun newInstance(): ArticleFragment {
            val frgmnt = ArticleFragment()
            frgmnt.retainInstance = true
            return frgmnt
        }
    }
}
