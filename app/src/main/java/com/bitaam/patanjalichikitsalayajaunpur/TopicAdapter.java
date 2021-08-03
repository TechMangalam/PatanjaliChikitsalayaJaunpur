package com.bitaam.patanjalichikitsalayajaunpur;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    RecyclerView recyclerView;
    ArrayList<String> videoId = new ArrayList<>();
    ArrayList<String> videoIdT = new ArrayList<>();
    Context context;
    boolean playing = false;
    int currentSong=0;
    OnPlayControlLitseners playControlLitseners;

    public interface OnPlayControlLitseners{
        void  playSong(String videoId,String videoT);
        void pauseSong(String videoId);
    }

    public void setPlayControlLitseners(OnPlayControlLitseners controlLitseners){
        playControlLitseners = controlLitseners;
    }


    public TopicAdapter(RecyclerView recyclerView, Context context, ArrayList<String> videoId,ArrayList<String> videoIdT) {
        this.recyclerView = recyclerView;
        this.videoId = videoId;
        this.context = context;
        this.videoIdT = videoIdT;
    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.video_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        MobileAds.initialize(context);
        if (position%2==1) {
            holder.banADS.setVisibility(View.VISIBLE);
            holder.banADS.loadAd(new AdRequest.Builder().build());
        }else {
            holder.banADS.setVisibility(View.GONE);
        }

        holder.titleTv.setText(videoIdT.get(position));
        holder.songNo.setText(position+".");

        holder.playPauseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing && currentSong == position){
                    playing = false;
                    playControlLitseners.pauseSong(videoId.get(position));
                    holder.playPauseImgBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);

                }else {
                    playing = true;
//                    holder.youtubeWebEmbed.resumeTimers();
//                    holder.youtubeWebEmbed.onResume();
                    playControlLitseners.playSong(videoId.get(position),videoIdT.get(position));
                    holder.playPauseImgBtn.setImageResource(R.drawable.ic_baseline_cancel_24);
                }
            }
        });

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
                + "height= 95%\""
                + "\" "
                + "src=\"http://www.youtube-nocookie.com/embed/"
                + vid
                + "?controls=0&showinfo=0&showsearch=0&modestbranding=0" +
                "&autoplay=1&fs=1&vq=tiny\" " + "frameborder=\"0\" allow=\"autoplay;encrpted-media\" allowfullscreen></iframe>"
                + "</body>"
                + "</html>";

        return html;
    }

    public String getHTMLPlaylist(String vid)
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
                + "src=\"http://www.youtube.com/embed/videoseries?list="
                + vid.trim()
                + "\" frameborder=\"0\" allow=\"autoplay;encrpted-media;picture-in-picture\" allowfullscreen></iframe>"
                + "</body>"
                + "</html>";

        return html;
    }



    @Override
    public int getItemCount() {
        return videoId.size();
    }

    public void update(String name,String vt){

        videoId.add(name);
        videoIdT.add(vt);

        notifyDataSetChanged();

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        AdView banADS;
        ImageButton playPauseImgBtn;
        TextView titleTv,songNo;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView_video);
            songNo = itemView.findViewById(R.id.songIdTv);
            banADS = itemView.findViewById(R.id.banAds);
            titleTv = itemView.findViewById(R.id.videoTitleTv);
            playPauseImgBtn = itemView.findViewById(R.id.play_pause_image_btn);




        }


    }



}

