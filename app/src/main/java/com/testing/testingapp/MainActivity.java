package com.testing.testingapp;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

@SuppressLint("DefaultLocale")
public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 112;
    private static final String NOTIFICATION_PERMISSION_CODE_STR = "112";
    String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private int reqCode = 1;

    private TextInputEditText etJoinName,etCode;
    private Button btnJoin;
    private boolean noPermission = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etJoinName = findViewById(R.id.etJoinName);
        etCode = findViewById(R.id.etCode);
        btnJoin = findViewById(R.id.btnJoin);
    }
    public void join(View view) {
        if(!isGranted()){
            askPermission();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !shouldShowRequestPermissionRationale(NOTIFICATION_PERMISSION_CODE_STR)){
            if (!noPermission){
                Toast.makeText(this, "No Notification permission", Toast.LENGTH_SHORT).show();
                getNotificationPermission();
                noPermission = true;
                return;
            }
        }else {
            noPermission = false;
        }

        if (!(etJoinName.getText() != null && !etJoinName.getText().toString().isEmpty())){
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = etJoinName.getText().toString();
        String code = null;

        if (etCode.getText() != null && !etCode.getText().toString().isEmpty()){
            code = etCode.getText().toString().trim();
        }



        String userId = Base.generateRandomString();
        UserModel userModel = new UserModel(name, userId,false,false,System.currentTimeMillis());

        if (code == null){
            int randomNumber = new Random().nextInt(1000000);
            code = String.format("%06d", randomNumber);

            FirebaseUtils.writeUserData(code, userModel);
            Intent intent = new Intent(MainActivity.this, CallActivity.class);
            intent.putExtra("userModel", userModel);
            intent.putExtra("code", code);
            intent.putExtra("type","create");
            if (noPermission){
                intent.putExtra("notification","no");
            }
            startActivity(intent);
            return;
        }
        String code1 = code;

        FirebaseUtils.checkCodeExists(code1, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
//                    FirebaseUtils.updateUserData(code, userModel);
                    Toast.makeText(MainActivity.this, "List Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, CallActivity.class);
                    intent.putExtra("userModel", userModel);
                    intent.putExtra("code", code1);
                    if (noPermission){
                        intent.putExtra("notification","no");
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Code Does not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
        }
    }


    void askPermission(){
        ActivityCompat.requestPermissions(this,permissions, reqCode);
    }

    private boolean isGranted(){
        for(String permission: permissions){
            if(ActivityCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @SuppressLint("ResourceAsColor")
    public void test(View view) {
        Intent intent = new Intent(this,ImageActivity.class);
        startActivity(intent);
    }



//    public void setMicColor(Context context, @DrawableRes int micDrawableRes, @ColorInt int color, int value, ImageView imageView) {
//        Drawable micDrawable = ContextCompat.getDrawable(context, micDrawableRes);
//
//        if (micDrawable != null) {
//            micDrawable = micDrawable.mutate(); // Create a mutable copy of the drawable
//            String pathData = getPathData(value);
//            micDrawable.setTint(color);
//            micDrawable.setTintMode(PorterDuff.Mode.SRC_IN);
//            micDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
//            micDrawable.setAlpha(255);
//            micDrawable.setPathData(pathData);
//            micDrawable = micDrawable.mutate(); // Create a mutable copy of the drawable
//            micDrawable.setTint(color);
//            int alpha = (int) (255 * (value / 100f)); // Calculate the alpha value based on the provided value
//            micDrawable.setAlpha(alpha);
//            imageView.setImageDrawable(micDrawable);
//        }
//    }

    public void setMicColor(Context context, @DrawableRes int micColoredRes, @DrawableRes int micUnfilledRes, @ColorInt int color, int value, ImageView imageView) {
        Drawable micColoredDrawable = ContextCompat.getDrawable(context, micColoredRes);
        Drawable micUnfilledDrawable = ContextCompat.getDrawable(context, micUnfilledRes);

        if (micColoredDrawable != null && micUnfilledDrawable != null) {
            micColoredDrawable = micColoredDrawable.mutate(); // Create a mutable copy of the colored mic drawable
            micUnfilledDrawable = micUnfilledDrawable.mutate(); // Create a mutable copy of the unfilled mic drawable

            micColoredDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

            float fillRatio = value / 100f;
            int fillHeight = (int) (micColoredDrawable.getIntrinsicHeight() * fillRatio);
            micUnfilledDrawable.setBounds(0, fillHeight, micUnfilledDrawable.getIntrinsicWidth(), micUnfilledDrawable.getIntrinsicHeight());

            Drawable[] layers = {micColoredDrawable, micUnfilledDrawable};
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            imageView.setImageDrawable(layerDrawable);
        }
    }


}
