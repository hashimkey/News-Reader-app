package project.min.school.schoolapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class NewsActivity extends AppCompatActivity  {

    static final String INTENT_EXTRA_POSITION = "intentExtraPosition";
    final int POSITION_DEFAULT = -1;
        static final String codingScheme = "UTF-8";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_news);

            WebView webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());

            Intent intent = getIntent();

            //String contentString = News.mMemoryCache.get(News.cacheKey);
            webView.loadData(intent.getStringExtra("content"),"text/html",codingScheme);
            //webView.loadDataWithBaseURL(webUrl, contentString, "text/html", "utf-8", null);
            //webView.loadData(contentString, "text/html", codingScheme);

        }
    }