package com.bitaam.patanjalichikitsalayajaunpur;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaModal;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaSessionModel;
import com.bitaam.patanjalichikitsalayajaunpur.utility.BgMediaWebView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class YogaTrackingActivity extends YouTubeBaseActivity {

    ArrayList<YogaModal> yogaModals;
    RelativeLayout yogaControlRlt,yogaCountRlt,yogaContentRlt;
    View progTo2,progTo3,progTo4,progTo5,progTo6;
    TextView yogaCount1Tv,yogaCount2Tv,yogaCount3Tv,yogaCount4Tv,yogaCount5Tv,yogaCount6Tv,yogaTimerTv;
    Button startYogaBtn,nextYogaBtn;
    boolean status = false;
    AdRequest adRequest;
    private RewardedAd mRewardedAd;
    BgMediaWebView yogaWebView;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    TextView errorTv,benifitsE,benifitsH,howE,howH,notE,notH,yogaSessionTitle,yogaName;
    AdView ban1,ban2,ban3,ban12,ban13,ban14;
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer mYouTubePlayer;
    private final String API_KEY = "AIzaSyAjpzvEvj6rYzw4TF_cdmxEDTjCXFyYx5k";
    int currentTime = 0;
    int currentPos = 0;
    ArrayList<String> yogaList;
    YogaSessionModel yogaSessionModel;
    TextToSpeech textToSpeech;
    FloatingActionButton stopVoiceBtn;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_tracking);

        Intent intent = getIntent();
        yogaSessionModel = (YogaSessionModel) intent.getSerializableExtra("sessionInfo");

        yogaModals = new ArrayList<>();
        yogaList = new ArrayList<>();
        assert yogaSessionModel != null;
        yogaList.add(yogaSessionModel.getYoga1());
        yogaList.add(yogaSessionModel.getYoga2());
        yogaList.add(yogaSessionModel.getYoga3());
        yogaList.add(yogaSessionModel.getYoga4());
        yogaList.add(yogaSessionModel.getYoga5());
        yogaList.add(yogaSessionModel.getYoga6());

        progressDialogShow("Loading","Please wait ..");
        databaseActivities();

        yogaContentRlt = findViewById(R.id.yogaContentRlt);
        yogaControlRlt = findViewById(R.id.controllerRlt);
        yogaCountRlt = findViewById(R.id.yogaNoIndicatorLl);

        progTo2 = findViewById(R.id.prog_to_2);
        progTo3 = findViewById(R.id.prog_to_3);
        progTo4 = findViewById(R.id.prog_to_4);
        progTo5 = findViewById(R.id.prog_to_5);
        progTo6 = findViewById(R.id.prog_to_6);

        stopVoiceBtn = findViewById(R.id.stopVoiceInstructions);
        yogaCount1Tv = findViewById(R.id.yogaCount1Tv);
        yogaCount2Tv = findViewById(R.id.yogaCount2Tv);
        yogaCount3Tv = findViewById(R.id.yogaCount3Tv);
        yogaCount4Tv = findViewById(R.id.yogaCount4Tv);
        yogaCount5Tv = findViewById(R.id.yogaCount5Tv);
        yogaCount6Tv = findViewById(R.id.yogaCount6Tv);
        yogaTimerTv = findViewById(R.id.timerTxtView);
        yogaSessionTitle = findViewById(R.id.yogaSessionTitle);
        yogaName = findViewById(R.id.yoga_name);
        errorTv = findViewById(R.id.youtubePlayerErrorTv);

        startYogaBtn = findViewById(R.id.startYogaBtn);
        startYogaBtn.setEnabled(true);
        startYogaBtn.setBackgroundTintList(getColorStateList(R.color.teal_200));
        nextYogaBtn = findViewById(R.id.nextYogaBtn);
        nextYogaBtn.setEnabled(false);
        //startYogaBtn.setBackgroundTintList(getColorStateList(R.color.toolbar_dark));

        youTubePlayerView = findViewById(R.id.youtubePlayerView);
        yogaWebView = findViewById(R.id.yoga_video_webview);
        progressBar = findViewById(R.id.yogaLoadingProgrssbar);
        benifitsE = findViewById(R.id.benifitsE);
        benifitsH = findViewById(R.id.benifitsH);
        howE = findViewById(R.id.methodE);
        howH = findViewById(R.id.methodH);
        notE = findViewById(R.id.notToDoE);
        notH = findViewById(R.id.notToDoH);
        ban1 = findViewById(R.id.yogaConBan1);
        ban2 = findViewById(R.id.yogaConBan2);
        ban3 = findViewById(R.id.yogaConBan3);
        ban12 = findViewById(R.id.yogaConBan12);
        ban13 = findViewById(R.id.yogaConBan13);
        ban14 = findViewById(R.id.yogaConBan14);

        youTubePlayerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                mYouTubePlayer = youTubePlayer;
                youTubePlayer.setShowFullscreenButton(false);

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(YogaTrackingActivity.this, "Initialisation failed", Toast.LENGTH_SHORT).show();
                Log.e("youtube",youTubeInitializationResult.toString());
            }
        });

        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("hi"));
                    textToSpeech.setPitch(0.7f);
                    textToSpeech.setSpeechRate(1f);
                }
            }
        });


        adRequest = new AdRequest.Builder().build();

        onClickActivities();



    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showData(0);
                progressDialog.dismiss();
                showBannerAds();
            }
        },5000);
    }

    private void showBannerAds() {

        ban1.setVisibility(View.VISIBLE);
        ban2.setVisibility(View.VISIBLE);
        ban3.setVisibility(View.VISIBLE);
        ban12.setVisibility(View.VISIBLE);
        ban13.setVisibility(View.VISIBLE);
        ban14.setVisibility(View.VISIBLE);

        ban1.loadAd(adRequest);
        ban2.loadAd(adRequest);
        ban3.loadAd(adRequest);
        ban12.loadAd(adRequest);
        ban13.loadAd(adRequest);
        ban14.loadAd(adRequest);

    }

    private void onClickActivities() {

        stopVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech.isSpeaking()){
                    textToSpeech.stop();
                    stopVoiceBtn.setVisibility(View.GONE);
                }
            }
        });

        final int[] pos = {0};

        nextYogaBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                startYogaBtn.setEnabled(true);
                pos[0]++;
                currentPos = pos[0];
                showData(pos[0]);
                rewardedAdsCall();
                nextYogaBtn.setEnabled(false);
                nextYogaBtn.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
                yogaTimerTv.setText("05:00");
                switch (pos[0]){
                    case 1:
                        yogaCount2Tv.setBackgroundTintList(getColorStateList(R.color.teal_200));
                        break;
                    case 2:
                        yogaCount3Tv.setBackgroundTintList(getColorStateList(R.color.teal_200));
                        break;
                    case 3:
                        yogaCount4Tv.setBackgroundTintList(getColorStateList(R.color.teal_200));
                        break;
                    case 4:
                        yogaCount5Tv.setBackgroundTintList(getColorStateList(R.color.teal_200));
                        break;
                    case 5:
                        yogaCount6Tv.setBackgroundTintList(getColorStateList(R.color.teal_200));
                        break;
                }
            }
        });

        startYogaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tt = 60000*getYogaTime(pos[0]);
                initialiseRewardedAd();
                new CountDownTimer(tt, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long minute =millisUntilFinished/60000;
                        long sec = 60 - ((tt-millisUntilFinished)/1000)%60;
                        String time = new DecimalFormat("00").format(minute)+":"+new DecimalFormat("00").format(sec);
                        yogaTimerTv.setText(time);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onFinish() {
                        mYouTubePlayer.pause();
                        if (pos[0] == 5){
                            yogaTimerTv.setText("Finished!");
                            nextYogaBtn.setEnabled(false);
                            nextYogaBtn.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
                            alertExit("Yoga Session","Your session finished. Do You want to exit ?");
                            textToSpeech.speak("योग सत्र समाप्त हो गया है, बाहर निकलने के लिए हाँ बटन पर क्लिक करें।",TextToSpeech.QUEUE_FLUSH, null);
                            //Yoga session is finished ,Click yes button to exit.
                        }else{
                            stopVoiceBtn.setVisibility(View.VISIBLE);
                            textToSpeech.speak("इस योग के लिए दिया गया समय समाप्त हो गया है। नेक्स्ट बटन सक्रिय है आप क्लिक कर सकते हैं और अगले योग पर जा सकते हैं।",TextToSpeech.QUEUE_FLUSH, null);
                            //Time given for this yoga has finished. Next button is activated you can click and go to next yoga.
                            //After clicking next button you will shown a advertisement after which next yoga is shown automatically.
                            yogaTimerTv.setText("Done!");
                            nextYogaBtn.setEnabled(true);
                            nextYogaBtn.setBackgroundTintList(getColorStateList(R.color.teal_200));
                        }
                        progTo2.setBackgroundColor(getColor(R.color.teal_200));
                        switch (pos[0]){
                            case 0:
                                progTo2.setBackgroundColor(getColor(R.color.teal_200));
                                break;
                            case 1:
                                progTo3.setBackgroundColor(getColor(R.color.teal_200));
                                break;
                            case 2:
                                progTo4.setBackgroundColor(getColor(R.color.teal_200));
                                break;
                            case 3:
                                progTo5.setBackgroundColor(getColor(R.color.teal_200));
                                break;
                            case 4:
                                progTo6.setBackgroundColor(getColor(R.color.teal_200));
                                break;
                        }
                    }
                }.start();
                startYogaBtn.setEnabled(false);
            }
        });


    }

    @Override
    public void onBackPressed() {

        if (status){
            super.onBackPressed();
        }else{
            alertExit("Yoga Session","You have not completed the yoga session! Do you really want to exit?");
        }
    }

    private void databaseActivities() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Yoga");

        for (int i = 0;i<6;i++){
            databaseReference.child(yogaList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    YogaModal yogaModal = snapshot.getValue(YogaModal.class);
                    yogaModals.add(yogaModal);
                    //Log.e("yoga",snapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

//        databaseReference.orderByChild("type").limitToFirst(6).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                YogaModal yogaModal = dataSnapshot.getValue(YogaModal.class);
//
//                yogaModals.add(yogaModal);
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



    }

    private void showData(int position){

        YogaModal yogaModal = yogaModals.get(position);
        String time = new DecimalFormat("00").format(getYogaTime(position))+":"+new DecimalFormat("00").format(0);

        if (yogaModal != null){
            yogaSessionTitle.setText(yogaSessionModel.getSessionName());
            yogaTimerTv.setText(time);
            yogaName.setText(yogaModal.getYogaName());
            benifitsE.setText(yogaModal.getImpE());
            benifitsH.setText(yogaModal.getImpH());
            howE.setText(yogaModal.getHowE());
            howH.setText(yogaModal.getHowH());
            notE.setText(yogaModal.getNotE());
            notH.setText(yogaModal.getNotH());
            //loadVid(holder,checkForLink(yogaModal.getVid()));
            if (mYouTubePlayer!=null)
                mYouTubePlayer.cueVideo(checkForLink(yogaModal.getVid()));
            else
                errorTv.setVisibility(View.VISIBLE);
            if (position == 0) {
                stopVoiceBtn.setVisibility(View.VISIBLE);
                textToSpeech.speak("योग सत्र शुरू करने के लिए नीचे दिए गए स्टार्ट बटन पर क्लिक करें। पहला योग समाप्त करने के बाद नेक्स्ट बटन सक्रिय होगा। आप दिए गए सत्र में प्रत्येक योग के लिए तय किए गए किसी भी योग को नहीं छोड़ सकते।\n" +
                        "आप दिए गए यूट्यूब वीडियो को देख सकते हैं या योग के बारे में दी गई जानकारी को पढ़ सकते हैं।", TextToSpeech.QUEUE_FLUSH, null);
            }else {
                stopVoiceBtn.setVisibility(View.GONE);
            }
            //Click  on  start  button given below  to  begin  yoogaa  session. After finishing first yoga next button active hoga . you can not skip the any yoga decided for each yoga in the given session.
            //You can watch given youtube video or read the information give about the yoga.

        }

    }

    private int getYogaTime(int pos){
        switch (pos){
            case 0:
                return yogaSessionModel.getDy1();
            case 1:
                return yogaSessionModel.getDy2();
            case 2:
                return yogaSessionModel.getDy3();
            case 3:
                return yogaSessionModel.getDy4();
            case 4:
                return yogaSessionModel.getDy5();
            case 5:
                return yogaSessionModel.getDy6();
            default:
                return 5;
        }
    }

    public String checkForLink(String link){
        if (link.startsWith("https://youtube.com/playlist?list=")){
            link = ""+link.substring(link.indexOf('=')+1);
        } else if (link.startsWith("https://youtu.be/")) {
            link = ""+link.substring(link.lastIndexOf('/')+1);
        }
        return  link;
    }

    public void alertExit(String title,String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg).setTitle(title)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void progressDialogShow(String title,String Msg){

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(Msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressDialog.setMax(100);
        progressDialog.show();

    }

    private void initialiseRewardedAd(){
        RewardedAd.load(this, "ca-app-pub-8103108161786269/4669619278",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        //Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        //Log.d(TAG, "Ad was loaded.");
                    }
                });
    }

    public void rewardedAdsCall(){

//        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//            @Override
//            public void onAdShowedFullScreenContent() {
//                // Called when ad is shown.
//                //Log.d(TAG, "Ad was shown.");
//            }
//
//            @Override
//            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
//                // Called when ad fails to show.
//                //Log.d(TAG, "Ad failed to show.");
//            }
//
//            @Override
//            public void onAdDismissedFullScreenContent() {
//                // Called when ad is dismissed.
//                // Set the ad reference to null so you don't show the ad a second time.
//                //Log.d(TAG, "Ad was dismissed.");
//                mRewardedAd = null;
//            }
//        });

        if (mRewardedAd != null) {
            Activity activityContext = YogaTrackingActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    //Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();

                }
            });
        }

    }


}