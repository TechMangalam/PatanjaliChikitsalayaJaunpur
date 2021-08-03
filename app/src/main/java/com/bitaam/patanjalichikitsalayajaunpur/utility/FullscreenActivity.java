package com.bitaam.patanjalichikitsalayajaunpur.utility;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

import uk.co.senab.photoview.PhotoViewAttacher;


public class FullscreenActivity extends AppCompatActivity {

    TouchImageView fullScreenImg;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        fullScreenImg = findViewById(R.id.fullScreenImgView);

        Intent intent = getIntent();
        String imgUrl = intent.getStringExtra("imgUrl");

        Picasso.get().load(imgUrl).into(fullScreenImg);


//        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());



    }

//    @Override
//    public boolean onTouchEvent(MotionEvent motionEvent) {
//        scaleGestureDetector.onTouchEvent(motionEvent);
//        return true;
//    }
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
//            mScaleFactor *= scaleGestureDetector.getScaleFactor();
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
//            fullScreenImg.setScaleX(mScaleFactor);
//            fullScreenImg.setScaleY(mScaleFactor);
//            return true;
//        }
//    }


}