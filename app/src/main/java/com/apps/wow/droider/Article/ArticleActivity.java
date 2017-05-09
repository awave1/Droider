package com.apps.wow.droider.Article;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import com.apps.wow.droider.Adapters.FeedAdapter;
import com.apps.wow.droider.DroiderBaseActivity;
import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.databinding.ArticleBinding;
import com.squareup.picasso.Picasso;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.codetail.animation.ViewAnimationUtils;

public class ArticleActivity extends DroiderBaseActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "ArticleActivity";

    private static final String YOUTUBE_API_KEY = "AIzaSyBl-6eQJ9SgBSznqnQV6ts_5MZ88o31sl4";

    public String webViewTextColor;

    public String webViewLinkColor;

    public String webViewTableColor;

    public String webViewTableHeaderColor;

    public boolean hasBlur;

    public ArticleBinding binding;

    private String articleTitle;

    private String shortDescription;

    private FrameLayout youtubeFrame;

    private boolean isPalette;

    private Bundle extras;

    private int webViewBackgroundColor;

    private int currentNightMode;

    private boolean isAnimationPlayed = false;

    private ArticleParser ArticleParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeSetup();

        // Fix for Circular Reveal animation on Pre-Lollipop
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        super.onCreate(savedInstanceState);
        getSharedPreferences();

        binding = DataBindingUtil.setContentView(ArticleActivity.this, R.layout.article);

        ArticleParser = new ArticleParser(this);

        viewInitialisation();

        toolbarSetup();
        ArticleParserSetup();
        backgroundTintColorSetup();
        binding.appbarArticle.addOnOffsetChangedListener(this);

        this.calculateMinimumHeight();
        this.setupArticleWebView(binding.article);
    }

    private void getSharedPreferences() {
        hasBlur = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("beta_enableBlur", false);
        isPalette = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("palette", false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isAnimationPlayed) {
            try {
                playActivityAnimation();
            } catch (UnsupportedOperationException | IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();
            }
            isAnimationPlayed = true;
        }
    }

    private void playActivityAnimation() {
        final View animatedView = binding.articleCoordinatorLayout;
        animatedView.post(() -> {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                createFadeAnimation(animatedView);
            } else {
                //should fix
                //Exception java.lang.IllegalStateException: Cannot start this animator on a detached view!
                if (ViewCompat.isAttachedToWindow(animatedView)) {
                    createCircularRevealAnimation(animatedView);
                }
            }
        });
    }

    private void createFadeAnimation(View animatedView) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animatedView, "alpha", 0f, 1f);
        objectAnimator.setDuration(320);
        objectAnimator.start();
    }

    private void createCircularRevealAnimation(View animatedView) {
        animatedView.setVisibility(View.VISIBLE);
        Bundle extras = getIntent().getExtras();
        float touchXCoordinate = extras.getFloat(Utils.EXTRA_ARTICLE_X_TOUCH_COORDINATE, 0);
        float touchYCoordinate = extras.getFloat(Utils.EXTRA_ARTICLE_Y_TOUCH_COORDINATE, 0);
        Animator animator = ViewAnimationUtils
                .createCircularReveal(animatedView, (int) touchXCoordinate, (int) touchYCoordinate,
                        0, Utils.CIRCULAR_REVIVAL_ANIMATION_RADIUS);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(320);
        animator.start();
    }

    private void backgroundTintColorSetup() {
        View view = findViewById(R.id.article_background_tint_view);
        if (activeTheme != R.style.AdaptiveTheme) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(AppCompatResources.getDrawable(this,
                        activeTheme == R.style.RedTheme ? R.drawable.article_background_tint_light
                                : R.drawable.article_background_tint_dark));
            } else {
                view.setBackground(AppCompatResources.getDrawable(this,
                        activeTheme == R.style.RedTheme ? R.drawable.article_background_tint_light
                                : R.drawable.article_background_tint_dark));
            }
        } else {
            final Drawable tintDrawable = AppCompatResources
                    .getDrawable(this, R.drawable.article_background_tint_dark);
            if (tintDrawable != null) {
                tintDrawable.setColorFilter(
                        getThemeAttribute(android.R.attr.colorBackground, activeTheme),
                        PorterDuff.Mode.SRC_ATOP);
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(tintDrawable);
            } else {
                view.setBackground(tintDrawable);
            }
        }
    }

    private void ArticleParserSetup() {
        /** Проверка как мы попали в статью **/
        intentExtraChecking();

        getArticleHeader().setText(articleTitle);
        binding.articleShortDescription.setText(shortDescription);
    }

    @Override
    protected void themeSetup() {
        super.themeSetup();
        currentNightMode = getResources().getConfiguration().uiMode;
        webViewBackgroundColor = getThemeAttribute(android.R.attr.colorForegroundInverse,
                activeTheme);
        webViewTextColor = "#" + Integer
                .toHexString(getThemeAttribute(android.R.attr.textColorPrimary, activeTheme))
                .substring(2);
        webViewLinkColor = "#" + Integer
                .toHexString(getThemeAttribute(R.attr.colorPrimary, activeTheme)).substring(2);

        if (activeTheme == R.style.RedTheme) {
            webViewTableColor = "#F5F5F5";
            webViewTableHeaderColor = "#EEEEEE";
        } else {
            webViewTableHeaderColor = "#212121";
            webViewTableColor = "#616161";
        }

        Log.d(TAG, "themeSetup: bg color: " + webViewBackgroundColor);
        Log.d(TAG, "themeSetup: webViewTextColor color: " + webViewTextColor);
        Log.d(TAG, "themeSetup: webViewLinkColor color: " + webViewLinkColor);
    }

    private void intentExtraChecking() {
        extras = getIntent().getExtras();
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            ArticleParser.isOutIntent(true);
            String outsideUrl = getIntent().getData().toString();
            ArticleParser.execute(outsideUrl);
        } else {
            Log.d(TAG, "intentExtraChecking: inner");
//            ArticleParser.execute(extras.getString(Utils.EXTRA_ARTICLE_URL));
            new NewArticleParser().with(this).execute(extras.getString(Utils.EXTRA_ARTICLE_URL));
            articleTitle = extras.getString(Utils.EXTRA_ARTICLE_TITLE);
            shortDescription = extras.getString(Utils.EXTRA_SHORT_DESCRIPTION);
            Picasso.with(this).load(extras.getString(Utils.EXTRA_ARTICLE_IMG_URL))
                    .into(binding.articleHeaderImg);
//
//            new Handler().postDelayed(() -> {
//                if (ArticleParser.isYoutube()) {
//                    /** а зачем зря тратить память как говорится, поэтому находим этот фрагмент только когда он точно нужен **/
//                    youtubeFrame = (FrameLayout) findViewById(R.id.YouTubeFrame);
//                    setupYoutubePlayer();
//                }
//            }, 750);
//
            binding.similarArticles.setLayoutManager(
                    new LinearLayoutManager(ArticleActivity.this, LinearLayoutManager.HORIZONTAL,
                            false));
//            new Handler().postDelayed(() ->
//                   , 750);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                if (hasBlur) {
                    binding.articleHeaderContent.setBackgroundDrawable(
                            Utils.applyBlur(FeedAdapter.getHeaderImageDrawable(), this));
                } else {
                    binding.articleHeaderContent
                            .setBackgroundDrawable(FeedAdapter.getHeaderImageDrawable());
                }
            } else {
                try {
                    if (hasBlur) {
                        binding.articleHeaderContent.setBackground(
                                Utils.applyBlur(FeedAdapter.getHeaderImageDrawable(), this));
                    } else {
                        binding.articleHeaderContent
                                .setBackground(FeedAdapter.getHeaderImageDrawable());
                    }
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                    binding.articleHeaderContent
                            .setBackground(FeedAdapter.getHeaderImageDrawable());
                }

            }
        }
    }

    private void viewInitialisation() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            binding.articleCoordinatorLayout.setAlpha(0);
        } else {
            binding.articleCoordinatorLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void toolbarSetup() {

        setSupportActionBar(binding.toolbarArticle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

            binding.toolbarArticle.setNavigationOnClickListener(v -> finish());
        }
    }

    private synchronized void setupYoutubePlayer() {
        youtubeFrame.setVisibility(View.VISIBLE);
        Log.d(TAG, "setupYoutubePlayer: ");
        YouTubePlayerSupportFragment youtubeFragment = YouTubePlayerSupportFragment.newInstance();
        youtubeFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                    YouTubePlayer youTubePlayer, boolean wasResumed) {
                youTubePlayer.cueVideo(ArticleParser.getYouTubeVideoURL());
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                    YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure: ");

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.YouTubeFrame, youtubeFragment)
                .commit();
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) >= appBarLayout.getBottom()) {
            setupPaletteBackground(false);
        } else {
            assert getSupportActionBar() != null;
            binding.collapsingToolbar.setTitle("");
            assert getActionBar() != null;
            setupPaletteBackground(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.menu_article, menu);

        menu.getItem(0).setIcon(ResourcesCompat
                .getDrawable(getResources(), R.drawable.ic_open_in_browser_white_24dp, null));
        menu.getItem(1).setIcon(
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_share_white_24dp, null));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_open_in_browser:
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(extras.getString(Utils.EXTRA_ARTICLE_URL))));
                break;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        articleTitle + ":  " + extras.getString(Utils.EXTRA_ARTICLE_URL));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Отправить ссылку на статью"));
        }
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupArticleWebView(WebView w) {
        Log.d(TAG, "setupArticleWebView: ");
        w.setBackgroundColor(webViewBackgroundColor);
        WebSettings settings = w.getSettings();
        w.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: url: " + url);
                if (url.matches("(http(s?):/)(/[^/]+)+" + "\\.(?:jpg|gif|png)")) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .addToBackStack("image_prev")
                            .replace(R.id.image_preview, ImagePreviewFragment.newInstance(url))
                            .commit();
                    binding.articleBackgroundNSV.setNestedScrollingEnabled(false);
                } else {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }

                return true;
            }
        });

        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setAppCacheEnabled(true);
        settings.setSaveFormData(true);

