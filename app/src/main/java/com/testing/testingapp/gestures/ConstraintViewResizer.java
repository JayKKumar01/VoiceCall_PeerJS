package com.testing.testingapp.gestures;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.testing.testingapp.CallActivity;

import java.util.Random;

public class ConstraintViewResizer {
    private final View view;
    private final ConstraintLayout.LayoutParams params;
    private final Handler handler;
    private final Random random;
    private Runnable runnable;
    int count;

    public ConstraintViewResizer(View view) {
        //this.context = context;
        this.view = view;
        params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        handler = new Handler();
        random = new Random();
    }

    public void randomSize() {
        Toast.makeText(CallActivity.context, "started", Toast.LENGTH_SHORT).show();
        runnable = new Runnable() {
            @Override
            public void run() {
                float randomFloat = random.nextFloat() + 0.1f;
                if (randomFloat >1){
                    randomFloat = 1.0f;
                }
                randomFloat = Math.round(randomFloat * 10.0f) / 10.0f;
                Log.d("randomFloat",randomFloat +"");
                resizeHeight(randomFloat);
                handler.postDelayed(runnable, 100);
            }
        };
        handler.post(runnable);
    }

    public void finish() {
        handler.removeCallbacks(runnable);
    }

    public void resizeHeight(float height) {
        params.matchConstraintPercentHeight = height;
        view.setLayoutParams(params);
    }
}
