package com.apps.wow.droider.Feed.Interactors

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

//TODO Сделать сохранение статей свайпом
class FeedItemTouchHelper(private val mFeedItemTouch: OnFeedItemTouch) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val swipeCards = ItemTouchHelper.START or ItemTouchHelper.END
        return ItemTouchHelper.Callback.makeMovementFlags(0, swipeCards)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        mFeedItemTouch.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mFeedItemTouch.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }
}
