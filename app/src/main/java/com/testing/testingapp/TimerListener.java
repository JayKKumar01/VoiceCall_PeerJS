package com.testing.testingapp;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class TimerListener {
    NotificationListener listener;
    Context context;
    private int curSec;
    private boolean isActive;

    public TimerListener(NotificationListener listener){
        this.listener = listener;
        context = (Context) listener;
    }

    public void startTimer(int curTime) {
        this.curSec = curTime;
        isActive = true;
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isActive) {
                    listener.onUpdateTime(curSec++);
                    handler.postDelayed(this, 1000);
                }else {
                    handler.removeCallbacks(this);
                }
            }
        });

    }

    public void endTimer(){
        isActive = false;
    }
}
