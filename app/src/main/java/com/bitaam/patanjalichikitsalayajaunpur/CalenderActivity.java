package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Objects;

public class CalenderActivity extends AppCompatActivity {
    CalendarView calendarView;
    TextView activitiesContentTv,eventsContentTv;
    FirebaseUser user;
    ProgressBar calenderProgBar;
    AdView calenderBanAds;
    String phoneNo="na";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        phoneNo = intent.getStringExtra("phone");
        if (phoneNo == null){
            phoneNo="na";
        }

        calendarView = findViewById(R.id.calendar);
        activitiesContentTv = findViewById(R.id.activityContentTv);
        eventsContentTv = findViewById(R.id.eventContentTv);
        calenderProgBar = findViewById(R.id.calenderProgBar);
        calenderBanAds = findViewById(R.id.calenderBanAds);

        MobileAds.initialize(getApplicationContext());
        calenderBanAds.loadAd(new AdRequest.Builder().build());

        calenderActivities();

    }

    private void calenderActivities() {

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = ""+dayOfMonth+"-"+(month+1)+"-"+year;
                calenderProgBar.setVisibility(View.VISIBLE);
                databaseActivities(selectedDate);
            }
        });

    }

    private void databaseActivities(String selectedDate) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.calendar_scrollview), "Selected date is " + selectedDate, Snackbar.LENGTH_SHORT);
        snackbar.show();
        if (phoneNo.equals("na")) {
            snackbar = Snackbar.make(findViewById(R.id.calendar_scrollview), "No data available", Snackbar.LENGTH_LONG);
            snackbar.show();
            calenderProgBar.setVisibility(View.GONE);
        } else{
//            String phone = Objects.requireNonNull(user.getPhoneNumber());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserActivities").child(phoneNo);

            databaseReference.child("Activity").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = "There is no activity to show";
                if (snapshot.hasChild(selectedDate)) {
                    data = Objects.requireNonNull(snapshot.child(selectedDate).getValue()).toString();
                }
                activitiesContentTv.setText(data);
                calenderProgBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                calenderProgBar.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar.make(findViewById(R.id.calendar_scrollview), "Some error occurred try again", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });
        }
    }
}