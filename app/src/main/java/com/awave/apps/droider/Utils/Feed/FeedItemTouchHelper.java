package com.awave.apps.droider.Utils.Feed;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;


public class FeedItemTouchHelper extends ItemTouchHelper.Callback {
    private static final String TAG = "FeedItemTouchHelper";

    private OnFeedItemTouch mFeedItemTouch;

    public FeedItemTouchHelper(OnFeedItemTouch feedItemTouch) {
        mFeedItemTouch = feedItemTouch;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeCards = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0, swipeCards);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mFeedItemTouch.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mFeedItemTouch.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
