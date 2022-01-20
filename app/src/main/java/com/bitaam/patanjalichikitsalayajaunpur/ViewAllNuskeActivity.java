package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bitaam.patanjalichikitsalayajaunpur.adapters.NuskeAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.modals.NuskeModal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class ViewAllNuskeActivity extends AppCompatActivity {

    RecyclerView ghareluNuskeRecycler;
    DatabaseReference databaseReference;
    FloatingActionButton addNuskeActionBtn;
    String role="User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_nuske);

        ghareluNuskeRecycler = findViewById(R.id.nuskeRecycler);
        ghareluNuskeRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        addNuskeActionBtn = findViewById(R.id.addNuskeActionBtn);

        databaseActivities();
        onClickActivities();

    }

    private void databaseActivities() {

        NuskeAdapter nuskeAdapter = new NuskeAdapter(ghareluNuskeRecycler,getApplicationContext(),new ArrayList<NuskeModal>());
        ghareluNuskeRecycler.setAdapter(nuskeAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Nuske");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                NuskeModal nuskeModal = dataSnapshot.getValue(NuskeModal.class);
                assert nuskeModal != null;
                if (nuskeModal.isVisibility() || role.equals("Developer")) {
                    ((NuskeAdapter) Objects.requireNonNull(ghareluNuskeRecycler.getAdapter())).update(nuskeModal, role);
                }

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

    private void onClickActivities(){

        addNuskeActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNuskeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

}