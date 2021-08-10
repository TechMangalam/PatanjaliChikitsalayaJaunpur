package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.modals.PatientModel;
import com.bitaam.patanjalichikitsalayajaunpur.modals.UserModal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PatientManagementActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView imageview;
    private Button btnSelectImage,submitBtn;
    TextView currentDateTimeTxtView,hospitalTv,doctorTv,rotateImgTv;
    EditText nameEdt,phoneNoEdt,diseaseEdt;
    Spinner genderSpinner;
    String imgLink="";
    String ImgId,shop_name,doctor_name;
    UserModal userModal;
    String currDateTime;
    byte[] filesInBytes;
    Bitmap qImg;
    AlertDialog.Builder builder;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_management);

        Intent intent = getIntent();
        userModal = (UserModal) intent.getSerializableExtra("UserInfo");
        assert userModal != null;
        shop_name = userModal.getShopName();

        imageview = findViewById(R.id.reportImgView);
        btnSelectImage = findViewById(R.id.uploadReportBtn);
        submitBtn = findViewById(R.id.submitBtn);
        currentDateTimeTxtView = findViewById(R.id.DateEntry);
        nameEdt = findViewById(R.id.profileName);
        phoneNoEdt = findViewById(R.id.email);
        hospitalTv = findViewById(R.id.hospitalTxtView);
        doctorTv = findViewById(R.id.doctorTxtView);
        genderSpinner = findViewById(R.id.jobMenu);
        diseaseEdt = findViewById(R.id.diseaseEdt);
        rotateImgTv = findViewById(R.id.rotateImageTv);
        currDateTime = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(new Date())+"";
        currentDateTimeTxtView.setText("Date & Time - "+currDateTime );

        doctor_name = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();

        doctorTv.setText("Doctor : " +doctor_name);
        hospitalTv.setText("Hospital : "+shop_name);

        builder = new AlertDialog.Builder(this);
        onClickActivities();


    }

    private void onClickActivities() {

        ArrayAdapter<CharSequence> adapterJob = ArrayAdapter.createFromResource(getApplicationContext(),R.array.genderList,android.R.layout.simple_spinner_item);
        adapterJob.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapterJob);
        genderSpinner.setOnItemSelectedListener(this);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnSelectImage.getText().equals("Upload Report")){
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1000);
                    btnSelectImage.setText("Change Report");
                }else{
                    imageview.setVisibility(View.GONE);
                    rotateImgTv.setVisibility(View.GONE);
                    filesInBytes = null;
                    btnSelectImage.setText("Upload Report");
                    submitBtn.setEnabled(false);
                }
            }
        });

        rotateImgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qImg = rotateImage(qImg,90);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToFirebase(filesInBytes);
            }
        });

    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        filesInBytes = null;
        filesInBytes = compressImg(rotatedImg);
        imageview.setImageBitmap(rotatedImg);
        return rotatedImg;
    }

    private byte[] compressImg(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
        byte[] fileInBytes = baos.toByteArray();
        //filesInByte = baos.toByteArray();
        return fileInBytes;
    }

    private void uploadImageToFirebase(byte[] fileInBytes) {

        String imgId = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(new Date())+""+ FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+".jpg";
        ImgId = imgId;
        final StorageReference fileRef = FirebaseStorage.getInstance().getReference("Images").child(imgId);
        fileRef.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgLink = uri.toString();
                        Picasso.get().load(uri)
                                .into(imageview);
                        imageview.setVisibility(View.VISIBLE);
                        getDataAndUpload();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageview.setVisibility(View.GONE);
                rotateImgTv.setVisibility(View.GONE);
                btnSelectImage.setText("Upload Report");
                submitBtn.setEnabled(false);
                imageview.setImageURI(null);
                imgLink = null;
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Try again ! Failed Uploading Image",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataAndUpload() {

        String dateTime = currDateTime;
        String name = nameEdt.getText().toString();
        String phoneNo = phoneNoEdt.getText().toString();
        String gender = String.valueOf(genderSpinner.getSelectedItem());
        String disease = diseaseEdt.getText().toString();

        boolean flag = true;

        if (imgLink.isEmpty()){
            Toast.makeText(this, "Attach Report image !", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (dateTime.isEmpty()){
            Toast.makeText(this, "Date Error !Please close and reopen", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (name.isEmpty()){
            nameEdt.setError("Enter patient name");
            flag = false;
        }
        if (phoneNo.isEmpty()){
            phoneNoEdt.setError("Enter patient phone no");
            flag = false;
        }
        if (gender.isEmpty()){
            Toast.makeText(this, "Select Gender of patient", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (disease.isEmpty()){
            diseaseEdt.setError("Enter disease of patient");
            flag = false;
        }

        if (flag){
            updateToFirebase(dateTime,name,phoneNo,gender,disease,imgLink);
        }

    }

    private void updateToFirebase(String dateTime, String name, String phoneNo, String gender, String disease, String imgLink) {

        PatientModel patientModel = new PatientModel();
        patientModel.setDateTime(dateTime);
        patientModel.setName(name);
        patientModel.setPhoneNo(phoneNo);
        patientModel.setGender(gender);
        patientModel.setDisease(disease);
        patientModel.setReportImg(imgLink);
        patientModel.setDoctor(doctor_name);
        patientModel.setHospital(shop_name);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Patients");
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(patientModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AlertExit();
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageview.setVisibility(View.GONE);
                rotateImgTv.setVisibility(View.GONE);
                btnSelectImage.setText("Upload Report");
                imageview.setImageURI(null);
                submitBtn.setEnabled(false);
                Toast.makeText(PatientManagementActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void AlertExit(){

        builder.setMessage("Patient record added Successfully!").setTitle("Confirmation")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                }).setCancelable(false);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imgUri = data.getData();
                Bitmap bmp = null;
                try{
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                    qImg = bmp;
                }catch (Exception e){}
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
//                //byte[] fileInBytes = baos.toByteArray();
                filesInBytes = compressImg(qImg);
                imageview.setVisibility(View.VISIBLE);
                rotateImgTv.setVisibility(View.VISIBLE);
                imageview.setImageBitmap(qImg);

                submitBtn.setEnabled(true);

                //uploadImageToFirebase(fileInBytes);
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}