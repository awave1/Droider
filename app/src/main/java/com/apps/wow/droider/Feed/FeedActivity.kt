package com.apps.wow.droider.Feed

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.apps.wow.droider.Adapters.ArticleSimilarAdapter
import com.apps.wow.droider.DroiderBaseActivity
import com.apps.wow.droider.Model.FeedModel
import com.apps.wow.droider.NavDrawScreens.AboutFragment
import com.apps.wow.droider.NavDrawScreens.NotifyService
import com.apps.wow.droider.NavDrawScreens.Preferences
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Const
import com.apps.wow.droider.Utils.Utils
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : DroiderBaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = FeedActivity::class.java.simpleName
    private var activeFeedTitle: String? = null
    private val mTitle = "Главная"
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        themeSetup()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        setupView()
    }

    private fun setupView() {
        toolbarSetup()
        navigationDrawerSetup()
        fragmentSetting()
        calculateCircularRevealAnimationRadius()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        restoreActionBar()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notify", false))
            startService(Intent(this, NotifyService::class.java))
    }

    override fun onStop() {
        super.onStop()
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notify", false))
            stopService(Intent(this, NotifyService::class.java))
    }

    override fun onBackPressed() {
        assert(supportActionBar != null)
        supportActionBar!!.title = activeFeedTitle
        if (navDrawer.isDrawerOpen(GravityCompat.START)) {
            navDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun fragmentSetting() {
        Log.d(TAG, "onCreate: isOnline = " + Utils.isOnline(this))
        if (!Utils.isOnline(this)) {
            initInternetConnectionDialog(this)
        } else {
            if (supportActionBar != null) {
                supportActionBar!!.title = getString(R.string.drawer_item_home)
                activeFeedTitle = getString(R.string.drawer_item_home)
            }

            if (supportFragmentManager.findFragmentById(R.id.containerMain) == null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.containerMain, FeedFragment.newInstance(Const.CATEGORY_MAIN, Const.SLUG_MAIN))
                        .commit()
            }
        }
    }

    private fun calculateCircularRevealAnimationRadius() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        Const.CIRCULAR_REVIVAL_ANIMATION_RADIUS = Math.max(width, height)
    }

    private fun navigationDrawerSetup() {
        actionBarDrawerToggle = ActionBarDrawerToggle(this, navDrawer, toolbar, R.string.drawer_open,
                R.string.drawer_close)
        navDrawer.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        navView.setNavigationItemSelectedListener(this)

        navDrawer.setBackgroundColor(getThemeAttribute(R.attr.colorPrimary, DroiderBaseActivity.Companion.activeTheme))
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var fragment: android.app.Fragment? = null
        val fragmentTransaction = fragmentManager.beginTransaction()
        var isBackStackNeeded = false
        assert(supportActionBar != null)

        when (menuItem.itemId) {
            R.id.home_page_tab -> {
                fragment = FeedFragment.newInstance(Const.CATEGORY_MAIN, Const.SLUG_MAIN)
                supportActionBar!!.title = getString(R.string.drawer_item_home)
                activeFeedTitle = getString(R.string.drawer_item_home)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.android_tab -> {
                fragment = FeedFragment.newInstance(Const.CATEGORY_ANDROID, Const.SLUG_ANDROID)
                supportActionBar!!.title = getString(R.string.drawer_item_android)
                activeFeedTitle = getString(R.string.drawer_item_android)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.apple_tab -> {
                fragment = FeedFragment.newInstance(Const.CATEGORY_APPLE, Const.SLUG_APPLE)
                supportActionBar!!.title = getString(R.string.drawer_item_apple)
                activeFeedTitle = getString(R.string.drawer_item_apple)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.gadgets_tab -> {
                fragment = FeedFragment.newInstance(Const.CATEGORY_GAGETS, Const.SLUG_GAGETS)
                supportActionBar!!.title = getString(R.string.drawer_item_gadgets)
                activeFeedTitle = getString(R.string.drawer_item_gadgets)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }

            R.id.video_tab -> {
                fragment = FeedFragment.newInstance(Const.CATEGORY_VIDEO, Const.SLUG_VIDEO)
                supportActionBar!!.title = getString(R.string.drawer_item_video)
                activeFeedTitle = getString(R.string.drawer_item_video)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.games_tab -> {
                fragment = FeedFragment.newInstance(Const.CATEGORY_NEW_GAMES, Const.SLUG_NEW_GAMES)
                supportActionBar!!.title = getString(R.string.drawer_item_games)
                activeFeedTitle = getString(R.string.drawer_item_games)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.welcome_to_the_internet_tab -> {
                fragment = FeedFragment.newInstance(Const.CATEGORY_FROM_INTERNET, Const.SLUG_FROM_INTERNET)
                supportActionBar!!.title = getString(R.string.drawer_item_internet)
                activeFeedTitle = getString(R.string.drawer_item_internet)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.droider_cast_tab -> {
                fragment = FeedFragment.newInstance(Const.CATEGORY_PODCAST, Const.SLUG_PODCAST)
                supportActionBar!!.title = getString(R.string.drawer_item_dr_cast)
                activeFeedTitle = getString(R.string.drawer_item_dr_cast)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }

            R.id.settings_tab -> {
                fragment = Preferences()
                supportActionBar!!.setTitle(R.string.drawer_item_settings)
                isBackStackNeeded = true
            }
            R.id.info_tab -> {
                fragment = AboutFragment()
                supportActionBar!!.title = getString(R.string.drawer_item_about)
                isBackStackNeeded = true
            }
        }

        if (menuItem.itemId != R.id.home_page_tab)
            popularNews.visibility = View.GONE
        else
            popularNews.visibility = View.VISIBLE

        if (fragment != null) {
            fragmentTransaction.setCustomAnimations(R.animator.frag_in, R.animator.frag_out)
            if (isBackStackNeeded) {
                fragmentTransaction.addToBackStack(fragment.tag)
            }
            fragmentTransaction.replace(R.id.containerMain, fragment).commit()
        }

        navDrawer.closeDrawer(GravityCompat.START)
        return super.onOptionsItemSelected(menuItem)
    }

    private fun toolbarSetup() {
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.text_color_toolbar_red))
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.title = mTitle
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        activeFeedTitle = getString(R.string.drawer_item_home)
        popularNews.overScrollMode = View.OVER_SCROLL_NEVER
    }

    private fun restoreActionBar() {
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = mTitle
    }

    fun setupPopularArticles(model: FeedModel) {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        //грязь - перенести из активити сюда, во фрагмент
        popularNews?.layoutManager = layoutManager
        popularNews?.adapter = ArticleSimilarAdapter(model.posts)

        try {
            snapHelper.attachToRecyclerView(popularNews)
        } catch (ise: IllegalStateException) {
            ise.printStackTrace()
        }

    }

    override fun onDestroy() {
        navDrawer.removeDrawerListener(actionBarDrawerToggle!!)
        super.onDestroy()
    }
}