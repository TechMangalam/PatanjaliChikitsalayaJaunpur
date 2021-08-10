package com.bitaam.patanjalichikitsalayajaunpur.ui.Nuske;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.AddNuskeActivity;
import com.bitaam.patanjalichikitsalayajaunpur.AddYogaActivity;
import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.bitaam.patanjalichikitsalayajaunpur.adapters.NuskeAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.adapters.YogaAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.modals.NuskeModal;
import com.bitaam.patanjalichikitsalayajaunpur.modals.UserModal;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaModal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;


public class NuskeFragment extends Fragment {

    RecyclerView ghareluNuskeRecycler;
    DatabaseReference databaseReference;
    FloatingActionButton addNuskeActionBtn;
    String role="User";
    UserModal userModal;


    public NuskeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nuske, container, false);

        assert getArguments() != null;
        userModal = (UserModal) getArguments().getSerializable("UserInfo");
        assert userModal != null;
        role = userModal.getRole();

        ghareluNuskeRecycler = view.findViewById(R.id.nuskeRecycler);
        ghareluNuskeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        addNuskeActionBtn = view.findViewById(R.id.addNuskeActionBtn);

        if(role.equals("Developer")){
            addNuskeActionBtn.setVisibility(View.VISIBLE);
        }

        databaseActivities();
        onClickActivities();

        return view;
    }


    private void databaseActivities() {

        NuskeAdapter nuskeAdapter = new NuskeAdapter(ghareluNuskeRecycler,getContext(),new ArrayList<NuskeModal>());
        ghareluNuskeRecycler.setAdapter(nuskeAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Nuske");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                NuskeModal nuskeModal = dataSnapshot.getValue(NuskeModal.class);

                ((NuskeAdapter) ghareluNuskeRecycler.getAdapter()).update(nuskeModal,role);


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
                Intent intent = new Intent(getContext(), AddNuskeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

}