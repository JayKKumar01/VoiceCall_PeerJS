package com.testing.testingapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.List;

public class CallService extends Service implements CallServiceListener,Data{

    private WebView webView;
    private String code = null;
    private UserModel userModel;
    private NotificationManager notificationManager;

    public static CallServiceListener listener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listener = this;
        if (intent != null) {
            code = intent.getStringExtra("code");
            userModel = (UserModel) intent.getSerializableExtra("userModel");
            if (intent.getStringExtra("type") != null && intent.getStringExtra("type").equals("create")){
                createNotification(false);
            }
        }

        // Set up the WebView here
        setupWebView();

        return START_STICKY;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                boolean x = !url.equals("file:///android_asset/call.html");
                if(x){
                    return;
                }
//                view.loadUrl("javascript:test()");
                callJavaScript("javascript:init(\""+ userModel.getUserId() +"\")");
//                callJavaScript("javascript:test()");
                Toast.makeText(CallService.this, "Started", Toast.LENGTH_SHORT).show();
            }

        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        String path = "file:android_asset/call.html";
        webView.loadUrl(path);


    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void callJavaScript(String func) {
        webView.evaluateJavascript(func, null);
    }


    @Override
    public void onJoinCall(boolean call, List<UserModel> userList) {
        if(call){
            callAllUsers(userList);
            FirebaseUtils.updateUserData(code, userModel);
            createNotification(false);
        }
        else{
            onDisconnect();
        }

    }

    @Override
    public void onToogleMic(boolean isMute) {

    }

    @Override
    public void onDisconnect() {
        if (webView != null) {
            webView.loadUrl("");
            callJavaScript("javascript:endCall()");
            FirebaseUtils.removeUserData(code, userModel);
            Toast.makeText(this, "Disconnected!", Toast.LENGTH_SHORT).show();
        }
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
        stopSelf();
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    private void callAllUsers(List<UserModel> userList) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < userList.size(); i++) {
            stringBuilder.append("'").append(userList.get(i).getUserId()).append(i == userList.size() - 1 ? "']" : "',");
        }
        callJavaScript("javascript:startCall(" + stringBuilder.toString() + ");");
    }
    private void createNotification(boolean isMute) {
        // Create an explicit intent for the activity that handles the button actions
        Intent intent = new Intent(this, CallNotificationActionReceiver.class);
        intent.setAction("com.testing.testingapp.ACTION_MUTE_HANGUP");
        intent.putExtra("requestCode", REQUEST_CODE_MUTE);

        PendingIntent mutePendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_MUTE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        intent.putExtra("requestCode", REQUEST_CODE_HANGUP);
        PendingIntent hangupPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_HANGUP, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String muteLabel = isMute? "Unmute" : "Mute";
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.call)
                .setContentTitle("Joining Call")
                .setContentText("Tap to manage the call")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.mic_on, muteLabel, mutePendingIntent)
                .addAction(R.drawable.call_end, "Hangup", hangupPendingIntent)
                .setAutoCancel(true)
                .setOngoing(true);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the notification channel for Android Oreo and above
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);


                notificationManager.createNotificationChannel(channel);
            }
            startForeground(NOTIFICATION_ID, builder.build());
            //notificationManager.notify(NOTIFICATION_ID, builder.build());

        }
    }
}
