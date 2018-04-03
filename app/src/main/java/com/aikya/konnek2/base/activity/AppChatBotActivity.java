package com.aikya.konnek2.base.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.aikya.konnek2.utils.AppConstant;


public class AppChatBotActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.aikya.konnek2.R.layout.activity_app_chat_bot);
        toolbar= findViewById(com.aikya.konnek2.R.id.toolbar_chatbot);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(AppConstant.HOME + AppConstant.GREATER_THAN + AppConstant.SARAI);
        toolbar.setNavigationIcon(com.aikya.konnek2.R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(com.aikya.konnek2.R.color.white));
        webView = findViewById(com.aikya.konnek2.R.id.webview_chatBot);
        progressBar = findViewById(com.aikya.konnek2.R.id.progress_chatbot);
        startWebView(AppConstant.CHAT_BOT_URL);
    }


    private void startWebView(String url) {
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onLoadResource(WebView view, String url) {
                if (progressBar == null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressBar.isEnabled()) {
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.aikya.konnek2.R.menu.menu_mstore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
