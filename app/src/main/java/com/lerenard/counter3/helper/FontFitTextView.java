package com.lerenard.counter3.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import com.lerenard.counter3.R;

public class FontFitTextView extends TextView {
    protected final FontFit fontFit;
    private final String TAG = "FontFitTextView";

    public FontFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FontFit,
                0, 0);
        float minTextSize, maxTextSize;
        try {
            minTextSize = typedArray.getDimension(
                    R.styleable.FontFit_minTextSize,
                    FontFit.defaultMinTextSize);
            maxTextSize = typedArray.getDimension(
                    R.styleable.FontFit_maxTextSize,
                    FontFit.defaultMaxTextSize);
        } finally {
            typedArray.recycle();
        }
        fontFit = new FontFit(this, minTextSize, maxTextSize);
    }
}