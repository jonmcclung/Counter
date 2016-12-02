package com.lerenard.counter3;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * This class automatically hides the fab when the user scrolls down, and shows it when they stop scrolling.
 */
public class DisappearingFabBehavior extends FloatingActionButton.Behavior {

    public DisappearingFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(
            final CoordinatorLayout layout,
            final FloatingActionButton child,
            final View directTargetChild,
            final View target,
            final int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(layout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(
            final CoordinatorLayout layout,
            final FloatingActionButton child,
            final View target,
            final int dxConsumed, final int dyConsumed,
            final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(layout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
//        Log.d(MainActivity.TAG, "scrolling, " + dxConsumed + ", " + dxUnconsumed + ", " + dyConsumed + ", " + dyUnconsumed);
        if (dyConsumed + dyUnconsumed > 0) child.hide();
    }

    @Override
    public void onStopNestedScroll(
            CoordinatorLayout layout,
            final FloatingActionButton child,
            View target) {
        child.show();
    }

}
