package com.example.pdfeditormadtpeeps.CustomWidgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class BoldButton extends AppCompatButton {

    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
     */
    private static Typeface mTypeface;

    public BoldButton(final Context context) {
        this(context, null);
    }

    public BoldButton(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoldButton(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/PoppinsBold.ttf");
        }
        setTypeface(mTypeface);
    }
}
