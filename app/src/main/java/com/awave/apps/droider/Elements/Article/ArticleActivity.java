package com.awave.apps.droider.Elements.Article;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.awave.apps.droider.Main.AdapterMain;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Utils.Article.ImageParser;
import com.awave.apps.droider.Utils.Utils.Blur;
import com.awave.apps.droider.Utils.Utils.DeveloperKey;
import com.awave.apps.droider.Utils.Utils.Helper;
import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class ArticleActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "ArticleActivity";

    private static Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    private ShareActionProvider mShareActionProvider;
    private Intent share = getIntent();
    private FrameLayout headerImage;
    private LinearLayout articleRelLayout;
    private static ArrayList<String> youTubeLink = new ArrayList<>();
    private TextView articleHeader;
    private static TextView article;
    private TextView articleShortDescription;
    private static YouTubePlayerSupportFragment youtubeFragment;
    private static DisplayMetrics metrics;
    private static Display display;
    private static ImageParser imageParser;
    private static FrameLayout  youtubeFrame;
    private String title;
    private String shortDescr;
    private int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String themeName = PreferenceManager.getDefaultSharedPreferences(this).getString("theme", "Тёмная");
        if(themeName.equals("Светлая"))
            theme = R.style.LightTheme;
        else
            theme = R.style.DarkTheme;
        //supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setTheme(theme);
        setContentView(R.layout.article);

        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar_article);
        appBarLayout.addOnOffsetChangedListener(this);
        youtubeFrame = (FrameLayout)findViewById(R.id.YouTubeFrame);
        articleRelLayout = (LinearLayout) findViewById(R.id.articleRelLayout);
        Bundle extras = getIntent().getExtras();
        title = extras.getString(Helper.EXTRA_ARTICLE_TITLE);
        shortDescr = extras.getString(Helper.EXTRA_SHORT_DESCRIPTION);

        collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar_article);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        articleHeader = (TextView) findViewById(R.id.article_header);
        articleHeader.setText(title);
        articleShortDescription = (TextView) findViewById(R.id.article_shortDescription);
        articleShortDescription.setText(shortDescr);

        display = getWindowManager().getDefaultDisplay();
        metrics = new DisplayMetrics(); // for ImageParser
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        articleRelLayout.setMinimumHeight(screenHeight - actionBarHeight);

        article = (TextView) findViewById(R.id.article);

        imageParser = new ImageParser(article, getResources(), this, metrics);

        headerImage = (FrameLayout) findViewById(R.id.article_header_content);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        headerImage.setBackgroundDrawable(AdapterMain.getHeaderImage());
        else
        headerImage.setBackground(AdapterMain.getHeaderImage());
//        if (!AdapterMain.getHeadImage().contains("youtube")){
//            new Blur.AsyncBlurImage(headerImage, this).execute(AdapterMain.getHeadImage());
//        }


        if(AdapterMain.getHeadImage().contains("youtube")) {
            youtubeFrame.setVisibility(View.VISIBLE);
            youtubeFragment = YouTubePlayerSupportFragment.newInstance();
            youtubeFragment.initialize(DeveloperKey.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasResumed) {
                   synchronized (youtubeFragment)
                   {
                       try {
                           youtubeFragment.wait(500);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }
                    youTubePlayer.cueVideo(Parser.YouTubeVideoURL);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    Log.d(TAG, "onInitializationFailure: ");

                }
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.YouTubeFrame, youtubeFragment).commit();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) >= appBarLayout.getBottom()) {
            collapsingToolbar.setTitle(title);

        }
        else {
            assert getSupportActionBar() != null;
            collapsingToolbar.setTitle("");
            assert getActionBar() != null;
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        youTubeLink.clear();
        super.onStop();
    }

    public static class Parser extends AsyncTask<String, String, String>{
        private String html = "";
        private static String YouTubeVideoURL;

        @Override
        protected void onPreExecute() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                 // ждём
                }
            },500);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(strings[0]).get();
                Elements elements = document.select("div.entry p");

                Elements entry = document.select("div.entry");

                for (Element youtube : entry) {
                    youTubeLink.add(youtube.getElementsByTag("iframe").attr("src"));
                        if (youTubeLink.get(youtube.getAllElements().indexOf(youtube)).contains("youtube")) {
                            Log.d(TAG, "doInBackground: was here");
                            YouTubeVideoURL = youTubeLink.get(0);
                            YouTubeVideoURL = Helper.trimYoutubeId(YouTubeVideoURL);
                        }
                }

                Log.d(TAG, "Youtube Video: " + youTubeLink.get(0));
                Log.d(TAG, "Youtube Video ID: " +YouTubeVideoURL);

                elements.remove(0);
                elements.remove(1);
                Log.d(TAG, "doInBackground: html = " + elements.toString());

                html = elements.toString();
            }
            catch (IOException e){
                Log.d(TAG, "Failed");
            }
            return "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            try {
                SpannableString spannableString = new SpannableString(Html.fromHtml(html, imageParser, null));
                article.setText(Helper.trimWhiteSpace(spannableString));
                article.setMovementMethod(LinkMovementMethod.getInstance()); // Handles hyperlink clicks
            } catch (StringIndexOutOfBoundsException stoobe)
            {
                stoobe.printStackTrace();
            }
        }
    }

    public void restoreActionBar() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        restoreActionBar();
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        // Set up ShareActionProvider's default share intent
//        switch ()

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_share:
                mShareActionProvider.setShareIntent(shareIntent());
                shareIntent();
                break;
            case R.id.action_open_in_browser:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AdapterMain.getShareUrl())));
                break;
        }
        return true;
    }

    private Intent shareIntent() {
        share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, AdapterMain.getShareTitle() + ":  " + AdapterMain.getShareUrl());
        share.setType("text/plain");
        return share;
    }

}