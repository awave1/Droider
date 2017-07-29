package com.apps.wow.droider.Article

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.apps.wow.droider.Utils.Utils
import com.apps.wow.droider.Utils.Utils.drawableToBitmap
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import io.codetail.animation.ViewAnimationUtils
import io.realm.Realm
import kotlinx.android.synthetic.main.article.*
import java.util.*

class ArticleActivity : DroiderBaseActivity(), AppBarLayout.OnOffsetChangedListener, ArticleView {

    var webViewTextColor: String? = null

    var webViewLinkColor: String? = null

    var webViewTableColor: String? = null

    var webViewTableHeaderColor: String? = null

    var hasBlur: Boolean = false

    private val youtubeFrame: FrameLayout? = null

    private var isPalette: Boolean = false

    private var extras: Bundle? = null

    private var webViewBackgroundColor: Int = 0

    private var currentNightMode: Int = 0

    private var isAnimationPlayed = false

    private var mUrl: String? = null

    private lateinit var mRealm: Realm

    @InjectPresenter(type = PresenterType.GLOBAL)
    lateinit var mArticlePresenter: ArticlePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        themeSetup()

        // Fix for Circular Reveal animation on Pre-Lollipop
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.article)
        Realm.init(this)
        mRealm = Realm.getDefaultInstance()
        getSharedPreferences()

        extras = intent.extras

        mUrl = if (extras != null && extras!!.getString(Utils.EXTRA_ARTICLE_URL) != null)
            extras!!.getString(Utils.EXTRA_ARTICLE_URL)
        else
            intent.data.toString()

        viewInitialisation()

        toolbarSetup()
        ArticleParserSetup()
        backgroundTintColorSetup()
        appbarArticle.addOnOffsetChangedListener(this)

        this.calculateMinimumHeight()
        this.setupArticleWebView(article)
    }

    private fun getSharedPreferences() {
        hasBlur = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("beta_enableBlur", false)
        isPalette = PreferenceManager.getDefaultSharedPreferences(this)
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
        val extras = intent.extras
        var touchXCoordinate: Float
        var touchYCoordinate: Float
        try {
            touchXCoordinate = extras.getFloat(Utils.EXTRA_ARTICLE_X_TOUCH_COORDINATE, 0f)
            touchYCoordinate = extras.getFloat(Utils.EXTRA_ARTICLE_Y_TOUCH_COORDINATE, 0f)
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
            touchXCoordinate = 0f
            touchYCoordinate = 0f
        }

        val animator = ViewAnimationUtils
                .createCircularReveal(animatedView, touchXCoordinate.toInt(), touchYCoordinate.toInt(),
                        0f, Utils.CIRCULAR_REVIVAL_ANIMATION_RADIUS.toFloat())
        animator.interpolator = AccelerateInterpolator()
        animator.duration = 320
        animator.start()
    }

    private fun backgroundTintColorSetup() {
        if (DroiderBaseActivity.Companion.activeTheme != R.style.AdaptiveTheme) {
            articleBackgroundTintView.background = AppCompatResources.getDrawable(this,
                    if (DroiderBaseActivity.Companion.activeTheme == R.style.RedTheme)
                        R.drawable.article_background_tint_light
                    else
                        R.drawable.article_background_tint_dark)
        } else {
            val tintDrawable = AppCompatResources
                    .getDrawable(this, R.drawable.article_background_tint_dark)
            tintDrawable?.setColorFilter(
                    getThemeAttribute(android.R.attr.colorBackground, DroiderBaseActivity.Companion.activeTheme),
                    PorterDuff.Mode.SRC_ATOP)
            articleBackgroundTintView.background = tintDrawable
        }
    }


    override fun themeSetup() {
        super.themeSetup()
        currentNightMode = resources.configuration.uiMode
        webViewBackgroundColor = getThemeAttribute(android.R.attr.colorForegroundInverse,
                DroiderBaseActivity.Companion.activeTheme)
        webViewTextColor = "#" + Integer
                .toHexString(getThemeAttribute(android.R.attr.textColorPrimary, DroiderBaseActivity.Companion.activeTheme))
                .substring(2)
        webViewLinkColor = "#" + Integer
                .toHexString(getThemeAttribute(R.attr.colorPrimary, DroiderBaseActivity.Companion.activeTheme)).substring(2)

        if (DroiderBaseActivity.Companion.activeTheme == R.style.RedTheme) {
            webViewTableColor = "#F5F5F5"
            webViewTableHeaderColor = "#EEEEEE"
        } else {
            webViewTableHeaderColor = "#212121"
            webViewTableColor = "#616161"
        }

        Log.d(TAG, "themeSetup: bg color: " + webViewBackgroundColor)
        Log.d(TAG, "themeSetup: webViewTextColor color: " + webViewTextColor)
        Log.d(TAG, "themeSetup: webViewLinkColor color: " + webViewLinkColor)
    }

    private fun ArticleParserSetup() {
        /** Проверка как мы попали в статью  */
        intentExtraChecking()
    }

    private fun intentExtraChecking() {
        if (Intent.ACTION_VIEW == intent.action) {
            mArticlePresenter.provideData(intent.data.toString(), setupArticleModel())
            mArticlePresenter.getPostDataForOutsideEvent()

        } else {
            Log.d(TAG, "intentExtraChecking: inner")
            mArticlePresenter.provideData(mUrl!!, setupArticleModel())

            articleHeader.text = extras?.getString(Utils.EXTRA_ARTICLE_TITLE)
            articleShortDescription.text = extras?.getString(Utils.EXTRA_SHORT_DESCRIPTION)

            articleHeaderImg.setImageURI(extras?.getString(Utils.EXTRA_ARTICLE_IMG_URL))
        }

        mArticlePresenter.parseArticle(mRealm)

        //            new Handler().postDelayed(() -> {
        //                if (ArticleParser.isYoutube()) {
        //                    /** а зачем зря тратить память как говорится, поэтому находим этот фрагмент только когда он точно нужен **/
        //                    youtubeFrame = (FrameLayout) findViewById(R.id.YouTubeFrame);
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

        setSupportActionBar(toolbarArticle)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.title = ""
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)

            toolbarArticle.setNavigationOnClickListener { finish() }
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
        supportFragmentManager.beginTransaction().replace(R.id.YouTubeFrame, youtubeFragment)
                .commit()
    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (Math.abs(verticalOffset) >= appBarLayout.bottom) {
            setupPaletteBackground(false)
        } else {
            assert(supportActionBar != null)
            collapsingToolbar.title = ""
            assert(actionBar != null)
            setupPaletteBackground(true)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        menuInflater.inflate(R.menu.menu_article, menu)

        menu.getItem(0).icon = ResourcesCompat
                .getDrawable(resources, R.drawable.ic_open_in_browser_white_24dp, null)
        menu.getItem(1).icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_share_white_24dp, null)

        return super.onCreateOptionsMenu(menu)
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
                    supportFragmentManager.beginTransaction()
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
        this.windowManager.defaultDisplay.getMetrics(metrics)
        val screenHeight = metrics.heightPixels
        var actionBarHeight = 0
        val tv = TypedValue()

        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue
                    .complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
        articleRelLayout.minimumHeight = screenHeight - actionBarHeight
    }

    private fun setupPaletteBackground(isTransparent: Boolean) {
        if (isPalette && (DroiderBaseActivity.Companion.activeTheme == R.style.RedTheme || currentNightMode == Configuration.UI_MODE_NIGHT_NO)) {
            try {

                val b: Bitmap = drawableToBitmap(articleHeaderImg.drawable)
                var p: Palette
                Utils.convertImageUrlToBitmap(extras?.getString(Utils.EXTRA_ARTICLE_IMG_URL)!!, this, object : BitmapLoaded {
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
        similarArticles.layoutManager = LinearLayoutManager(this@ArticleActivity, LinearLayoutManager.HORIZONTAL,
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

        private val TAG = "ArticleActivity"

        private val YOUTUBE_API_KEY = "AIzaSyBl-6eQJ9SgBSznqnQV6ts_5MZ88o31sl4"
    }
}