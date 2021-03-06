package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
//    public static final int STARTUP_DELAY = 50;
//    public static final int ANIM_ITEM_DURATION = 1000;
//    public static final int ITEM_DELAY = 100;

    private boolean animationStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }else {
            startActivity(new Intent(getApplicationContext(),SignInActivity.class));
        }
        finish();

//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if(mAuth.getCurrentUser()!=null){
//                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
//                    finish();
//                }else {
//                    startActivity(new Intent(getApplicationContext(),SignInActivity.class));
//                    finish();
//                }
//
//            }
//        },1300);

    }
    

}