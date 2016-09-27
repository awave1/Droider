package com.awave.apps.droider.Main;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.awave.apps.droider.DroiderBaseActivity;
import com.awave.apps.droider.Elements.MainScreen.AboutFragment;
import com.awave.apps.droider.Elements.MainScreen.Feed;
import com.awave.apps.droider.Elements.MainScreen.Preferences;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Helper;


@SuppressWarnings("ALL")
public class MainActivity extends DroiderBaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private String mTitle = "Главная";
    private int theme;
    private String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawerLayout;
    private String activeFeedTitle;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        themeSetup();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // TODO: 18.08.2016 Font size on screen orientation changing
        toolbarSetup();
        navigationDrawerSetup();

        fragmentSetting();

        // TODO: 18.08.2016 For what?
        nightModeDebug();
        calculateCircularRevealAnimationRadius();
    }

    private void calculateCircularRevealAnimationRadius() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Helper.CIRCULAR_REVIVAL_ANIMATION_RADIUS = Math.max(width, height);
    }

    private void nightModeDebug() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                Log.d(TAG, "onCreate: Night mode is not active, we're in day time ");
                break;

            case Configuration.UI_MODE_NIGHT_YES:
                Log.d(TAG, "onCreate: Night mode is active, we're at night! ");
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:

                Log.d(TAG, "onCreate: We don't know what mode we're in, assume notnight ");
                break;
        }
    }

    private void fragmentSetting() {
        Log.d(TAG, "onCreate: isOnline = " + Helper.isOnline(this));
        if (!Helper.isOnline(this))
            Helper.initInternetConnectionDialog(this);
        else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container_main, Feed.instance(Helper.HOME_URL))
                    .commit();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void navigationDrawerSetup() {
        drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout.setBackgroundColor(getThemeAttribute(R.attr.colorPrimary, activeTheme));
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

    @Override
    protected void onStart() {
        stopService(new Intent(this, NotifyService.class));
        super.onStart();
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.apps.wow.droider.main/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onStop() {
        startService(new Intent(this, NotifyService.class));
        super.onStop();
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.apps.wow.droider.main/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.disconnect();
    }

    public void restoreActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        restoreActionBar();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        getSupportActionBar().setTitle(activeFeedTitle);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        android.app.Fragment fragment = null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        boolean isBackStackNeeded = false;

        switch (menuItem.getItemId()) {

            case R.id.home_page_tab:
                fragment = Feed.instance(Helper.HOME_URL);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_home));
                activeFeedTitle = getString(R.string.drawer_item_home);
                if (!Helper.isOnline(this))
                    Helper.initInternetConnectionDialog(this);
                break;
            case R.id.news_tab:
                fragment = Feed.instance(Helper.NEWS_URL);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_news));
                activeFeedTitle = getString(R.string.drawer_item_news);
                if (!Helper.isOnline(this))
                    Helper.initInternetConnectionDialog(this);
                break;
            case R.id.apps_tab:
                fragment = Feed.instance(Helper.APPS_URL);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_apps));
                activeFeedTitle = getString(R.string.drawer_item_apps);
                if (!Helper.isOnline(this))
                    Helper.initInternetConnectionDialog(this);
                break;
            case R.id.games_tab:
                fragment = Feed.instance(Helper.GAMES_URL);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_games));
                activeFeedTitle = getString(R.string.drawer_item_games);
                if (!Helper.isOnline(this))
                    Helper.initInternetConnectionDialog(this);
                break;
            case R.id.video_tab:
                fragment = Feed.instance(Helper.VIDEOS_URL);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_videos));
                activeFeedTitle = getString(R.string.drawer_item_videos);
                if (!Helper.isOnline(this))
                    Helper.initInternetConnectionDialog(this);
                break;
            case R.id.droider_cast_tab:
                fragment = Feed.instance(Helper.DROIDER_CAST_URL);
                getSupportActionBar().setTitle(getString(R.string.drawer_item_drcast));
                activeFeedTitle = getString(R.string.drawer_item_drcast);
                if (!Helper.isOnline(this))
                    Helper.initInternetConnectionDialog(this);
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

        if (fragment != null) {
            fragmentTransaction.setCustomAnimations(
                    android.R.animator.fade_in, android.R.animator.fade_out);
            if (isBackStackNeeded) {
                fragmentTransaction.addToBackStack(fragment.getTag());
                isBackStackNeeded = false;
            }
            fragmentTransaction.replace(R.id.container_main, fragment).commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(menuItem);
    }
}