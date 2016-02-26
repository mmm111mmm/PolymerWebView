package com.newfivefour.polymerwebview;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivityActivity extends AppCompatActivity {

  private WebView mWebview;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    mWebview = (WebView) findViewById(R.id.webView);

    mWebview.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
    mWebview.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
    mWebview.getSettings().setAllowFileAccess( true );
    mWebview.getSettings().setAppCacheEnabled( true );
    mWebview.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default

    if ( !isNetworkAvailable() ) { // loading offline
      Log.d("HIYA", "No network");
      mWebview.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
    }

    WebSettings webSettings = mWebview.getSettings();

    // Enable JavaScript.
    webSettings.setJavaScriptEnabled(true);

    // Enable HTML Imports to be loaded from file://.
    webSettings.setAllowFileAccessFromFileURLs(true);

    // Ensure local links/redirects in WebView, not the browser.
    mWebview.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {

        // Handle local URLs.
        if (Uri.parse(url).getHost().contains("newfivefour.com")) {
          return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
        return true;
      }
    });
    mWebview.setWebChromeClient(new WebChromeClient() {
      public void onProgressChanged(WebView view, int progress) {
        MainActivityActivity.this.setTitle("Loading...");
        MainActivityActivity.this.setProgress(progress * 100);

        if(progress == 100)
          MainActivityActivity.this.setTitle(R.string.app_name);
      }
    });

    mWebview.loadUrl("https://www.newfivefour.com/");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      WebView.setWebContentsDebuggingEnabled(true);
    }

  }

  @Override
  public void onBackPressed() {
    if(mWebview.canGoBack()) {
      mWebview.goBack();
    } else {
      super.onBackPressed();
    }
  }

  private boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

}
