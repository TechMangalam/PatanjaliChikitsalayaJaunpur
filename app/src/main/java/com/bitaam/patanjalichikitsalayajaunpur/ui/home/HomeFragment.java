package com.bitaam.patanjalichikitsalayajaunpur.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitaam.patanjalichikitsalayajaunpur.PatientListActivity;
import com.bitaam.patanjalichikitsalayajaunpur.PatientManagementActivity;
import com.bitaam.patanjalichikitsalayajaunpur.R;
import com.bitaam.patanjalichikitsalayajaunpur.SearchingActivity;
import com.bitaam.patanjalichikitsalayajaunpur.YogaSessionsListActivity;
import com.bitaam.patanjalichikitsalayajaunpur.YogaTrackingActivity;
import com.bitaam.patanjalichikitsalayajaunpur.adapters.DailyYogaAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.adapters.GeneralUpcharAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.adapters.YogaAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.modals.UpcharModal;
import com.bitaam.patanjalichikitsalayajaunpur.modals.UserModal;
import com.bitaam.patanjalichikitsalayajaunpur.modals.YogaModal;
import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    UserModal userModal;
    RecyclerView dailyYogRecycler,generalUpcharRecycler;
    CardView itemSearchCard,yogaSessionCard;
    FloatingActionButton addPatientActionBtn;
    CarouselView carouselView;
    View parentView;
    ScrollView homeScroll;
    ArrayList<String> infoImgUrls;

    SharedPreferences sharedPreferences;

    public HomeFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        dailyYogRecycler = root.findViewById(R.id.dailyYogRecycler);
        itemSearchCard = root.findViewById(R.id.patientManagementCard);
        generalUpcharRecycler = root.findViewById(R.id.generalUpcharRecycler);
        addPatientActionBtn = root.findViewById(R.id.addPatientActionBtn);
        yogaSessionCard = root.findViewById(R.id.yogaSessionCard);
        infoImgUrls = new ArrayList<>();
        sharedPreferences = requireActivity().getSharedPreferences("HomeInfo", Context.MODE_PRIVATE);

        infoImgUrls.add(sharedPreferences.getString("img1","https://cdn.dnaindia.com/sites/default/files/styles/full/public/2019/10/04/873153-ayurveda-istock.jpg"));
        infoImgUrls.add(sharedPreferences.getString("img2","https://www.kindpng.com/picc/m/103-1032545_nature-of-patient-and-doctor-hd-png-download.png"));
        infoImgUrls.add(sharedPreferences.getString("img3","https://akm-img-a-in.tosshub.com/indiatoday/sunsetyoga-2_647_062115121022.jpg?Q7x3aPFYhLV6E2CgD7oXmSdjoh5wnAiq&size=1200:675"));
        infoImgUrls.add(sharedPreferences.getString("img4","https://innohealthmagazine.com/wp-content/uploads/2017/11/Ayurveda-Mothe-of-all-healings.jpg"));


        carouselView = root.findViewById(R.id.carouselView);
        carouselView.setPageCount(infoImgUrls.size());
        carouselView.setImageListener(imageListener);

        getHomeInfoCarouselData();

        parentView = requireActivity().findViewById(R.id.nav_view);
        homeScroll = root.findViewById(R.id.scroll_home);

        homeScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if ((oldScrollY-scrollY)>=0){
                    parentView.setVisibility(View.VISIBLE);
                }else {
                    parentView.setVisibility(View.GONE);
                }
            }
        });

        dailyYogRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        generalUpcharRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        getUserInfo();

        databaseActivities();


        onClickAndTouchEvent();


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null) {
            new AlertDialog.Builder(requireContext()).setTitle("Internet not connected").setMessage("Turn on internet and reopen app.")
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            requireActivity().finish();
                        }
                    }).show();
        }
        if (parentView.getVisibility() != View.VISIBLE){
            parentView.setVisibility(View.VISIBLE);
        }
    }

    private void onClickAndTouchEvent(){

        yogaSessionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), YogaSessionsListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        itemSearchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PatientListActivity.class);
                intent.putExtra("userInfo",userModal);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        addPatientActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PatientManagementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("UserInfo",userModal);
                startActivity(intent);
            }
        });

    }

    private void databaseActivities() {

        DailyYogaAdapter yogaAdapter = new DailyYogaAdapter(dailyYogRecycler,getContext(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<YogaModal>());
        dailyYogRecycler.setAdapter(yogaAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Yoga");
        databaseReference.keepSynced(true);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {

                YogaModal yogaModal = dataSnapshot.getValue(YogaModal.class);

                assert yogaModal != null;
                if (yogaModal.getCategory().equals("trending")){
                    String fileName = dataSnapshot.getKey();
                    String iconUrl = "na";
                    if (dataSnapshot.hasChild("iconUrl")){
                        iconUrl = String.valueOf(dataSnapshot.child("iconUrl").getValue());
                    }
                    ((DailyYogaAdapter) Objects.requireNonNull(dailyYogRecycler.getAdapter())).update(fileName,iconUrl,yogaModal);
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.onDisconnect();

        GeneralUpcharAdapter generalUpcharAdapter = new GeneralUpcharAdapter(generalUpcharRecycler,getContext(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<UpcharModal>());
        generalUpcharRecycler.setAdapter(generalUpcharAdapter);

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Upchar");
        databaseReference1.keepSynced(true);
        databaseReference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {

                UpcharModal upcharModal = dataSnapshot.getValue(UpcharModal.class);

                assert upcharModal != null;
                if (upcharModal.getCategory().equals("all")){
                    String fileName = dataSnapshot.getKey();
                    String iconUrl = "na";
                    if (dataSnapshot.hasChild("iconUrl")){
                        iconUrl = String.valueOf(dataSnapshot.child("iconUrl").getValue());
                    }
                    ((GeneralUpcharAdapter) Objects.requireNonNull(generalUpcharRecycler.getAdapter())).update(fileName,iconUrl,upcharModal);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @com.google.firebase.database.annotations.Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference1.onDisconnect();

    }

    private void getUserInfo(){
        final String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModal = snapshot.getValue(UserModal.class);
                Log.e("userInfo",snapshot.getValue().toString());
                if (userModal.getRole().equals("Doctor or Vaidya") || userModal.getRole().equals("Developer")){
                    addPatientActionBtn.setVisibility(View.VISIBLE);
                }

                if(userModal.getRole().equals("Patanjali Chikitsalaya or store")){
                    addPatientActionBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        assert getArguments() != null;
//        userModal = (UserModal)getArguments().getSerializable("UserInfo");
//        assert userModal != null;
//        if (userModal.getRole().equals("Doctor or Vaidya") || userModal.getRole().equals("Developer")){
//            addPatientActionBtn.setVisibility(View.VISIBLE);
//        }
//
//        if(userModal.getRole().equals("Patanjali Chikitsalaya or store")){
//            addPatientActionBtn.setVisibility(View.VISIBLE);
//        }

    }

    private void getHomeInfoCarouselData(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("InfoImages");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //Log.e("info", Objects.requireNonNull(dataSnapshot.getValue(String.class)));
                    infoImgUrls.add(Objects.requireNonNull(dataSnapshot.getValue(String.class)));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//
//            }
//        },2000);

    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.get().load(infoImgUrls.get(position)).into(imageView);

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("img1",infoImgUrls.get(4));
        editor.putString("img2",infoImgUrls.get(5));
        editor.putString("img3",infoImgUrls.get(6));
        editor.putString("img4",infoImgUrls.get(7));
        editor.apply();
    }
}