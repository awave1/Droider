package com.awave.apps.droider.Elements.Article;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.awave.apps.droider.Main.AdapterMain;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Utils.Article.ImageParser;
import com.awave.apps.droider.Utils.Utils.Blur;
import com.awave.apps.droider.Utils.Utils.DeveloperKey;
import com.awave.apps.droider.Utils.Utils.Helper;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class ArticleActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private static Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    private ShareActionProvider mShareActionProvider;
    private Intent share = getIntent();
    private FrameLayout headerImage;
    private RelativeLayout articleRelLayout;
    private static NestedScrollView nestedScrollView;

    private static TextView articleHeader;
    private static TextView article;

    private static DisplayMetrics metrics;
    private static Display display;
    private static ImageParser imageParser;
    private String title;
    private static final String TAG = "ArticleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);

        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar_article);
        appBarLayout.addOnOffsetChangedListener(this);

        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        articleRelLayout = (RelativeLayout) findViewById(R.id.articleRelLayout);
        Bundle extras = getIntent().getExtras();
        title = extras.getString(Helper.EXTRA_ARTICLE_TITLE);

        collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar_article);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        articleHeader = (TextView) findViewById(R.id.article_header);
        articleHeader.setText(title);

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
        // for webview
//        article.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        article.getSettings().setDefaultTextEncodingName("utf-8");
//        article.getSettings().setUseWideViewPort(true);
//        article.setBackgroundColor(getResources().getColor(R.color.primary_bgr));


        imageParser = new ImageParser(article, getResources(), this, metrics);

        headerImage = (FrameLayout) findViewById(R.id.article_header_content);
        if (!AdapterMain.getHeadImage().contains("youtube")){
            new Blur.AsyncBlurImage(headerImage, this).execute(AdapterMain.getHeadImage());
        }
        else {
            YouTubePlayerSupportFragment youtubeFragment = YouTubePlayerSupportFragment.newInstance();
            youtubeFragment.initialize(DeveloperKey.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasResumed) {
                    youTubePlayer.cueVideo(Helper.trimYoutubeId(Helper.getYoutubeVideo()));
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.article_header_content, youtubeFragment).commit();
            Log.d(TAG, "Youtube Video: " + Helper.getYoutubeVideo());
            Log.d(TAG, "Youtube Video ID: " + Helper.trimYoutubeId(Helper.getYoutubeVideo()));
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

        }
    }

    public static class Parser extends AsyncTask<String, String, String>{
        private String html = "";

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(strings[0]).get();
                Elements elements = document.select("div.entry p");
                Elements imgs = document.select("div.entry img");
//                Elements titleDiv = document.select("div.title a");
                String qr = "http://chart.apis.google.com/chart?cht=qr&chs=150x150&chl=https://play.google.com/store/apps/details?id=";
                String video = "https://www.youtube.com/embed/";

                // todo finish parser fo webview
//                elements.attr("style", "padding-left:20dp;padding-right:20dp;color:white");
//                imgs.attr("style", "padding-left:0dp;padding-right:0dp");

                Log.d(TAG, "doInBackground: html = " + elements.toString());
                Log.d(TAG, "doInBackground: imgs = " + imgs.toString());
//                html = elements.toString().replace("<img src=\""+AdapterMain.getHeadImage()+"\">", "");

                html = elements.toString();
//                Log.d(TAG, "doInBackground: html aft = " + html);
//                if (html.contains(AdapterMain.getHeadImage())) {
//                    Log.d(TAG, "doInBackground: html has head image!");
//                }
//                else {
//                    Log.d(TAG, "doInBackground: html doesn't have head image");
//                }
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
            // TEST
//            article.loadData(Base64.encodeToString(html.getBytes(), Base64.DEFAULT), "text/html; charset=utf-8", "base64");
        }

//        private String padRight(String s, int n) {
//            return String.format("%10s", s);
//        }
//
//        private String padLeft(String s, int n) {
//            return String.format("%s", s);
//        }
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

        switch (item.getItemId())
        {
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


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}