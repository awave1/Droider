package com.apps.wow.droider.Adapters;

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

import com.apps.wow.droider.Article.ArticleActivity;
import com.apps.wow.droider.Model.NewFeedModel;
import com.apps.wow.droider.Model.Post;
import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.Utils;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedViewHolder> {

    private static Drawable headerImageDrawable;
    private NewFeedModel feedModel;
    private ArrayList<Post> postList;
    private String TAG = FeedRecyclerViewAdapter.class.getSimpleName();
    private boolean isPodcast = false;
    private float touchYCoordinate;
    private float touchXCoordinate;

    public FeedRecyclerViewAdapter(NewFeedModel feedModel, boolean isPodcast) {
        this.feedModel = feedModel;
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

        final Post post = feedModel.getPosts().get(i);
        feedViewHolder.getCardTitleTextView().setText(
                StringEscapeUtils.unescapeHtml4(post.getTitle())
        );

        if (!post.getDescription().isEmpty()) {
            feedViewHolder.getCardDescriptionTextView().setText(
                    StringEscapeUtils.unescapeHtml4(post.getDescription())
            );
        } else {
            feedViewHolder.getCardDescriptionTextView().setVisibility(View.GONE);
        }

        feedViewHolder.getSiteUrlTextView().setText(post.getUrl());


        Picasso.with(feedViewHolder.getCardImageView().getContext())
                .load(post.getPictureWide())
                .into(feedViewHolder.getCardImageView());

        final String url = post.getUrl();

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
                    Intent articleIntent =
                            new Intent(feedViewHolder.getCardView().getContext(), ArticleActivity.class);
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_TITLE,
                            feedViewHolder.getCardTitleTextView().getText().toString());
                    articleIntent.putExtra(Utils.EXTRA_SHORT_DESCRIPTION,
                            feedViewHolder.getCardDescriptionTextView().getText().toString());
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_URL, url);
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_X_TOUCH_COORDINATE, touchXCoordinate);
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_Y_TOUCH_COORDINATE, touchYCoordinate);
                    feedViewHolder.getCardView().getContext().startActivity(articleIntent);
                    FeedRecyclerViewAdapter.setHeaderImageDrawable(
                            feedViewHolder.getCardImageView().getDrawable());
                } catch (NullPointerException npe) {
                    // Ошибка происходит если пытаться отправить пикчу
                    // в статью. Сначала он выкидывал NullPointerException
                    // на article в ArticleActivity. Я закомментил
                    // после этого ничего не открывалось
                    Toast.makeText(feedViewHolder.getCardView().getContext(),
                            "Произошла ошибка при открытии статьи!", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onClick: Failed to open ArticleActivity!", npe.getCause());
                    npe.printStackTrace();
                }
            }
        });
        feedViewHolder.getCardView().setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLingClick cardview");
                ClipboardManager clipboardManager = (ClipboardManager) feedViewHolder.getCardView()
                        .getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData copyLink = ClipData.newPlainText("", post.getUrl());
                clipboardManager.setPrimaryClip(copyLink);
                Toast.makeText(view.getContext(), R.string.main, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedModel.getPosts().size();
    }

    public NewFeedModel getFeedModel() {
        return feedModel;
    }
}