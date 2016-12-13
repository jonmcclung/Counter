package com.lerenard.counter3.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.lerenard.counter3.R;

/**
 * Created by mc on 13-Dec-16.
 */

public class FontFitEditText extends EditText {
    protected final FontFit fontFit;

    public FontFitEditText(Context context, AttributeSet attrs) {
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        fontFit.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(fontFit.getWidthMeasureSpec(), fontFit.getHeightMeasureSpec());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (fontFit == null) {
            super.onTextChanged(text, start, lengthBefore, lengthAfter);
        }
        else {
            fontFit.onTextChanged(text);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        fontFit.onSizeChanged(w, h, oldw, oldh);
    }
}
