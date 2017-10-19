package com.apps.wow.droider.Feed.Interactors


interface OnFeedItemTouch {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDismiss(position: Int)
}
