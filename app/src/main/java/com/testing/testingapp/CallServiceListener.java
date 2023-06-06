package com.testing.testingapp;

import java.util.List;

public interface CallServiceListener {
    void onJoinCall(boolean join, List<UserModel> userList);
    void onToogleMic(boolean isMute);
    void onDisconnect();
}
