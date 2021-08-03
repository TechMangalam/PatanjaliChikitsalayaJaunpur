package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitaam.patanjalichikitsalayajaunpur.modals.PatientModel;
import com.bitaam.patanjalichikitsalayajaunpur.utility.FullscreenActivity;
import com.squareup.picasso.Picasso;

public class ShowPatientDetailActivity extends AppCompatActivity {

    TextView dateTv,hospitalTv,doctorTv,nameTv,mobileNoTv,genderTv,diseaseTv;
    ImageView reportImgView;
    PatientModel patientModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient_detail);

        Intent intent = getIntent();
        patientModel = (PatientModel)intent.getSerializableExtra("patientInfo");

        dateTv = findViewById(R.id.DateEntry);
        hospitalTv = findViewById(R.id.hospitalTxtView);
        doctorTv = findViewById(R.id.doctorTxtView);
        nameTv = findViewById(R.id.profileNametv);
        mobileNoTv = findViewById(R.id.phoneProfile);
        genderTv = findViewById(R.id.genderTitle);
        diseaseTv = findViewById(R.id.diseaseTv);
        reportImgView = findViewById(R.id.reportImgView);

        displayPatientData(patientModel);

    }

    @SuppressLint("SetTextI18n")
    private void displayPatientData(PatientModel patientModel) {

        dateTv.setText("Date & Time - "+patientModel.getDateTime());
        hospitalTv.setText("Hospital : "+patientModel.getHospital());
        doctorTv.setText("Doctor : "+patientModel.getDoctor());
        nameTv.setText("Name : "+patientModel.getName());
        mobileNoTv.setText("Phone No : "+patientModel.getPhoneNo());
        genderTv.setText("Gender : "+patientModel.getGender());
        diseaseTv.setText(patientModel.getDisease());
        Picasso.get().load(patientModel.getReportImg()).into(reportImgView);
        reportImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("imgUrl",patientModel.getReportImg());
                startActivity(intent);
            }
        });

    }

}