package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.adapters.DoubtAnsAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.modals.DoubtQuestionModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DoubtAnswerPosting extends AppCompatActivity {

    TextView post,attach,postDate,showHide,rotateImgTv;
    EditText question;
    ImageView queImg;
    ProgressBar progressBar;
    FirebaseUser user;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    String queImgUrls;
    String imgUrlProf;
    String QiD;
    RelativeLayout ansLayout,mainLayout;
    RecyclerView doubtCommentRecycler;
    DoubtAnsAdapter doubtAnsAdapter;
    byte[] filesInByte;
    Bitmap qImg;
    String ImgId;
    ArrayList<String> commentIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubt_answer_posting);

        mainLayout = findViewById(R.id.askFragment);

        queImg = findViewById(R.id.queImg);

        question = findViewById(R.id.question);
        question.requestFocus();
        progressBar = findViewById(R.id.queImgPro);

        postDate = findViewById(R.id.date);
        post = findViewById(R.id.postQue);
        attach = findViewById(R.id.attachImg);
        showHide = findViewById(R.id.showHide);
        ansLayout = findViewById(R.id.ansLayout);
        ansLayout.setVisibility(View.VISIBLE);
        rotateImgTv = findViewById(R.id.rotateImageTv);

        Bundle bundle = getIntent().getExtras();
        QiD = bundle.getString("qId");

        databaseReference = FirebaseDatabase.getInstance().getReference("Community").child("doubts").child(QiD).child("Comments");
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages");
        user = FirebaseAuth.getInstance().getCurrentUser();
        queImgUrls = "na";
        imgUrlProf = "na";

        StorageReference ref = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+".jpg");
        if (ref != null){
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imgUrlProf = uri.toString();
                }
            });
        }



        doubtCommentRecycler = findViewById(R.id.commentRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        doubtCommentRecycler.setLayoutManager(linearLayoutManager);

        databaseActivity();

        clickEventAll();

        MobileAds.initialize(this);
        AdView adView = findViewById(R.id.ask_doctor_ans_ads);
        adView.loadAd(new AdRequest.Builder().build());
        adView.setVisibility(View.VISIBLE);

    }

    public void clickEventAll(){

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attach.getText().equals("Attach Image")){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,1000);
                    attach.setText("Remove Image");
                }else if (attach.getText().equals("Remove Image")){

//                    StorageReference fileRef = FirebaseStorage.getInstance().getReference("Images").child(ImgId);
//                    if (fileRef!=null){
//                        fileRef.delete();
//                    }
                    queImgUrls = "na";
                    filesInByte = null;
                    qImg = null;
                    queImg.setImageURI(null);
                    rotateImgTv.setVisibility(View.GONE);
                    queImg.setVisibility(View.GONE);
                    attach.setText("Attach Image");
                }
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setEnabled(true);

                if (filesInByte != null){
                    uploadImageToFirebase(filesInByte);
                }else{
                    postComment();
                    progressBar.setVisibility(View.VISIBLE);
                }
