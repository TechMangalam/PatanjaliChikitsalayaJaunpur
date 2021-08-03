package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.bitaam.patanjalichikitsalayajaunpur.adapters.YogaAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.adapters.YogaSessionAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaModal;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaSessionModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class YogaSessionsListActivity extends AppCompatActivity {

    RecyclerView yogaSessionRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_sessions_list);

        MobileAds.initialize(this);

        yogaSessionRecycler = findViewById(R.id.yoga_session_recycler);
        yogaSessionRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

        databaseActivities();

        AdView adView = findViewById(R.id.yoga_session_list_ads);
        adView.loadAd(new AdRequest.Builder().build());
        adView.setVisibility(View.VISIBLE);

    }

    private void databaseActivities() {

        YogaSessionAdapter yogaSessionAdapter = new YogaSessionAdapter(yogaSessionRecycler,getApplicationContext(),new ArrayList<>());
        yogaSessionRecycler.setAdapter(yogaSessionAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("YogaSessions");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                YogaSessionModel yogaSessionModel = dataSnapshot.getValue(YogaSessionModel.class);

                ((YogaSessionAdapter) Objects.requireNonNull(yogaSessionRecycler.getAdapter())).update(yogaSessionModel,"viewer");


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}