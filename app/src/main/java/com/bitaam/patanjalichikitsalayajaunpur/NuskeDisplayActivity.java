package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.modals.NuskeModal;
import com.bitaam.patanjalichikitsalayajaunpur.utility.YoutubeVideoDisplayActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

public class NuskeDisplayActivity extends AppCompatActivity {

    ImageView yogaImgView;
    Toolbar toolbar;
    ScrollView scrollView;
    //InterstitialAd interstitialAd;
    AdRequest adRequest;
    ProgressBar progressBar;
    NuskeModal nuskeModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuske_display);

        MobileAds.initialize(getApplicationContext());

        Intent intent = getIntent();
        nuskeModal = (NuskeModal) intent.getSerializableExtra("nuskeInfo");

        scrollView = findViewById(R.id.scrollContentView);
        toolbar = findViewById(R.id.toolbarYoga);
        yogaImgView = findViewById(R.id.yogaImageView);
        progressBar = findViewById(R.id.yogaLoadingProgrssbar);
        TextView benifitsE = findViewById(R.id.benifitsE);
        TextView benifitsH = findViewById(R.id.benifitsH);
        TextView howE = findViewById(R.id.methodE);
        TextView howH = findViewById(R.id.methodH);
        AdView ban1 = findViewById(R.id.yogaConBan1);
        AdView ban2 = findViewById(R.id.yogaConBan2);
        AdView ban11 = findViewById(R.id.yogaConBan11);
        AdView ban12 = findViewById(R.id.yogaConBan12);
        AdView ban13 = findViewById(R.id.yogaConBan13);
        TextView writerNameTv = findViewById(R.id.writerNameTv);


        adRequest = new AdRequest.Builder().build();

        ban1.loadAd(adRequest);
        ban2.loadAd(adRequest);
        ban11.loadAd(adRequest);
        ban12.loadAd(adRequest);
        ban13.loadAd(adRequest);

        setSupportActionBar(toolbar);
        if (nuskeModal != null){
            Picasso.get().load(Uri.parse(nuskeModal.getIconUrl())).into(yogaImgView);
            String writer = "Written by : "+nuskeModal.getName();
            writerNameTv.setText(writer);
            toolbar.setTitle(nuskeModal.getNuskeName());
            benifitsE.setText(nuskeModal.getImpE());
            benifitsH.setText(nuskeModal.getImpH());
            howE.setText(nuskeModal.getHowE());
            howH.setText(nuskeModal.getHowH());
        }else{
            Toast.makeText(this, "Go back and reopen to refresh", Toast.LENGTH_LONG).show();
        }

        Button playYogaVideoBtn = findViewById(R.id.videoPlayBtn);
        playYogaVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), YoutubeVideoDisplayActivity.class);
                intent1.putExtra("videoUrl",nuskeModal.getVid());
                startActivity(intent1);

            }
        });

    }

}