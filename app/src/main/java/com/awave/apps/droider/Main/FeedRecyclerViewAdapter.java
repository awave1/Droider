package com.awave.apps.droider.Main;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.awave.apps.droider.Article.ArticleActivity;
import com.awave.apps.droider.Article.ArticleParser;
import com.awave.apps.droider.Model.FeedModel;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedViewHolder> {

    private static ArrayList<FeedModel> feedModelArrayList;
    private static Activity activity;
    private static Drawable headerImageDrawable;
    private String TAG = FeedRecyclerViewAdapter.class.getSimpleName();
    private boolean isPodcast = false;
    private float touchYCoordinate;
    private float touchXCoordinate;

    public FeedRecyclerViewAdapter(Activity activity, ArrayList<FeedModel> feedModelArrayList,
                                   boolean isPodcast) {
        FeedRecyclerViewAdapter.activity = activity;
        FeedRecyclerViewAdapter.feedModelArrayList = feedModelArrayList;
        this.isPodcast = isPodcast;
    }

    public static Drawable getHeaderImageDrawable() {
        return headerImageDrawable;
    }

    public static void setHeaderImageDrawable(Drawable headerImageDrawable) {
        FeedRecyclerViewAdapter.headerImageDrawable = headerImageDrawable;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new FeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder feedViewHolder, final int i) {

        final FeedModel feedModel = feedModelArrayList.get(i);
        feedViewHolder.getCardTitleTextView().setText(feedModel.getTitle());
        feedViewHolder.getCardDescriptionTextView().setText(feedModel.getDescription());
        feedViewHolder.getSiteUrlTextView().setText(feedModel.getUrl());

        if (isPodcast) {
            assert feedViewHolder.getCardImageView() != null;
            Glide.with(activity).load(feedModel.getDrCastImg()).into(feedViewHolder.getCardImageView());
        } else {
            Glide.with(activity).load(feedModel.getImgUrl()).into(feedViewHolder.getCardImageView());
        }

        final String url = feedModel.getUrl();

        feedViewHolder.getCardView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchXCoordinate = event.getRawX();
                touchYCoordinate = event.getRawY();
                return false;
            }
        });

        feedViewHolder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new ArticleParser(activity).execute(url);
                    Intent articleIntent = new Intent(activity, ArticleActivity.class);
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_TITLE, feedViewHolder.getCardTitleTextView().getText().toString());
                    articleIntent.putExtra(Utils.EXTRA_SHORT_DESCRIPTION, feedViewHolder.getCardDescriptionTextView().getText().toString());
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_URL, url);
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_X_TOUCH_COORDINATE, touchXCoordinate);
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_Y_TOUCH_COORDINATE, touchYCoordinate);
                    activity.startActivity(articleIntent);
                    FeedRecyclerViewAdapter.setHeaderImageDrawable(feedViewHolder.getCardImageView().getDrawable());
                } catch (NullPointerException npe) {
                    // Ошибка происходит если пытаться отправить пикчу
                    // в статью. Сначала он выкидывал NullPointerException
                    // на article в ArticleActivity. Я закомментил
                    // после этого ничего не открывалось
                    Toast.makeText(activity, "Произошла ошибка при открытии статьи!", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onClick: Failed to open ArticleActivity!", npe.getCause());
                    npe.printStackTrace();
                }
            }
        });
        feedViewHolder.getCardView().setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLingClick cardview");
                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData copyLink = ClipData.newPlainText("", feedModel.getUrl());
                clipboardManager.setPrimaryClip(copyLink);
                Toast.makeText(view.getContext(), R.string.main, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedModelArrayList.size();
    }

}