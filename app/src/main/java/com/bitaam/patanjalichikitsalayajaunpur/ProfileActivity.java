package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    EditText profileName,email;
    Button update;
    SimpleDraweeView photoSelector;
    CircleImageView profileImage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    TextView rotateImageTv,privacyPolicyTv;
    ProgressBar progressBar;
    Uri profileImageUri;
    byte[] filesInBytes;
    Bitmap qImg;
    String imgLink="";
    String userName = "na";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_profile);


        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");


        profileName = findViewById(R.id.profileName);
        progressBar = findViewById(R.id.progressCircular);
        email = findViewById(R.id.email);
        update = findViewById(R.id.update);
        photoSelector = findViewById(R.id.photo_selector_icon);
        rotateImageTv = findViewById(R.id.rotateImageTv);
        privacyPolicyTv = findViewById(R.id.privacyPolicyTv);

        profileImage = findViewById(R.id.profile_image);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try{
            if (user != null){
                //progressBar.setVisibility(View.VISIBLE);
                try {
                    if(!user.getDisplayName().isEmpty()){
                        profileName.setText(user.getDisplayName());
                        userName = user.getDisplayName();
                        //profileName.setSelection(user.getDisplayName().length());

                    }else {
                        profileName.requestFocus();
                        profileName.setText("Unknown");
                    }
                }catch (Exception e){profileName.setText("Unknown");}


                if(user.getPhoneNumber() != null){
                    email.setText(user.getPhoneNumber());
                    email.setEnabled(false);
                }

                StorageReference ref = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+".jpg");

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        }catch (Exception e){
            if(!user.getPhoneNumber().isEmpty()){
                email.setText(user.getPhoneNumber());
                email.setEnabled(false);
            }
        }

        onClickActivity();



    }

    private void onClickActivity() {

        privacyPolicyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://bitaam.com/Privacy-Policy/my-upchaar-privacy-policy.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        photoSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1000);
                update.setEnabled(false);
            }
        });

        rotateImageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qImg = rotateImage(qImg,90);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filesInBytes!=null){
                    progressBar.setVisibility(View.VISIBLE);
                    uploadImageToFirebase(filesInBytes);
                }else{
                    changeProfileName(profileName.getText().toString());
                }

            }
        });

        profileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userName.equals(s.toString())){
                    update.setEnabled(false);
                }else{
                    update.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imgUri = data.getData();
                profileImageUri = imgUri;
                Bitmap bmp = null;
                try{
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                    qImg = bmp;
                }catch (Exception e){}
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
//                byte[] fileInBytes = baos.toByteArray();

                //filesInBytes = compressImg(qImg);
                qImg = rotateImage(qImg,90);
                rotateImageTv.setVisibility(View.VISIBLE);
                //profileImage.setImageURI(profileImageUri);
                update.setEnabled(true);

                //uploadImageToFirebase(fileInBytes);
            }
        }
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        filesInBytes = null;
        filesInBytes = compressImg(rotatedImg);
        profileImage.setImageBitmap(rotatedImg);
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

        final StorageReference fileRef = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+".jpg");
        fileRef.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        profileImageUri = uri;
                        imgLink = uri.toString();
                        rotateImageTv.setVisibility(View.GONE);
                        getDataAndUpload();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed Uploading Image",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getDataAndUpload(){

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(profileName.getText().toString()).setPhotoUri(profileImageUri).build();

        assert user != null;
        user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressBar.setVisibility(View.GONE);
                    update.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"Profile updated",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                }
            });



    }

    private void changeProfileName(String name){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();

        assert user != null;
        user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                userName = user.getDisplayName();
                progressBar.setVisibility(View.GONE);
                update.setEnabled(false);
                Toast.makeText(getApplicationContext(),"Profile name updated",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }


}
