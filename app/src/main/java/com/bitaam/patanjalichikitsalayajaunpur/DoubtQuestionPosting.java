package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.modals.DoubtQuestionModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class DoubtQuestionPosting extends AppCompatActivity {

    TextView post,attach,postDate;
    TextInputEditText question;
    ImageView queImg;
    ProgressBar progressBar;
    SwitchMaterial visibilitySwitch;
    FirebaseUser user;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    //String couName;
    String queImgUrls;
    String imgUrlProf;
    byte[] filesInBytes;
    String ImgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubt_question_posting);

        queImg = findViewById(R.id.queImg);

        question = findViewById(R.id.question);
        question.requestFocus();
        progressBar = findViewById(R.id.queImgPro);
        visibilitySwitch = findViewById(R.id.visibility_switch);

        postDate = findViewById(R.id.date);
        post = findViewById(R.id.postQue);
        attach = findViewById(R.id.attachImg);

        databaseReference = FirebaseDatabase.getInstance().getReference("Community").child("doubts");
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages");
        user = FirebaseAuth.getInstance().getCurrentUser();
        queImgUrls = "na";
        imgUrlProf = "na";

//        StorageReference ref = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+".jpg");
//        if (ref != null){
//            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    imgUrlProf = uri.toString();
//                }
//            });
//        }

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attach.getText().equals("Attach Image")){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,1000);
                    attach.setText("Remove Image");
                }else if (attach.getText().equals("Remove Image")){
                    StorageReference fileRef = FirebaseStorage.getInstance().getReference("Images").child(ImgId);
                    if (fileRef!=null){
                        fileRef.delete();
                    }
                    queImgUrls = "na";
                    queImg.setImageURI(null);
                    queImg.setVisibility(View.GONE);
                    attach.setText("Attach Image");
                }

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setEnabled(false);

                postQuestion();
                queImgUrls = null;
                Toast.makeText(getApplicationContext(),"Question posted successfully",Toast.LENGTH_SHORT).show();
                finish();

            }
        });



    }

    private void postQuestion() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String disName = user.getDisplayName();
        String proUrl = "na";
        if (user.getPhotoUrl() != null) {
            proUrl = user.getPhotoUrl().toString();
        }
        boolean visibility = true;
        if (visibilitySwitch.isChecked()){
            visibility = false;
        }
        String poDat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        //new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault()).format(new Date())
        String que = question.getText().toString().trim();
        String queUrl = queImgUrls;
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(new DoubtQuestionModel(que,poDat,proUrl,queUrl,disName,visibility,user.getUid()));
        addToMyQuestions(key);
    }

    private void addToMyQuestions(String qId) {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("MyQuestions");

            databaseReference1.child(qId).setValue("added");


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                progressBar.setVisibility(View.VISIBLE);
                Uri imgUri = data.getData();
                Bitmap bmp = null;
                try{
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                }catch (Exception e){}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
                byte[] fileInBytes = baos.toByteArray();
                filesInBytes = baos.toByteArray();
                queImg.setVisibility(View.VISIBLE);

                queImg.setImageURI(imgUri);

                uploadImageToFirebase(fileInBytes);
            }
        }
    }

    private void uploadImageToFirebase(byte[] fileInBytes) {

        String imgId = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(new Date())+""+FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+".jpg";
        ImgId = imgId;
        final StorageReference fileRef = FirebaseStorage.getInstance().getReference("Images").child(imgId);
        fileRef.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        queImgUrls = uri.toString();
                        Picasso.get().load(uri)
                                .into(queImg);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                queImg.setVisibility(View.GONE);
                queImg.setImageURI(null);
                queImgUrls = null;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Failed Uploading Image",Toast.LENGTH_SHORT).show();
            }
        });
    }

}