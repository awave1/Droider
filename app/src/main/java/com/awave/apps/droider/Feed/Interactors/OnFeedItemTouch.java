package com.awave.apps.droider.Feed.Interactors;


public interface OnFeedItemTouch {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