//                question.setText(null);
//                queImg.setImageURI(null);
//                queImg.setVisibility(View.GONE);
//                queImgUrls = null;
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
//                Toast.makeText(getApplicationContext(),"Comment posted successfully",Toast.LENGTH_SHORT).show();

            }
        });

        showHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ansLayout.getVisibility() == View.GONE){
                    ansLayout.setVisibility(View.VISIBLE);
                    question.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    showHide.setText("Hide & See all comments");
                    showHide.setCompoundDrawables(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.more_black_hori),null,AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_baseline_expand_less_24),null);
                }else{
                    ansLayout.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    showHide.setText("Write comment");
                    showHide.setCompoundDrawables(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.more_black_hori),null,AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_baseline_expand_more_24),null);
                }
            }
        });

        doubtAnsAdapter.setOnItemClickListener(new DoubtAnsAdapter.OnItemClickListener() {
            @Override
            public void onLiked(int position) {
                likeCounter(position);
            }
        });

        rotateImgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qImg = rotateImage(qImg,90);
            }
        });


    }

    private void likeCounter(final int position) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Community")
                .child("doubts").child(QiD).child("Comments");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(commentIds.get(position))){
                    if (snapshot.hasChild("nLikes")){
                        if (snapshot.child("nLikes").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Toast.makeText(getApplicationContext(),"Already Liked !",Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                                liked(position,databaseReference);
                                databaseReference.child(commentIds.get(position)).child("nLikes")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(1);
                        }
                    }else{
                        liked(position,databaseReference);
                        databaseReference.child(commentIds.get(position)).child("nLikes")
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

    private void liked(int position, DatabaseReference databaseReference) {
        databaseReference.child(commentIds.get(position)).child("nLikes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(1);
        Toast.makeText(getApplicationContext(),"Liked !",Toast.LENGTH_SHORT).show();
        doubtAnsAdapter.notifyDataSetChanged();
    }

    private void postComment() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String disName = user.getDisplayName();
        String proUrl = imgUrlProf;
        String poDat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        //new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault()).format(new Date())
        String que = question.getText().toString().trim();
        if (que.isEmpty()){
            question.setError("Empty comment not allowed");
            progressBar.setVisibility(View.GONE);
            return;
        }
        String queUrl = queImgUrls;

        databaseReference.push().setValue(new DoubtQuestionModel(que,poDat,proUrl,queUrl,disName,true,user.getUid()))
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.GONE);
                question.setText(null);
                queImg.setImageURI(null);
                queImg.setVisibility(View.GONE);
                rotateImgTv.setVisibility(View.GONE);
                queImgUrls = null;
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                Toast.makeText(DoubtAnswerPosting.this, "Commented successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                question.setText(null);
                queImg.setImageURI(null);
                queImg.setVisibility(View.GONE);
                rotateImgTv.setVisibility(View.GONE);
                queImgUrls = null;
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                Toast.makeText(DoubtAnswerPosting.this, "Failed! Retry", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void databaseActivity() {


        doubtAnsAdapter = new DoubtAnsAdapter(doubtCommentRecycler, getApplicationContext(), new ArrayList<DoubtQuestionModel>());

        doubtCommentRecycler.setAdapter(doubtAnsAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Community")
                .child("doubts").child(QiD).child("Comments");
        if (databaseReference != null) {

            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    long nLikes = 0;
                    if (snapshot.hasChild("nLikes")){
                        nLikes = snapshot.child("nLikes").getChildrenCount();
                    }

                    commentIds.add(snapshot.getKey());

                    DoubtQuestionModel doubtQuestionModel = new DoubtQuestionModel();
                    doubtQuestionModel.setName((String) snapshot.child("name").getValue());
                    doubtQuestionModel.setProfileImgUrl((String) snapshot.child("profileImgUrl").getValue());
                    doubtQuestionModel.setPostDate((String) snapshot.child("postDate").getValue());
                    doubtQuestionModel.setQue((String) snapshot.child("que").getValue());
                    doubtQuestionModel.setQueImgUrl((String) snapshot.child("queImgUrl").getValue());

                    ((DoubtAnsAdapter) doubtCommentRecycler.getAdapter()).update(doubtQuestionModel,nLikes);


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    long nLikes = 0;
                    if (snapshot.hasChild("nLikes")){
                        nLikes = snapshot.child("nLikes").getChildrenCount();
                    }

                    commentIds.add(snapshot.getKey());

                    DoubtQuestionModel doubtQuestionModel = new DoubtQuestionModel();
                    doubtQuestionModel.setName((String) snapshot.child("name").getValue());
                    doubtQuestionModel.setProfileImgUrl((String) snapshot.child("profileImgUrl").getValue());
                    doubtQuestionModel.setPostDate((String) snapshot.child("postDate").getValue());
                    doubtQuestionModel.setQue((String) snapshot.child("que").getValue());
                    doubtQuestionModel.setQueImgUrl((String) snapshot.child("queImgUrl").getValue());

                    ((DoubtAnsAdapter) doubtCommentRecycler.getAdapter()).update(doubtQuestionModel,nLikes);
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
                    qImg = bmp;
                }catch (Exception e){}
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
//                byte[] fileInBytes = baos.toByteArray();
                filesInByte = compressImg(qImg);

                queImg.setVisibility(View.VISIBLE);
                rotateImgTv.setVisibility(View.VISIBLE);

                queImg.setImageBitmap(qImg);
                progressBar.setVisibility(View.GONE);

                //uploadImageToFirebase(fileInBytes);
            }
        }
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        filesInByte = null;
        filesInByte = compressImg(rotatedImg);
        queImg.setImageBitmap(rotatedImg);
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
        progressBar.setVisibility(View.VISIBLE);

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
                        postComment();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                queImg.setVisibility(View.GONE);
                rotateImgTv.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                queImgUrls = null;
                queImg.setImageURI(null);
                Toast.makeText(getApplicationContext(),"Failed Uploading Image",Toast.LENGTH_SHORT).show();
            }
        });
    }

}