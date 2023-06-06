package com.testing.testingapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewService extends Service {
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate() {
        super.onCreate();
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new AudioInterface(), "AudioInterface");

        String audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";  // Replace with your audio file URL
        String html = "<html><body><audio id=\"audioPlayer\" controls autoplay><source src=\"" + audioUrl + "\" type=\"audio/mpeg\"></audio></body></html>";

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Toast.makeText(WebViewService.this, "Page Loaded", Toast.LENGTH_SHORT).show();
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        Toast.makeText(this, "Playing now", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class AudioInterface {
        @JavascriptInterface
        public void onPlaybackCompleted() {
            // Handle playback completion if needed
        }
    }
}
