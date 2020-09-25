package com.example.hyeoukloginchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Major extends AppCompatActivity {

    private WebView webView;
    private String url="https://major.eulji.ac.kr/mitm/index.html?menuno=2897";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        webView =(WebView)findViewById(R.id.schedulewebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new Major.WebViewClientClass2());
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);



    }


    private class WebViewClientClass2 extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}