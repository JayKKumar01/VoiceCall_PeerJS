package com.testing.testingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;


public class CallActivity extends AppCompatActivity implements NotificationListener,Data{



    private TextView roomCodeTV, userNameTV;
    private RecyclerView recyclerViewUsers;
    private ImageView callBtn,micBtn;
    private UserAdapter userAdapter;
    private List<UserModel> userList;
    private String code;

    WebView webView;
    UserModel userModel;
    public static boolean call,mic;
    private NotificationManager notificationManager;
    public static NotificationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_call);
        initViews();

        if (userModel != null) {
            userNameTV.setText(userModel.getName());
            //setupWebView();
        }

        if (code != null) {
            roomCodeTV.setText(code);
        }
        updateUsers();
    }

    private void initViews() {
        roomCodeTV = findViewById(R.id.roomCode);
        userNameTV = findViewById(R.id.userName);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        webView = findViewById(R.id.webView);
        callBtn = findViewById(R.id.callBtn);
        micBtn = findViewById(R.id.micBtn);

        // Retrieve UserModel and code from the intent
        userModel = (UserModel) getIntent().getSerializableExtra("userModel");
        code = getIntent().getStringExtra("code");

        Intent serviceIntent = new Intent(this, CallService.class);
        serviceIntent.putExtra("code", code);
        serviceIntent.putExtra("userModel", userModel);



        if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("create")){
            serviceIntent.putExtra("type", "create");
            callBtn.setImageResource(R.drawable.call);
            call = true;
            micBtn.setVisibility(View.VISIBLE);
            //createNotification(mic);
        }
        startService(serviceIntent);
//        else if (getIntent().getStringExtra("type").equals("pending")){
//            callBtn.setImageResource(R.drawable.call);
//            call = true;
//            micBtn.setVisibility(View.VISIBLE);
//        }
    }

    private void updateUsers() {
        FirebaseUtils.getUserList(code, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                userAdapter = new UserAdapter(userList);
                recyclerViewUsers.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setupWebView(){
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        String path = "file:android_asset/call.html";
        webView.loadUrl(path);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                boolean x = !url.equals("file:///android_asset/call.html");
                if(x){
                    Toast.makeText(CallActivity.this, "Returned!", Toast.LENGTH_SHORT).show();
                    return;
                }
                callJavaScript("javascript:init(\""+ userModel.getUserId() +"\")");
                Toast.makeText(CallActivity.this, "Started", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void joinCall(View view){
        call = !call;
        CallService.listener.onJoinCall(call,userList);
        if(call){
            callBtn.setImageResource(R.drawable.call);
            micBtn.setVisibility(View.VISIBLE);
//            callAllUsers(userList);
//            FirebaseUtils.updateUserData(code, userModel);
//            createNotification(mic);
        }
        else{
            finish();
        }


    }

    private void callAllUsers(List<UserModel> userList) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < userList.size(); i++) {
            stringBuilder.append("'").append(userList.get(i).getUserId()).append(i == userList.size() - 1 ? "']" : "',");
        }
        callJavaScript("javascript:startCall(" + stringBuilder.toString() + ");");
    }


    public void callJavaScript(String func){
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(func,null);
            }
        });
    }


    public void mic(View view) {
        toogleMic();
        //createNotification(mic);
    }

    private void toogleMic() {
        mic = !mic;
        callJavaScript("javascript:toggleAudio(\""+!mic+"\")");
        if(mic){
            micBtn.setImageResource(R.drawable.mic_off);
        }
        else{
            micBtn.setImageResource(R.drawable.mic_on);
        }
    }



    @Override
    protected void onDestroy() {
        disconnect();

        super.onDestroy();
    }
    private void disconnect() {
        callBtn.setImageResource(R.drawable.call_end);
        micBtn.setVisibility(View.GONE);
//        if (webView != null && webView.getParent() != null) {
//            webView.loadUrl("");
//            callJavaScript("javascript:endCall()");
//            callBtn.setImageResource(R.drawable.call_end);
//            micBtn.setVisibility(View.GONE);
//            FirebaseUtils.removeUserData(code, userModel);
//            Toast.makeText(this, "Disconnected!", Toast.LENGTH_SHORT).show();
//            notificationManager.cancelAll();
//        }
    }


    private void createNotification(boolean isMute) {
        listener = this;
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
            notificationManager.notify(NOTIFICATION_ID, builder.build());

        }
    }

    @Override
    public void OnToogleMic() {
        toogleMic();
        createNotification(mic);
    }

    @Override
    public void OnHangUp() {
        finish();
    }


}