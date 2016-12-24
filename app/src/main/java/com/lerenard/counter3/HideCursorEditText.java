package com.lerenard.counter3;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.lerenard.counter3.helper.FontFitEditText;

/**
 * Created by mc on 11-Dec-16.
 */

public class HideCursorEditText extends FontFitEditText {
    private OnKeyPreImeListener onKeyPreImeListener;

    public OnKeyPreImeListener getOnKeyPreImeListener() {
        return onKeyPreImeListener;
    }

    public void setOnKeyPreImeListener(OnKeyPreImeListener onKeyPreImeListener) {
        this.onKeyPreImeListener = onKeyPreImeListener;
    }

    private static final String TAG = "HideCursorEditText_TAG";

    private void init() {
        setCursorVisible(false);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCursorVisible(true);
            }
        });
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
            event.getAction() == KeyEvent.ACTION_UP) {
            setCursorVisible(false);
        }
        if (onKeyPreImeListener != null) {
            return onKeyPreImeListener.onKeyPreIme(keyCode, event);
        }
        else {
            return super.dispatchKeyEvent(event);
        }
    }

    public HideCursorEditText(
            Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public HideCursorEditText(
            Context context, AttributeSet attrs, OnKeyPreImeListener onKeyPreImeListener) {
        super(context, attrs);
        this.onKeyPreImeListener = onKeyPreImeListener;
        init();
    }
}
