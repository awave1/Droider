package com.awave.apps.droider.Utils.Feed;

/**
 * Created by awave on 2016-02-19.
 */
public interface OnFeedItemTouch {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
