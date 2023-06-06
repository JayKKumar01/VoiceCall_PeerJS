package com.testing.testingapp;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class CallNotificationActionReceiver extends BroadcastReceiver {
    NotificationListener notificationListener;

    public void setNotificationListener(NotificationListener notificationListener){
        this.notificationListener = notificationListener;
    }
    @Override
    public void onReceive(Context context, Intent intent1) {
        int requestCode = intent1.getIntExtra("requestCode", -1);

        if (requestCode == CallActivity.REQUEST_CODE_MUTE) {
            CallService.listener.onToogleMic();
            if (CallActivity.listener != null) {
                CallActivity.listener.OnToogleMic();
            }
        } else if (requestCode == CallActivity.REQUEST_CODE_HANGUP) {
            CallService.listener.onDisconnect();
            if (CallActivity.listener != null){
                CallActivity.listener.OnHangUp();
            }
        } else if (requestCode == CallActivity.REQUEST_CODE_DEAFEN) {
            CallService.listener.onToogleDeafen();
        }
    }
}

