package com.bitaam.patanjalichikitsalayajaunpur.utility;

import android.content.Intent;
import android.os.Bundle;
import android.view.ScaleGestureDetector;

import androidx.appcompat.app.AppCompatActivity;

import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.squareup.picasso.Picasso;


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