//        w.addJavascriptInterface(new ArticleWebClient(this), "android");
    }

    private void calculateMinimumHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();

        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue
                    .complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        binding.articleRelLayout.setMinimumHeight(screenHeight - actionBarHeight);
    }

    private void setupPaletteBackground(boolean isTransparent) {
        if ((isPalette && activeTheme == R.style.RedTheme) || (isPalette
                && currentNightMode == Configuration.UI_MODE_NIGHT_NO)) {
            try {
                Palette p = new Palette.Builder(
                        Utils.drawableToBitmap(binding.articleHeaderContent.getBackground()))
                        .generate();
                if (p.getLightMutedSwatch() != null && !isTransparent) {
                    binding.toolbarArticle.setBackgroundColor(p.getLightMutedSwatch().getRgb());
                    binding.articleBackgroundNSV
                            .setBackgroundColor(p.getLightMutedSwatch().getRgb());
                    Log.d(TAG, "onCreate: color from bitmap: " + p.getLightMutedSwatch().getRgb()
                            + "");
                } else {
                    binding.toolbarArticle.setBackgroundColor(Color.TRANSPARENT);
                    binding.articleBackgroundNSV
                            .setBackgroundColor(p.getLightMutedSwatch().getRgb());
                    Log.d(TAG, "onCreate: else color from bitmap:TRANSPARENT ");
                }
            } catch (NullPointerException e) {
                if (isTransparent) {
                    binding.toolbarArticle.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    Log.e(TAG,
                            "onCreate: Переход по ссылке с заблюренной картинкой или Palette не может понять какой LightVibrantSwatch() ",
                            e.getCause());
                }
            }
        }
    }

    public void loadArticle(String html) {
        binding.article
                .loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", "");
    }

    public void setupCoverImage(Bitmap b) {
        binding.articleHeaderImg.setImageBitmap(b);
    }

    public ProgressBar getProgressBar() {
        return binding.articleProgressBar;
    }

    public TextView getArticleHeader() {
        return binding.articleHeader;
    }

    public ImageView getArticleImg() {
        return binding.articleHeaderImg;
    }

    public void setArticleTitle(String title) {
        articleTitle = title;
    }

    public TextView getArticleShortDescription() {
        return binding.articleShortDescription;
    }

    public WebView getArticle() {
        return binding.article;
    }

    public boolean hasBlur() {
        return hasBlur;
    }

    public String getWebViewTextColor() {
        return webViewTextColor;
    }

    public String getWebViewLinkColor() {
        return webViewLinkColor;
    }

    public String getWebViewTableColor() {
        return webViewTableColor;
    }

    public String getWebViewTableHeaderColor() {
        return webViewTableHeaderColor;
    }

}