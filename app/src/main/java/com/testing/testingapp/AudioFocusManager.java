package com.testing.testingapp;

import android.content.Context;
import android.media.AudioManager;
import android.widget.Toast;

public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private final Context context;
    private final AudioManager audioManager;

    public AudioFocusManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void requestAudioFocus() {
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Audio focus gained
            updateAudioFocusStatus("Audio focus gained");
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // Audio focus lost
            updateAudioFocusStatus("Audio focus lost");
            //requestAudioFocus();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            // Temporary audio focus loss
            updateAudioFocusStatus("Temporary audio focus loss");
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // Temporary audio focus loss with the ability to continue playing at a lower volume
            updateAudioFocusStatus("Temporary audio focus loss with the ability to duck");
        }
    }

    private void updateAudioFocusStatus(String status) {
        // Update your UI or perform other actions based on the audio focus status
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }
}

