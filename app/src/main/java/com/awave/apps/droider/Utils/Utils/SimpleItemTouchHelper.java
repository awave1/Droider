package com.awave.apps.droider.Utils.Utils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by awave on 2016-01-23.
 */
public class SimpleItemTouchHelper extends ItemTouchHelper.Callback {
    private OnSimpleItemTouch itemTouchInterface;

    public SimpleItemTouchHelper(OnSimpleItemTouch itemTouchInterface) {
        this.itemTouchInterface = itemTouchInterface;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        itemTouchInterface.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        itemTouchInterface.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeCards = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0, swipeCards);
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