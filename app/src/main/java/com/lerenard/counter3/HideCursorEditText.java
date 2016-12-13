package com.lerenard.counter3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.lerenard.counter3.helper.FontFitEditText;

/**
 * Created by mc on 11-Dec-16.
 */

public class HideCursorEditText extends FontFitEditText {

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
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    public HideCursorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
}
