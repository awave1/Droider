package com.awave.apps.droider.Utils.Feed;


public interface OnFeedItemTouch {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
