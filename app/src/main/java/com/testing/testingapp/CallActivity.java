package com.testing.testingapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

import java.io.IOException;
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
    private ImageView callBtn,micBtn,deafenBtn;
    private UserAdapter userAdapter;
    private List<UserModel> userList;
    private String code;

    UserModel userModel;
    public static NotificationListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_call);


        listener = this;
        initViews();

        if (userModel != null) {
            userNameTV.setText(userModel.getName());
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
        callBtn = findViewById(R.id.callBtn);
        micBtn = findViewById(R.id.micBtn);
        deafenBtn = findViewById(R.id.deafenBtn);

        // Retrieve UserModel and code from the intent
        userModel = (UserModel) getIntent().getSerializableExtra("userModel");
        code = getIntent().getStringExtra("code");
        if (getIntent().getStringExtra("notification") != null){
            findViewById(R.id.noti).setVisibility(View.VISIBLE);
        }
        boolean pendingIntent = false;

        Intent serviceIntent = new Intent(this, CallService.class);
        serviceIntent.putExtra("code", code);
        serviceIntent.putExtra("userModel", userModel);




        if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("create")){
            serviceIntent.putExtra("type", "create");
            callBtn.setImageResource(R.drawable.call);
            Info.isCallActive = true;
            micBtn.setVisibility(View.VISIBLE);
            deafenBtn.setVisibility(View.VISIBLE);
            //createNotification(mic);
        }
        else if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("pendingIntent")){
            pendingIntent = true;
            callBtn.setImageResource(R.drawable.call);
            Info.isCallActive = true;
            micBtn.setVisibility(View.VISIBLE);
            deafenBtn.setVisibility(View.VISIBLE);
            setMicImage();
            setDeafenImage();
        }

        if (!pendingIntent){
            startService(serviceIntent);
        }
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

    public void joinCall(View view){
        Info.isCallActive = !Info.isCallActive;
        CallService.listener.onJoinCall(Info.isCallActive,userList);
        if(Info.isCallActive){
            callBtn.setImageResource(R.drawable.call);
            micBtn.setVisibility(View.VISIBLE);
            deafenBtn.setVisibility(View.VISIBLE);
        }
        else{
            finish();
        }


    }


    public void mic(View view) {
        toogleMic(true);
    }
    public void deafen(View view) {
        toogleDeafen(true);
    }
    private void toogleMic(boolean isTap) {
        Info.isMute = !Info.isMute;
        if (isTap){
            CallService.listener.onToogleMic();
        }
        setMicImage();
    }
    private void toogleDeafen(boolean isTap) {
        Info.isDeafen = !Info.isDeafen;
        if (isTap){
            CallService.listener.onToogleDeafen();
        }
        setDeafenImage();
    }

    private void setDeafenImage() {
        if(Info.isDeafen){
            deafenBtn.setImageResource(R.drawable.deafen_on);
        }
        else{
            deafenBtn.setImageResource(R.drawable.deafen_off);
        }
    }

    private void setMicImage() {
        if(Info.isMute){
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
        deafenBtn.setVisibility(View.GONE);
    }

    @Override
    public void onToogleMic() {
        toogleMic(false);
    }

    @Override
    public void onHangUp() {
        finish();
    }

    @Override
    public void onDeafen() {
        toogleDeafen(false);
    }
}