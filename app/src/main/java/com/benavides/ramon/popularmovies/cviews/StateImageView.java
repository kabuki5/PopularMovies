package com.benavides.ramon.popularmovies.cviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.benavides.ramon.popularmovies.R;

public class StateImageView extends ImageView {

    public StateImageView(Context context) {
        super(context);
    }

    public StateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressed) {
            setColorFilter(R.color.colorPrimary);
        } else {
            setColorFilter(null);
        }

        super.setPressed(pressed);
    }
}
