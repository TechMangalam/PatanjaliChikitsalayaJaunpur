package com.bitaam.patanjalichikitsalayajaunpur;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bitaam.patanjalichikitsalayajaunpur.modals.UserModal;
import com.bitaam.patanjalichikitsalayajaunpur.ui.AskDoctor.AskDoctorFragment;
import com.bitaam.patanjalichikitsalayajaunpur.ui.Nuske.NuskeFragment;
import com.bitaam.patanjalichikitsalayajaunpur.ui.home.HomeFragment;
import com.bitaam.patanjalichikitsalayajaunpur.ui.upchar.UpcharFragment;
import com.bitaam.patanjalichikitsalayajaunpur.ui.yoga.YogaFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.text.DecimalFormat;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    Fragment active;
    boolean homeFlag = true;
    Toolbar toolbar;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;
    UserModal userModal;

    FirebaseAuth mAuth;
    FirebaseUser user;
    boolean logoutFlag = false;
    String msg="";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getUserInfo();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        builder = new AlertDialog.Builder(this);

        toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My Upchaar");

        navView = findViewById(R.id.nav_view);
        active  = new HomeFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment,active,"TAG").commit();
        navView.setSelectedItemId(R.id.navigation_home);

        onClickActivities();

        new CountDownTimer(2*60000,1000){
            public void onTick(long millisUntilFinished) {

                long minute =millisUntilFinished/60000;
                long sec = 60 - ((3*60000-millisUntilFinished)/1000)%60;
                String time = new DecimalFormat("00").format(minute)+":"+new DecimalFormat("00").format(sec);
                msg = "You can logout after "+time+" minutes";

            }

            @Override
            public void onFinish() {
                logoutFlag = true;
            }


        }.start();

        getDynamicLinkData();


    }

    private void getDynamicLinkData() {

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null){
                            deepLink = pendingDynamicLinkData.getLink();
                            if (deepLink!=null && (deepLink.toString()).equals("https://myupchaar.bitaam.com/nuske")){
                                navView.setSelectedItemId(R.id.navigation_exercise);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Dynamic", "getDynamicLink:onFailure", e);
            }
        });

    }

    private void onClickActivities(){

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = new HomeFragment();


                switch(item.getItemId()){

                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        homeFlag = true;
                        toolbar.setTitle("My Upchaar");
                        break;
                    case R.id.navigation_upchar:
                        fragment = new UpcharFragment();
                        homeFlag = false;
                        toolbar.setTitle("Upchaar");
                        //if (interstitialAd.isLoaded())
                            //interstitialAd.show();
                        break;
                    case R.id.navigation_ask_doctor:
                        fragment = new AskDoctorFragment();
                        homeFlag = false;
                        toolbar.setTitle("Ask Doctor");
                        //if (interstitialAd.isLoaded())
                            //interstitialAd.show();
                        break;
                    case R.id.navigation_yoga:
                        fragment = new YogaFragment();
                        homeFlag = false;
                        toolbar.setTitle("Yoga");
                        //if (interstitialAd.isLoaded())
                            //interstitialAd.show();
                        break;
                    case R.id.navigation_exercise:
                        fragment = new NuskeFragment();
                        homeFlag = false;
                        toolbar.setTitle("Nuske");
                        //if (interstitialAd.isLoaded())
                            //interstitialAd.show();
                        break;

                }

                Bundle bundle = new Bundle();
                bundle.putSerializable("UserInfo",userModal);
                //assert fragment != null;
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,fragment,"TAG").remove(active).commit();
                active = fragment;//.hide(active)
                return true;
            }
        });

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        navView.setSelectedItemId(R.id.navigation_home);

    }

    @Override
    public void onBackPressed() {

        if (homeFlag)
        {
            //if (interstitialAd.isLoaded())
             //   interstitialAd.show();
            finish();
        }else{
            navView.setSelectedItemId(R.id.navigation_home);
            //if (interstitialAd.isLoaded())
                //interstitialAd.show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menumain,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.log_out)
        {
            if (logoutFlag){
                AlertExit();
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else{
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

        }else if(id == R.id.profile){
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }else if(id == R.id.share){
            shareApp();
        }else if(id == R.id.feedback){
            startActivity(new Intent(getApplicationContext(),FeedbackActivity.class));

        }else if(id == R.id.trackActivity){
            String phone = Objects.requireNonNull(user.getPhoneNumber());
            Intent intent = new Intent(getApplicationContext(),CalenderActivity.class);
            intent.putExtra("phone",phone.substring(3));
            startActivity(intent);

        }

        return true;
    }

    private void shareApp(){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share MyUpchaar");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("share");
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                intent.putExtra(android.content.Intent.EXTRA_TEXT, snapshot.getValue(String.class));

                startActivity(Intent.createChooser(intent, "Select app to share"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void AlertExit(){

        builder.setMessage("Do you really want to Logout?").setTitle("Confirmation")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(user!=null){
                            mAuth.signOut();
                            finish();
                            startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                        }

                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);



    }

    private void getUserInfo(){

        progressDialogShow("Creating profile","Please wait...");

        final String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModal = snapshot.getValue(UserModal.class);
                //Log.e("userInfo",snapshot.getValue().toString());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if (Objects.equals(snapshot.getKey(), uid)){
//                    userModal = snapshot.getValue(UserModal.class);
//                    Log.e("userInfo",snapshot.getValue().toString());
//                    progressDialog.dismiss();
//
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


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

}