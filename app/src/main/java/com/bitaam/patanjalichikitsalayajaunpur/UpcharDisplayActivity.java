package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.modals.UpcharModal;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaModal;
import com.bitaam.patanjalichikitsalayajaunpur.utility.BgMediaWebView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class UpcharDisplayActivity extends AppCompatActivity {

    BgMediaWebView upcharVideo;
    Toolbar toolbar;
    ScrollView scrollView;
    //InterstitialAd interstitialAd;
    AdRequest adRequest;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upchar_display);

        MobileAds.initialize(getApplicationContext());

        Intent intent = getIntent();
        UpcharModal upcharModal = (UpcharModal) intent.getSerializableExtra("upcharInfo");

        scrollView = findViewById(R.id.scrollContentView);
        toolbar = findViewById(R.id.toolbarYoga);
        upcharVideo = findViewById(R.id.yogaWebView);
        progressBar = findViewById(R.id.upcharLoadingProgrssbar);
        TextView benifitsE = findViewById(R.id.benifitsE);
        TextView benifitsH = findViewById(R.id.benifitsH);
        TextView howE = findViewById(R.id.methodE);
        TextView howH = findViewById(R.id.methodH);
        TextView notE = findViewById(R.id.notToDoE);
        TextView notH = findViewById(R.id.notToDoH);
        AdView ban1 = findViewById(R.id.yogaConBan1);
        AdView ban2 = findViewById(R.id.yogaConBan2);
        AdView ban3 = findViewById(R.id.yogaConBan3);
        AdView ban11 = findViewById(R.id.yogaConBan11);
        AdView ban12 = findViewById(R.id.yogaConBan12);
        AdView ban13 = findViewById(R.id.yogaConBan13);
        AdView ban14 = findViewById(R.id.yogaConBan14);

        adRequest = new AdRequest.Builder().build();

        ban1.loadAd(adRequest);
        ban2.loadAd(adRequest);
        ban3.loadAd(adRequest);
        ban11.loadAd(adRequest);
        ban14.loadAd(adRequest);
        ban12.loadAd(adRequest);
        ban13.loadAd(adRequest);

        //interstitialAd = new InterstitialAd(this);
        //interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //test id - ca-app-pub-3940256099942544/1033173712
        //ad id - ca-app-pub-8103108161786269/4278453913
        //interstitialAd.loadAd(adRequest);

        //loadInterstitialContinuously();

        setSupportActionBar(toolbar);
        if (upcharModal != null){
            toolbar.setTitle(upcharModal.getUpcharName());
            benifitsE.setText(upcharModal.getSymptomE());
            benifitsH.setText(upcharModal.getSymptomH());
            howE.setText(upcharModal.getCureE());
            howH.setText(upcharModal.getCureH());
            notE.setText(upcharModal.getRegimenE());
            notH.setText(upcharModal.getRegimenH());
            loadVid(checkForLink(upcharModal.getVid()));
        }else{
            Toast.makeText(this, "Go back and reopen to refresh", Toast.LENGTH_LONG).show();
        }


    }

    private void loadInterstitialContinuously() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                if (interstitialAd.isLoaded())
//                    interstitialAd.show();

                loadInterstitialContinuously();

            }
        },60000);

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadVid(String vid){

        upcharVideo.setWebChromeClient(new MyChrome());
        upcharVideo.getSettings().setPluginState(WebSettings.PluginState.ON);
        upcharVideo.setWebViewClient(new UpcharDisplayActivity.MyWebViewClient());
        upcharVideo.getSettings();
        upcharVideo.getSettings().setJavaScriptEnabled(true);
        upcharVideo.setBackgroundColor(0x00000000);
        upcharVideo.setKeepScreenOn(true);
        upcharVideo.setHorizontalScrollBarEnabled(false);
        upcharVideo.setVerticalScrollBarEnabled(false);
        upcharVideo.getSettings().setBuiltInZoomControls(true);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";

        String html = getHTML(vid);
        upcharVideo.loadDataWithBaseURL("", html, mimeType, encoding, "");

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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        upcharVideo.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        upcharVideo.restoreState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            scrollView.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

            scrollView.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }

    public class MyWebViewClient extends WebViewClient {

        public MyWebViewClient() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            if(progressBar.getVisibility()==View.GONE){
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
            upcharVideo.setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }


}