package com.testing.testingapp.gestures;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import com.testing.testingapp.R;

public class OnTouch extends View implements View.OnTouchListener{
    Context context;
    private Drawable bg;

    public OnTouch(Context context) {
        super(context);
        this.context = context;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            bg = view.getBackground();
            view.setBackground(context.getDrawable(R.drawable.bg_hover));

        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            view.setBackground(bg);
        }
        return super.onTouchEvent(event);
    }
}
