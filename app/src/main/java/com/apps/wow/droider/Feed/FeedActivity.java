package com.apps.wow.droider.Feed;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.apps.wow.droider.Adapters.NotifyService;
import com.apps.wow.droider.DroiderBaseActivity;
import com.apps.wow.droider.Feed.View.FeedFragment;
import com.apps.wow.droider.MainScreen.AboutFragment;
import com.apps.wow.droider.MainScreen.Preferences;
import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.databinding.ActivityFeedBinding;

public class FeedActivity extends DroiderBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = FeedActivity.class.getSimpleName();

    private ActivityFeedBinding binding;
    private DrawerLayout drawerLayout;
    private String activeFeedTitle;
    private Toolbar toolbar;
    private String mTitle = "Главная";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeSetup();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed);
        setupView();
    }

    public void setupView() {
        // TODO: 18.08.2016 Font size on screen orientation changing
        toolbarSetup();
        navigationDrawerSetup();
        fragmentSetting();
        calculateCircularRevealAnimationRadius();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        startService(new Intent(this, NotifyService.class));
        super.onStart();
    }

    @Override
    protected void onStop() {
        stopService(new Intent(this, NotifyService.class));
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(activeFeedTitle);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void fragmentSetting() {
        Log.d(TAG, "onCreate: isOnline = " + Utils.isOnline(this));
        if (!Utils.isOnline(this)) {
            initInternetConnectionDialog(this);
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container_main, FeedFragment.newInstance(Utils.HOME_URL))
                    .commit();
        }
    }

    private void calculateCircularRevealAnimationRadius() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Utils.CIRCULAR_REVIVAL_ANIMATION_RADIUS = Math.max(width, height);
    }

    private void navigationDrawerSetup() {
        drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                        R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout.setBackgroundColor(getThemeAttribute(R.attr.colorPrimary, activeTheme));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        android.app.Fragment fragment = null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        boolean isBackStackNeeded = false;
        assert getSupportActionBar() != null;

        switch (menuItem.getItemId()) {
            case R.id.home_page_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_MAIN);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_home));
                activeFeedTitle = getString(R.string.drawer_item_home);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.android_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_ANDROID);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_android));
                activeFeedTitle = getString(R.string.drawer_item_android);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.apple_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_APPLE);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_apple));
                activeFeedTitle = getString(R.string.drawer_item_apple);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.gadgets_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_GAGETS);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_gadgets));
                activeFeedTitle = getString(R.string.drawer_item_gadgets);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;

            case R.id.video_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_VIDEO);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_video));
                activeFeedTitle = getString(R.string.drawer_item_video);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.games_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_NEW_GAMES);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_games));
                activeFeedTitle = getString(R.string.drawer_item_games);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.welcome_to_the_internet_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_FROM_INTERNET);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_internet));
                activeFeedTitle = getString(R.string.drawer_item_internet);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;

            case R.id.settings_tab:
                fragment = new Preferences();
                getSupportActionBar().setTitle(R.string.drawer_item_settings);
                isBackStackNeeded = true;
                break;
            case R.id.info_tab:
                fragment = new AboutFragment();
                getSupportActionBar().setTitle(getString(R.string.drawer_item_about));
                isBackStackNeeded = true;
                break;
        }

        //        switch (menuItem.getItemId()) {
        //            case R.id.home_page_tab:
        //                fragment = FeedFragment.newInstance(Utils.CATEGORY_MAIN);
        //                getSupportActionBar().setTitle(getString(R.string.drawer_item_home));
        //                activeFeedTitle = getString(R.string.drawer_item_home);
        //                if (!Utils.isOnline(this))
        //                    initInternetConnectionDialog(this);
        //                break;
        //            case R.id.news_tab:
        //                fragment = FeedFragment.newInstance(Utils.CATEGORY_MAIN);
        //                getSupportActionBar().setTitle(getString(R.string.drawer_item_news));
        //                activeFeedTitle = getString(R.string.drawer_item_news);
        //                if (!Utils.isOnline(this))
        //                    initInternetConnectionDialog(this);
        //                break;
        //            case R.id.apps_tab:
        //                fragment = FeedFragment.newInstance(Utils.CATEGORY_MAIN);
        //                getSupportActionBar().setTitle(getString(R.string.drawer_item_apps));
        //                activeFeedTitle = getString(R.string.drawer_item_apps);
        //                if (!Utils.isOnline(this))
        //                    initInternetConnectionDialog(this);
        //                break;
        //            case R.id.games_tab:
        //                fragment = FeedFragment.newInstance(Utils.CATEGORY_MAIN);
        //                getSupportActionBar().setTitle(getString(R.string.drawer_item_games));
        //                activeFeedTitle = getString(R.string.drawer_item_games);
        //                if (!Utils.isOnline(this))
        //                    initInternetConnectionDialog(this);
        //                break;
        //            case R.id.video_tab:
        //                fragment = FeedFragment.newInstance(Utils.CATEGORY_MAIN);
        //                getSupportActionBar().setTitle(getString(R.string.drawer_item_videos));
        //                activeFeedTitle = getString(R.string.drawer_item_videos);
        //                if (!Utils.isOnline(this))
        //                    initInternetConnectionDialog(this);
        //                break;
        //            case R.id.droider_cast_tab:
        //                fragment = FeedFragment.newInstance(Utils.CATEGORY_MAIN);
        //                getSupportActionBar().setTitle(getString(R.string.drawer_item_drcast));
        //                activeFeedTitle = getString(R.string.drawer_item_drcast);
        //                if (!Utils.isOnline(this))
        //                    initInternetConnectionDialog(this);
        //                break;
        //

        //        }

        if (fragment != null) {
            fragmentTransaction.setCustomAnimations(R.animator.frag_in, R.animator.frag_out);
            if (isBackStackNeeded) {
                fragmentTransaction.addToBackStack(fragment.getTag());
                isBackStackNeeded = false;
            }
            fragmentTransaction.replace(R.id.container_main, fragment).commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(menuItem);
    }

    private void toolbarSetup() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color_toolbar_red));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(mTitle);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        activeFeedTitle = getString(R.string.drawer_item_home);
    }

    public void restoreActionBar() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(mTitle);
    }
}