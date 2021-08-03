package com.bitaam.patanjalichikitsalayajaunpur.ui.yoga;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.proto.ProtoOutputStream;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.AddUpcharActivity;
import com.bitaam.patanjalichikitsalayajaunpur.AddYogaActivity;
import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.bitaam.patanjalichikitsalayajaunpur.TopicAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.adapters.YogaAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaModal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class YogaFragment extends Fragment {

    RecyclerView yogaRecycler;

    DatabaseReference databaseReference;

    FloatingActionButton addYogaActionBtn;
    String role="";

    View parentView;

    public YogaFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_yoga, container, false);

        assert getArguments() != null;
        role = getArguments().getString("UserRole");

        yogaRecycler = view.findViewById(R.id.yogaRecycler);
        yogaRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        addYogaActionBtn = view.findViewById(R.id.addYogaActionBtn);

        if(role.equals("Developer")){
            addYogaActionBtn.setVisibility(View.VISIBLE);
        }

        addYogaActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddYogaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        databaseActivities();

        parentView = requireActivity().findViewById(R.id.nav_view);
        //homeScroll = root.findViewById(R.id.scroll_home);

        yogaRecycler.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if ((oldScrollY-scrollY)>=0){
                    parentView.setVisibility(View.VISIBLE);
                }else {
                    parentView.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    private void databaseActivities() {

        YogaAdapter yogaAdapter = new YogaAdapter(yogaRecycler,getContext(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<YogaModal>());
        yogaRecycler.setAdapter(yogaAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Yoga");

        databaseReference.orderByChild("type").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                YogaModal yogaModal = dataSnapshot.getValue(YogaModal.class);

                String fileName = dataSnapshot.getKey();
                String iconUrl = "na";
                if (dataSnapshot.hasChild("iconUrl")){
                    iconUrl = String.valueOf(dataSnapshot.child("iconUrl").getValue());
                }
                ((YogaAdapter) yogaRecycler.getAdapter()).update(fileName,iconUrl,yogaModal,role);


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

    public String checkForLink(String link){
        if (link.startsWith("https://youtube.com/playlist?list=")){
            link = ""+link.substring(link.indexOf('=')+1);
        } else if (link.startsWith("https://youtu.be/")) {
            link = ""+link.substring(link.lastIndexOf('/')+1);
        }
        return  link;
    }


}