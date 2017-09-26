package com.news.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.news.activity.R;

public class WebActivity extends AppCompatActivity {

    private WebView web_display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent browser = getIntent();
        String url = browser.getStringExtra("url");

        web_display = (WebView) findViewById(R.id.web_display);
        web_display.setWebViewClient(new MyBrowser());
        web_display.getSettings().setLoadsImagesAutomatically(true);
        web_display.loadUrl(url);

    }

    private class MyBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
