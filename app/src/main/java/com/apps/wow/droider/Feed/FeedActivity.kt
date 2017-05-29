package com.apps.wow.droider.Feed

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.apps.wow.droider.Adapters.NotifyService
import com.apps.wow.droider.DroiderBaseActivity
import com.apps.wow.droider.NavDrawScreens.AboutFragment
import com.apps.wow.droider.NavDrawScreens.Preferences
import com.apps.wow.droider.R
import com.apps.wow.droider.Utils.Utils
import com.apps.wow.droider.databinding.ActivityFeedBinding

class FeedActivity : DroiderBaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = FeedActivity::class.java.simpleName

    var binding: ActivityFeedBinding? = null

    private var activeFeedTitle: String? = null
    private val mTitle = "Главная"
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        themeSetup()
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityFeedBinding>(this, R.layout.activity_feed)
        setupView()
    }

    fun setupView() {
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
        startService(Intent(this, NotifyService::class.java))
        super.onStart()
    }

    override fun onStop() {
        stopService(Intent(this, NotifyService::class.java))
        super.onStop()
    }

    override fun onBackPressed() {
        assert(supportActionBar != null)
        supportActionBar!!.title = activeFeedTitle
        if (binding?.navDrawer!!.isDrawerOpen(GravityCompat.START)) {
            binding?.navDrawer?.closeDrawer(GravityCompat.START)
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
            fragmentManager.beginTransaction()
                    .replace(R.id.container_main, FeedFragment.newInstance(Utils.CATEGORY_MAIN, Utils.SLUG_MAIN))
                    .commit()
        }
    }

    private fun calculateCircularRevealAnimationRadius() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        Utils.CIRCULAR_REVIVAL_ANIMATION_RADIUS = Math.max(width, height)
    }

    private fun navigationDrawerSetup() {
        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding!!.navDrawer, binding!!.toolbar, R.string.drawer_open,
                R.string.drawer_close)
        binding!!.navDrawer.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        binding!!.navDrawer.setBackgroundColor(getThemeAttribute(R.attr.colorPrimary, DroiderBaseActivity.Companion.activeTheme))
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var fragment: android.app.Fragment? = null
        val fragmentTransaction = fragmentManager.beginTransaction()
        var isBackStackNeeded = false
        assert(supportActionBar != null)

        when (menuItem.itemId) {
            R.id.home_page_tab -> {
                fragment = FeedFragment.newInstance(Utils.CATEGORY_MAIN, Utils.SLUG_MAIN)
                supportActionBar!!.title = getString(R.string.drawer_item_home)
                activeFeedTitle = getString(R.string.drawer_item_home)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.android_tab -> {
                fragment = FeedFragment.newInstance(Utils.CATEGORY_ANDROID, Utils.SLUG_ANDROID)
                supportActionBar!!.title = getString(R.string.drawer_item_android)
                activeFeedTitle = getString(R.string.drawer_item_android)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.apple_tab -> {
                fragment = FeedFragment.newInstance(Utils.CATEGORY_APPLE, Utils.SLUG_APPLE)
                supportActionBar!!.title = getString(R.string.drawer_item_apple)
                activeFeedTitle = getString(R.string.drawer_item_apple)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.gadgets_tab -> {
                fragment = FeedFragment.newInstance(Utils.CATEGORY_GAGETS, Utils.SLUG_GAGETS)
                supportActionBar!!.title = getString(R.string.drawer_item_gadgets)
                activeFeedTitle = getString(R.string.drawer_item_gadgets)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }

            R.id.video_tab -> {
                fragment = FeedFragment.newInstance(Utils.CATEGORY_VIDEO, Utils.SLUG_VIDEO)
                supportActionBar!!.title = getString(R.string.drawer_item_video)
                activeFeedTitle = getString(R.string.drawer_item_video)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.games_tab -> {
                fragment = FeedFragment.newInstance(Utils.CATEGORY_NEW_GAMES, Utils.SLUG_NEW_GAMES)
                supportActionBar!!.title = getString(R.string.drawer_item_games)
                activeFeedTitle = getString(R.string.drawer_item_games)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.welcome_to_the_internet_tab -> {
                fragment = FeedFragment.newInstance(Utils.CATEGORY_FROM_INTERNET, Utils.SLUG_FROM_INTERNET)
                supportActionBar!!.title = getString(R.string.drawer_item_internet)
                activeFeedTitle = getString(R.string.drawer_item_internet)
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this)
            }
            R.id.droider_cast_tab -> {
                fragment = FeedFragment.newInstance(Utils.CATEGORY_PODCAST, Utils.SLUG_PODCAST)
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
            binding!!.popularNews.visibility = View.GONE
        else
            binding!!.popularNews.visibility = View.VISIBLE

        if (fragment != null) {
            fragmentTransaction.setCustomAnimations(R.animator.frag_in, R.animator.frag_out)
            if (isBackStackNeeded) {
                fragmentTransaction.addToBackStack(fragment.tag)
                isBackStackNeeded = false
            }
            fragmentTransaction.replace(R.id.container_main, fragment).commit()
        }

        binding!!.navDrawer.closeDrawer(GravityCompat.START)
        return super.onOptionsItemSelected(menuItem)
    }

    private fun toolbarSetup() {
        binding!!.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.text_color_toolbar_red))
        setSupportActionBar(binding!!.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.title = mTitle
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
        activeFeedTitle = getString(R.string.drawer_item_home)
        binding!!.popularNews.overScrollMode = View.OVER_SCROLL_NEVER
    }

    fun restoreActionBar() {
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding!!.toolbar.title = mTitle
    }

    override fun onDestroy() {
        binding!!.navDrawer.removeDrawerListener(actionBarDrawerToggle!!)
        super.onDestroy()
    }
}