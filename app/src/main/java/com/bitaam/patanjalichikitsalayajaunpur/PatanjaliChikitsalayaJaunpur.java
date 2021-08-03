package com.bitaam.patanjalichikitsalayajaunpur;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatanjaliChikitsalayaJaunpur extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }
}
