package com.lerenard.counter3;

/**
 * Created by mc on 07-Dec-16.
 */
public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
