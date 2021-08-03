package com.bitaam.patanjalichikitsalayajaunpur.ui.upchar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitaam.patanjalichikitsalayajaunpur.AddUpcharActivity;
import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.bitaam.patanjalichikitsalayajaunpur.adapters.UpcharAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.modals.UpcharModal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class UpcharFragment extends Fragment {

    RecyclerView upcharRecycler;

    DatabaseReference databaseReference;

    FloatingActionButton addUpcharActionBtn;
    String role="";

    View parentView;

    public UpcharFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_upchar, container, false);

        assert getArguments() != null;
        role = getArguments().getString("UserRole");



        upcharRecycler = root.findViewById(R.id.upcharRecycler);
        upcharRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        addUpcharActionBtn = root.findViewById(R.id.addUpcharActionBtn);

        if(role.equals("Developer")){
            addUpcharActionBtn.setVisibility(View.VISIBLE);
        }

        addUpcharActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), AddUpcharActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        databaseActivities();

        parentView = requireActivity().findViewById(R.id.nav_view);
        //homeScroll = root.findViewById(R.id.scroll_home);

        upcharRecycler.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if ((oldScrollY-scrollY)>=0){
                    parentView.setVisibility(View.VISIBLE);
                }else {
                    parentView.setVisibility(View.GONE);
                }
            }
        });

        return root;
    }

    private void databaseActivities() {

        UpcharAdapter upcharAdapter = new UpcharAdapter(upcharRecycler,new ArrayList<String>(),new ArrayList<String>(),new ArrayList<UpcharModal>(),getContext());
        upcharRecycler.setAdapter(upcharAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Upchar");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {

                UpcharModal upcharModal = dataSnapshot.getValue(UpcharModal.class);

                String fileName = dataSnapshot.getKey();
                String iconUrl = "na";
                if (dataSnapshot.hasChild("iconUrl")){
                    iconUrl = String.valueOf(dataSnapshot.child("iconUrl").getValue());
                }
                ((UpcharAdapter) upcharRecycler.getAdapter()).update(fileName,iconUrl,upcharModal,role);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {

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