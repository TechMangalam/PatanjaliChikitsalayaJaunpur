package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.modals.UpcharModal;
import com.bitaam.patanjalichikitsalayajaunpur.utility.YoutubeVideoDisplayActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

public class UpcharDisplayActivity extends AppCompatActivity {

    ImageView yogaImgView;
    Toolbar toolbar;
    ScrollView scrollView;
    //InterstitialAd interstitialAd;
    AdRequest adRequest;
    ProgressBar progressBar;
    UpcharModal upcharModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upchar_display);

        MobileAds.initialize(getApplicationContext());

        Intent intent = getIntent();
        upcharModal = (UpcharModal) intent.getSerializableExtra("upcharInfo");

        scrollView = findViewById(R.id.scrollContentView);
        toolbar = findViewById(R.id.toolbarYoga);
        yogaImgView = findViewById(R.id.yogaImageView);
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
            Picasso.get().load(Uri.parse(upcharModal.getIconUrl())).into(yogaImgView);
            toolbar.setTitle(upcharModal.getUpcharName());
            benifitsE.setText(upcharModal.getSymptomE());
            benifitsH.setText(upcharModal.getSymptomH());
            howE.setText(upcharModal.getCureE());
            howH.setText(upcharModal.getCureH());
            notE.setText(upcharModal.getRegimenE());
            notH.setText(upcharModal.getRegimenH());
            //loadVid(checkForLink(upcharModal.getVid()));
        }else{
            Toast.makeText(this, "Go back and reopen to refresh", Toast.LENGTH_LONG).show();
        }

        Button playYogaVideoBtn = findViewById(R.id.videoPlayBtn);
        playYogaVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), YoutubeVideoDisplayActivity.class);
                intent1.putExtra("videoUrl",upcharModal.getVid());
                startActivity(intent1);

            }
        });


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


}