package com.testing.testingapp;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FirebaseUtils {

    private static DatabaseReference getDatabaseReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child("VoiceChatApp");
    }

    public static void checkCodeExists(String code, ValueEventListener valueEventListener) {
        DatabaseReference databaseReference = getDatabaseReference().child(code);
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }
    public static void writeUserData(String path, UserModel model) {
        DatabaseReference reference = getDatabaseReference().child(path);
        List<UserModel> list = new ArrayList<>();
        list.add(model);
        reference.setValue(list);
    }

    public static void getUserList(String code, ValueEventListener valueEventListener) {
        DatabaseReference databaseReference = getDatabaseReference().child(code);
        databaseReference.addValueEventListener(valueEventListener);
    }

    public static void updateUserData(String path, UserModel userModel) {
        DatabaseReference databaseReference = getDatabaseReference().child(path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the existing list from the database
                    List<UserModel> userList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel existingUser = snapshot.getValue(UserModel.class);
                        userList.add(existingUser);
                    }

                    // Add the new user to the existing list
                    userList.add(userModel);

                    // Update the database with the updated list
                    databaseReference.setValue(userList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if needed
            }
        });
    }
    public static void removeUserData(String path, UserModel userModel) {
        DatabaseReference databaseReference = getDatabaseReference().child(path);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = userModel.getUserId();
                    // Retrieve the existing list from the database
                    List<UserModel> userList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel existingUser = snapshot.getValue(UserModel.class);
                        assert existingUser != null;
                        if (!existingUser.getUserId().equals(userId)) {
                            userList.add(existingUser);
                        }
                    }

                    // Update the database with the updated list
                    databaseReference.setValue(userList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case if needed
            }
        });
    }

    public static void deleteData(String path) {
        DatabaseReference reference = getDatabaseReference().child(path);
        reference.removeValue();
    }
}

