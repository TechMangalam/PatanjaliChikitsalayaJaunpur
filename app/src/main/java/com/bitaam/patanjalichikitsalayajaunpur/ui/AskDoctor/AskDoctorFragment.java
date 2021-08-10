package com.bitaam.patanjalichikitsalayajaunpur.ui.AskDoctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitaam.patanjalichikitsalayajaunpur.DoubtAnswerPosting;
import com.bitaam.patanjalichikitsalayajaunpur.DoubtQuestionPosting;
import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.bitaam.patanjalichikitsalayajaunpur.adapters.DoubtQueAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.modals.DoubtQuestionModel;
import com.bitaam.patanjalichikitsalayajaunpur.modals.UserModal;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AskDoctorFragment extends Fragment {

    RecyclerView globalRecycler;
    FloatingActionButton askFloatingBtn;
    ArrayList<DoubtQuestionModel> list;
    ArrayList<String> queId = new ArrayList<String>();
    DoubtQueAdapter doubtQueAdapter;
    DatabaseReference databaseReference;
    UserModal currUserInfo;
    String role="User";
    FirebaseAuth mAuth;
    Spinner spinner;
    View parentView;



    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_ask_doctor, container, false);

        assert getArguments() != null;
        currUserInfo = (UserModal)getArguments().getSerializable("UserInfo");
        assert currUserInfo != null;
        role = currUserInfo.getRole();

        globalRecycler = root.findViewById(R.id.queChatRecycler);

        askFloatingBtn = root.findViewById(R.id.askQuestion);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        globalRecycler.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        spinner = root.findViewById(R.id.switch_question_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),R.array.question_type,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("My Questions")){
                    databaseActivity(false);
                }else{
                    databaseActivity(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        parentView = requireActivity().findViewById(R.id.nav_view);
        //homeScroll = root.findViewById(R.id.scroll_home);

        globalRecycler.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if ((oldScrollY-scrollY)>=0){
                    parentView.setVisibility(View.VISIBLE);
                }else {
                    parentView.setVisibility(View.GONE);
                }
            }
        });

        MobileAds.initialize(requireContext());
        AdView adView = root.findViewById(R.id.ask_doctor_ads);
        adView.loadAd(new AdRequest.Builder().build());
        adView.setVisibility(View.VISIBLE);

        return root;
    }

    private void onClickEvents(){
        
        askFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), DoubtQuestionPosting.class);
                //Bundle bundle = new Bundle();
                startActivity(intent);
            }
        });

    }

    private void addToMyQuestions(final String qId) {
        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Community").child("doubts");

        databaseReference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(qId)){
                    if(snapshot.hasChild("nUpvotes")){
                        if (snapshot.child("nUpvotes").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Toast.makeText(getContext(),"Already upvoted!",Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            upvoting(qId);
                            databaseReference1.child(qId).child("nUpvotes")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(1);
                        }
                    }else{
                        upvoting(qId);
                        databaseReference1.child(qId).child("nUpvotes")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(1);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    private void upvoting(String qId) {



        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("MyQuestions");


        databaseReference.child(qId).setValue("added").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Upvoted ,Added to MyQuestions", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        onClickEvents();
    }

    public void databaseActivity(Boolean type) {

        doubtQueAdapter = new DoubtQueAdapter(globalRecycler, getContext(), new ArrayList<DoubtQuestionModel>());

        globalRecycler.setAdapter(doubtQueAdapter);

        doubtQueAdapter.setOnItemClickListener(new DoubtQueAdapter.OnItemClickListener() {
            @Override
            public void onUpvoted(int position) {

                addToMyQuestions(queId.get(position));
            }

            @Override
            public void onCommented(int position) {

                Intent intent = new Intent(getContext(), DoubtAnswerPosting.class);
                Bundle bundle = new Bundle();
                bundle.putString("qId", queId.get(position));
                bundle.putSerializable("UserInfo",currUserInfo);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onDeleted(DoubtQuestionModel model) {

                deleteTopicFromDatabase(model);
            }

            @Override
            public void onShowHide(DoubtQuestionModel model) {

                showHideTopicItems(model);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Community").child("doubts");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                queId.add(snapshot.getKey().toString());

                DoubtQuestionModel doubtQuestionModel = snapshot.getValue(DoubtQuestionModel.class);

                assert doubtQuestionModel != null;
                if (doubtQuestionModel.getAuth().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()) || doubtQuestionModel.isVisibility()){

                    long nComment = 0;
                    if (snapshot.hasChild("Comments")){
                        nComment = snapshot.child("Comments").getChildrenCount();
                    }
                    long nUpvote = 0;
                    if (snapshot.hasChild("nUpvotes")){
                        nUpvote = snapshot.child("nUpvotes").getChildrenCount();
                    }


                    /*doubtQuestionModel.setName(snapshot.child("name").getValue().toString());
                    doubtQuestionModel.setProfileImgUrl((String)snapshot.child("profileImgUrl").getValue());
                    doubtQuestionModel.setPostDate(snapshot.child("postDate").getValue().toString());
                    doubtQuestionModel.setQue(snapshot.child("que").getValue().toString());
                    doubtQuestionModel.setQueImgUrl(snapshot.child("queImgUrl").getValue().toString());
                    doubtQuestionModel.setVisibility((boolean)snapshot.child("visibility").getValue());*/
                    list.add(doubtQuestionModel);
                    if (type){
                        ((DoubtQueAdapter) Objects.requireNonNull(globalRecycler.getAdapter())).update(doubtQuestionModel,nComment,nUpvote);
                    }else{
                        if (doubtQuestionModel.getAuth().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())){
                            ((DoubtQueAdapter) Objects.requireNonNull(globalRecycler.getAdapter())).update(doubtQuestionModel,nComment,nUpvote);
                        }
                    }

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void showHideTopicItems(final DoubtQuestionModel model) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Community").child("doubts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //Toast.makeText(TopicActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    if (dataSnapshot.getValue(DoubtQuestionModel.class).getQue().equals(model.getQue()) && dataSnapshot.getValue(DoubtQuestionModel.class).getPostDate().equals(model.getPostDate())){
                        dataSnapshot.getRef().child("visibility").setValue(model.isVisibility()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (model.isVisibility()){
                                    doubtQueAdapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(), "Visibility Changed ,Now your topic is public", Toast.LENGTH_SHORT).show();
                                }else{
                                    doubtQueAdapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(), "Visibility Changed ,Now your topic is Hidden", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteTopicFromDatabase(final DoubtQuestionModel model) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Community").child("doubts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //Toast.makeText(TopicActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    if (dataSnapshot.getValue(DoubtQuestionModel.class).getQue().equals(model.getQue()) && dataSnapshot.getValue(DoubtQuestionModel.class).getPostDate().equals(model.getPostDate())){
                        dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                doubtQueAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}