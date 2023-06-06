package com.testing.testingapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallNotificationActionReceiver extends BroadcastReceiver implements Data{
    NotificationListener notificationListener;

    public void setNotificationListener(NotificationListener notificationListener){
        this.notificationListener = notificationListener;
    }
    @Override
    public void onReceive(Context context, Intent intent1) {
        int requestCode = intent1.getIntExtra("requestCode", -1);

        if (requestCode == REQUEST_CODE_MUTE) {
            CallService.listener.onToogleMic();
            CallActivity.listener.onToogleMic();
        } else if (requestCode == REQUEST_CODE_HANGUP) {
            CallService.listener.onDisconnect();
            CallActivity.listener.onHangUp();
        } else if (requestCode == CallActivity.REQUEST_CODE_DEAFEN) {
            CallService.listener.onToogleDeafen();
            CallActivity.listener.onDeafen();
        }
    }
}

