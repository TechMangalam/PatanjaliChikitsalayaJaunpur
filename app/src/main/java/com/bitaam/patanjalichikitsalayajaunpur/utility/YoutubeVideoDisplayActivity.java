package com.bitaam.patanjalichikitsalayajaunpur.utility;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bitaam.patanjalichikitsalayajaunpur.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class YoutubeVideoDisplayActivity extends AppCompatActivity {

    BgMediaWebView yogaVideo;
    ProgressBar progressBar;
    String videoUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video_display);

        Intent intent = getIntent();
        videoUrl = intent.getStringExtra("videoUrl");

        progressBar = findViewById(R.id.yogaLoadingProgrssbar);
        yogaVideo = findViewById(R.id.yogaWebView);

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadVid(String vid){

        yogaVideo.setWebChromeClient(new MyChrome());
        yogaVideo.getSettings().setPluginState(WebSettings.PluginState.ON);
        yogaVideo.setWebViewClient(new MyWebViewClient());
        yogaVideo.getSettings();
        yogaVideo.getSettings().setJavaScriptEnabled(true);
        yogaVideo.setBackgroundColor(0x00000000);
        yogaVideo.setKeepScreenOn(true);
        yogaVideo.setHorizontalScrollBarEnabled(false);
        yogaVideo.setVerticalScrollBarEnabled(false);
        yogaVideo.getSettings().setBuiltInZoomControls(true);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";

        String html = getHTML(vid);
        yogaVideo.loadDataWithBaseURL("", html, mimeType, encoding, "");

    }

    public String checkForLink(String link){
        if (link.startsWith("https://youtube.com/playlist?list=")){
            link = ""+link.substring(link.indexOf('=')+1);
        } else if (link.startsWith("https://youtu.be/")) {
            link = ""+link.substring(link.lastIndexOf('/')+1);
        }
        return  link;
    }

    public String getHTML(String vid)
    {

        String html = "<html>"

                + "<head>"
                + "</head>"
                + "<body style=\"border: 0; padding: 0\">"
                + "<iframe "
                + "type=\"text/html\" "
                + "class=\"youtube-player\" "
                + "width= 100%\""
                + "\" "
                + "height= 100%\""
                + "\" "
                + "src=\"http://www.youtube.com/embed/"
                + vid
                + "?controls=1&showinfo=0&showsearch=0&modestbranding=0" +
                "&autoplay=1&fs=1&vq=small\" " + "frameborder=\"0\" allow=\"autoplay;encrpted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"
                + "</body>"
                + "</html>";

        return html;
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadVid(checkForLink(videoUrl));
    }

    @Override
    protected void onPause() {
        super.onPause();
        yogaVideo.loadUrl("");
        yogaVideo.clearCache(true);
    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        yogaVideo.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        yogaVideo.restoreState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }

    public class MyWebViewClient extends WebViewClient {

        public MyWebViewClient() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            if(progressBar.getVisibility() == View.GONE){
                progressBar.setVisibility(View.VISIBLE);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(progressBar.getVisibility()==View.VISIBLE){
                progressBar.setVisibility(View.GONE);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(progressBar.getVisibility()==View.GONE){
                progressBar.setVisibility(View.VISIBLE);
            }
            super.onPageStarted(view, url, favicon);
        }
    }


    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            setRequestedOrientation(ActivityInfo. SCREEN_ORIENTATION_PORTRAIT);
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            //setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            setRequestedOrientation(ActivityInfo. SCREEN_ORIENTATION_LANDSCAPE);
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            yogaVideo.setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }

}