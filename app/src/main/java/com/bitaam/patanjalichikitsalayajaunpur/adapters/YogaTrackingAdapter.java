package com.bitaam.patanjalichikitsalayajaunpur.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaModal;
import com.bitaam.patanjalichikitsalayajaunpur.utility.BgMediaWebView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

public class YogaTrackingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RecyclerView recyclerView;
    ArrayList<YogaModal> yogaModals;
    Context context;
    AdRequest adRequest;
    private final String API_KEY = "AIzaSyAjpzvEvj6rYzw4TF_cdmxEDTjCXFyYx5k";


    public YogaTrackingAdapter(RecyclerView recyclerView, Context context,ArrayList<YogaModal> yogaModals) {
        this.recyclerView = recyclerView;
        this.yogaModals = yogaModals;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Fresco.initialize(context);
        View view = LayoutInflater.from(context).inflate(R.layout.yoga_tracker_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        MobileAds.initialize(context);
        adRequest = new AdRequest.Builder().build();

        ((ViewHolder)holder).ban1.loadAd(adRequest);
        ((ViewHolder)holder).ban2.loadAd(adRequest);
        ((ViewHolder)holder).ban3.loadAd(adRequest);
        ((ViewHolder)holder).ban11.loadAd(adRequest);
        ((ViewHolder)holder).ban12.loadAd(adRequest);
        ((ViewHolder)holder).ban13.loadAd(adRequest);
        ((ViewHolder)holder).ban14.loadAd(adRequest);

        YogaModal yogaModal = yogaModals.get(position);

        if (yogaModal != null){
            ((ViewHolder)holder).benifitsE.setText(yogaModal.getImpE());
            ((ViewHolder)holder).benifitsH.setText(yogaModal.getImpH());
            ((ViewHolder)holder).howE.setText(yogaModal.getHowE());
            ((ViewHolder)holder).howH.setText(yogaModal.getHowH());
            ((ViewHolder)holder).notE.setText(yogaModal.getNotE());
            ((ViewHolder)holder).notH.setText(yogaModal.getNotH());
            //loadVid(holder,checkForLink(yogaModal.getVid()));
            ((ViewHolder)holder).youTubePlayerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.loadVideo(yogaModal.getVid());
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });

        }



    }

    @Override
    public int getItemViewType(int position) {

        return 1;
    }

    @Override
    public int getItemCount() {
        return yogaModals.size();
    }

    public void update(YogaModal yogaModal){

        yogaModals.add(yogaModal);
        notifyDataSetChanged();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        BgMediaWebView yogaWebView;
        ProgressBar progressBar;
        TextView benifitsE,benifitsH,howE,howH,notE,notH;
        AdView ban1,ban2,ban3,ban11,ban12,ban13,ban14;
        YouTubePlayerView youTubePlayerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Fresco.initialize(context);
            youTubePlayerView = itemView.findViewById(R.id.youtubePlayerView);
            yogaWebView = itemView.findViewById(R.id.yoga_video_webview);
            progressBar = itemView.findViewById(R.id.yogaLoadingProgrssbar);
            benifitsE = itemView.findViewById(R.id.benifitsE);
            benifitsH = itemView.findViewById(R.id.benifitsH);
            howE = itemView.findViewById(R.id.methodE);
            howH = itemView.findViewById(R.id.methodH);
            notE = itemView.findViewById(R.id.notToDoE);
            notH = itemView.findViewById(R.id.notToDoH);
            ban1 = itemView.findViewById(R.id.yogaConBan1);
            ban2 = itemView.findViewById(R.id.yogaConBan2);
            ban3 = itemView.findViewById(R.id.yogaConBan3);
            ban11 = itemView.findViewById(R.id.yogaConBan11);
            ban12 = itemView.findViewById(R.id.yogaConBan12);
            ban13 = itemView.findViewById(R.id.yogaConBan13);
            ban14 = itemView.findViewById(R.id.yogaConBan14);

        }


    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadVid(RecyclerView.ViewHolder holder,String vid){

        ((ViewHolder)holder).yogaWebView.setWebChromeClient(new WebChromeClient());
        ((ViewHolder)holder).yogaWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        ((ViewHolder)holder).yogaWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        ((ViewHolder)holder).yogaWebView.getSettings().setJavaScriptEnabled(true);
        ((ViewHolder)holder).yogaWebView.setBackgroundColor(Color.BLACK);



        final String mimeType = "text/html";
        final String encoding = "UTF-8";

        String html = getHTML(vid);
        ((ViewHolder)holder).yogaWebView.loadDataWithBaseURL("", html, mimeType, encoding, "");

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



}
