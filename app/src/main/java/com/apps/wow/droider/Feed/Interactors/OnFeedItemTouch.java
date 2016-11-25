package com.apps.wow.droider.Feed.Interactors;


public interface OnFeedItemTouch {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
