package com.apps.wow.droider.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.apps.wow.droider.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class FeedViewHolder extends RecyclerView.ViewHolder {

    private SimpleDraweeView cardImage;

    private CardView cardView;

    private TextView siteUrlTextView;

    private TextView cardDescriptionTextView;

    private TextView cardTitleTextView;

    public FeedViewHolder(View holderView) {
        super(holderView);
        cardView = (CardView) holderView.findViewById(R.id.card_view);
        cardImage = (SimpleDraweeView) holderView.findViewById(R.id.feed_card_image);

        siteUrlTextView = (TextView) holderView.findViewById(R.id.feed_card_site_url);
        cardTitleTextView = (TextView) holderView.findViewById(R.id.feed_card_title);
        cardDescriptionTextView = (TextView) holderView.findViewById(R.id.feed_card_description);
    }

    public TextView getCardDescriptionTextView() {
        return cardDescriptionTextView;
    }

    public TextView getCardTitleTextView() {
        return cardTitleTextView;
    }

    public TextView getSiteUrlTextView() {
        return siteUrlTextView;
    }

    public CardView getCardView() {
        return cardView;
    }

    public SimpleDraweeView getCardImage() {
        return cardImage;
    }
}
