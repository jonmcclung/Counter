package com.lerenard.counter3;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Copied from http://stackoverflow.com/q/4139288/4714742
 */
public class OnSwipeListener implements OnTouchListener {

    private static final String TAG = "OnSwipeListener";
    private final GestureDetector gestureDetector;

    public OnSwipeListener(Context ctx) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 10;
        private static final int SWIPE_VELOCITY_THRESHOLD = 2;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                Log.d(TAG, Float.toString(diffX) + ", " + Float.toString(diffY) + ", " +
                           Float.toString(velocityX) + ", " + Float.toString(velocityY));
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        }
                        else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD &&
                         Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    }
                    else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {}

    public void onSwipeLeft() {}

    public void onSwipeTop() {}

    public void onSwipeBottom() {}
}
