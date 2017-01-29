package com.apps.wow.droider.Feed;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.apps.wow.droider.Adapters.NotifyService;
import com.apps.wow.droider.DroiderBaseActivity;
import com.apps.wow.droider.NavDrawScreens.AboutFragment;
import com.apps.wow.droider.NavDrawScreens.Preferences;
import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.databinding.ActivityFeedBinding;

public class FeedActivity extends DroiderBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = FeedActivity.class.getSimpleName();

    protected ActivityFeedBinding binding;
    private String activeFeedTitle;
    private String mTitle = "Главная";
    private ActionBarDrawerToggle actionBarDrawerToggle;

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
        if (binding.navDrawer.isDrawerOpen(GravityCompat.START)) {
            binding.navDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void fragmentSetting() {
        Log.d(TAG, "onCreate: isOnline = " + Utils.isOnline(this));
        if (!Utils.isOnline(this)) {
            initInternetConnectionDialog(this);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.drawer_item_home));
                activeFeedTitle = getString(R.string.drawer_item_home);
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.container_main, FeedFragment.newInstance(Utils.CATEGORY_MAIN, Utils.SLUG_MAIN))
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
        actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, binding.navDrawer, binding.toolbar, R.string.drawer_open,
                        R.string.drawer_close);
        binding.navDrawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        binding.navDrawer.setBackgroundColor(getThemeAttribute(R.attr.colorPrimary, activeTheme));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        android.app.Fragment fragment = null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        boolean isBackStackNeeded = false;
        assert getSupportActionBar() != null;

        switch (menuItem.getItemId()) {
            case R.id.home_page_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_MAIN, Utils.SLUG_MAIN);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_home));
                activeFeedTitle = getString(R.string.drawer_item_home);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.android_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_ANDROID, Utils.SLUG_ANDROID);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_android));
                activeFeedTitle = getString(R.string.drawer_item_android);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.apple_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_APPLE, Utils.SLUG_APPLE);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_apple));
                activeFeedTitle = getString(R.string.drawer_item_apple);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.gadgets_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_GAGETS, Utils.SLUG_GAGETS);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_gadgets));
                activeFeedTitle = getString(R.string.drawer_item_gadgets);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;

            case R.id.video_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_VIDEO, Utils.SLUG_VIDEO);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_video));
                activeFeedTitle = getString(R.string.drawer_item_video);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.games_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_NEW_GAMES, Utils.SLUG_NEW_GAMES);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_games));
                activeFeedTitle = getString(R.string.drawer_item_games);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.welcome_to_the_internet_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_FROM_INTERNET, Utils.SLUG_FROM_INTERNET);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_internet));
                activeFeedTitle = getString(R.string.drawer_item_internet);
                if (!Utils.isOnline(this)) initInternetConnectionDialog(this);
                break;
            case R.id.droider_cast_tab:
                fragment = FeedFragment.newInstance(Utils.CATEGORY_PODCAST, Utils.SLUG_PODCAST);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_dr_cast));
                activeFeedTitle = getString(R.string.drawer_item_dr_cast);
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

        if (menuItem.getItemId() != R.id.home_page_tab)
            binding.popularNews.setVisibility(View.GONE);
        else
            binding.popularNews.setVisibility(View.VISIBLE);

        if (fragment != null) {
            fragmentTransaction.setCustomAnimations(R.animator.frag_in, R.animator.frag_out);
            if (isBackStackNeeded) {
                fragmentTransaction.addToBackStack(fragment.getTag());
                isBackStackNeeded = false;
            }
            fragmentTransaction.replace(R.id.container_main, fragment).commit();
        }

        binding.navDrawer.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(menuItem);
    }

    private void toolbarSetup() {
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.text_color_toolbar_red));
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(mTitle);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        activeFeedTitle = getString(R.string.drawer_item_home);
        binding.popularNews.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void restoreActionBar() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setTitle(mTitle);
    }

    @Override
    protected void onDestroy() {
        binding.navDrawer.removeDrawerListener(actionBarDrawerToggle);
        super.onDestroy();
    }
}