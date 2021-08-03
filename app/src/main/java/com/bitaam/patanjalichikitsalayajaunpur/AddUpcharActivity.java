package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.modals.UpcharModal;
import com.bitaam.patanjalichikitsalayajaunpur.utility.BgMediaWebView;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddUpcharActivity extends AppCompatActivity {
    
    Button attachImageBtn,checkVideoBtn,submitBtn;
    EditText dNameEdt,videoLinkEdt,dCauseEnEdt,dCauseHiEdt,dCureEnEdt,dCureHiEdt,dRegimenEnEdt,dRegimenHiEdt;
    BgMediaWebView checkVideoWebView;
    ImageView imageView;
    String imgLink="";
    String ImgId,videoId;
    byte[] filesInBytes;
    AlertDialog.Builder builder;
    String role="na";
    boolean completed = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_upchar);

        Intent intent = getIntent();
        UpcharModal upcharModal = (UpcharModal) intent.getSerializableExtra("upcharInfo");
        if (intent.getStringExtra("role") != null){
            role = intent.getStringExtra("role");
        }

        builder = new AlertDialog.Builder(this);
        
        attachImageBtn = findViewById(R.id.attachImageBtn);
        checkVideoBtn = findViewById(R.id.videoCheckBtn);
        submitBtn = findViewById(R.id.submitBtn);
        
        dNameEdt = findViewById(R.id.dName);
        dCauseEnEdt = findViewById(R.id.dCauseE);
        dCauseHiEdt = findViewById(R.id.dCauseH);
        dCureEnEdt = findViewById(R.id.dCureE);
        dCureHiEdt = findViewById(R.id.dCureH);
        dRegimenEnEdt = findViewById(R.id.dRegimenE);
        dRegimenHiEdt = findViewById(R.id.dRegimenH);
        videoLinkEdt = findViewById(R.id.videoLink);
        
        checkVideoWebView = findViewById(R.id.youtubeWebembed);
        imageView = findViewById(R.id.upcharImgView);

        onClickActivities();

        if (role.equals("edit")){
            setDataToEdit(upcharModal);
        }
        
        
    }

    private void setDataToEdit(UpcharModal upcharModal) {

        dNameEdt.setText(upcharModal.getUpcharName());
        dCauseEnEdt.setText(upcharModal.getSymptomE());
        dCauseHiEdt.setText(upcharModal.getSymptomH());
        dCureEnEdt.setText(upcharModal.getCureE());
        dCureHiEdt.setText(upcharModal.getCureH());
        dRegimenEnEdt.setText(upcharModal.getRegimenE());
        dRegimenHiEdt.setText(upcharModal.getRegimenH());
        videoLinkEdt.setText(upcharModal.getVid());
        Picasso.get().load(upcharModal.getIconUrl()).into(imageView);
        imageView.setVisibility(View.VISIBLE);
        imgLink = upcharModal.getIconUrl();

    }

    private void onClickActivities() {

        attachImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attachImageBtn.getText().equals("Attach Image")){
                    attachImageBtn.setText("Remove Image");
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent,"Select Image"),1000);
                }else {
                    imageView.setImageURI(null);
                    filesInBytes = null;
                    attachImageBtn.setText("Attach Image");
                    imageView.setVisibility(View.GONE);
                }

            }
        });

        checkVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkVideoBtn.getText().equals("Check Video")){

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

                    if (videoLinkEdt.getText().toString().trim().length() != 0){
                        videoId = "" + videoLinkEdt.getText() + "";
                        checkForLink();
                        checkVideoWebView.setVisibility(View.VISIBLE);
                        setWebView();
                        checkVideoBtn.setText("Remove");
                        submitBtn.setEnabled(true);
                        //attachYouVidBtn.setVisibility(View.VISIBLE);
                    }else{
                        videoLinkEdt.setError("enter youtube video link");
                    }
                }else{
                    submitBtn.setEnabled(false);
                    videoLinkEdt.setText(null);
                    checkVideoBtn.setText("Check Video");
                    checkVideoWebView.loadUrl("");
                    checkVideoWebView.setVisibility(View.GONE);
                    checkVideoWebView.setVisibility(View.GONE);
                }


            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToFirebase(filesInBytes);
                //alert();
            }
        });

    }

    public void checkForLink(){
        if (videoId.startsWith("https://youtube.com/playlist?list=")){
            videoId = ""+videoId.substring(videoId.indexOf('=')+1);
        } else if (videoId.startsWith("https://youtu.be/")) {
            videoId = ""+videoId.substring(videoId.lastIndexOf('/')+1);
        }
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
                }catch (Exception e){}
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
                //byte[] fileInBytes = baos.toByteArray();
                filesInBytes = baos.toByteArray();
                imageView.setVisibility(View.VISIBLE);

                imageView.setImageURI(imgUri);

                submitBtn.setEnabled(true);

                //uploadImageToFirebase(fileInBytes);
            }
        }

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
                                .into(imageView);
                        imageView.setVisibility(View.VISIBLE);
                        getDataAndUpload();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setVisibility(View.GONE);
                imageView.setImageURI(null);
                imgLink = null;
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Try again ! Failed Uploading Image",Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void getDataAndUpload() {
        
        String name,causeE,causeH,cureE,cureH,regE,regH,videoLink;
        name = dNameEdt.getText().toString();
        causeE = dCauseEnEdt.getText().toString();
        causeH = dCauseHiEdt.getText().toString();
        cureE = dCureEnEdt.getText().toString();//getTextLocale().getLanguage();
        cureH = dCureHiEdt.getText().toString();
        regE = dRegimenEnEdt.getText().toString();
        regH = dRegimenHiEdt.getText().toString();
        videoLink = videoLinkEdt.getText().toString();
        
        boolean flag = true;
        
        if (name.isEmpty()){
            dNameEdt.setError("Name is empty !");
            flag = false;
        }
        if (causeE.isEmpty()){
            dCauseEnEdt.setError("Cause is empty !");
            flag = false;
        }
        if (causeH.isEmpty()){
            dCauseHiEdt.setError("Cause is empty");
            flag = false;
        }
        if (cureE.isEmpty()){
            dCureEnEdt.setError("Cure is empty");
            flag = false;
        }
        if (cureH.isEmpty()){
            dCureHiEdt.setError("Cure is empty");
            flag = false;
        }
        if (regE.isEmpty()){
            dCureEnEdt.setError("Regimen is empty");
            flag = false;
        }
        if (regH.isEmpty()){
            dRegimenHiEdt.setError("Parhej is empty");
            flag = false;
        }

        if (videoLink.isEmpty()){
            dRegimenHiEdt.setError("video link is empty");
            flag = false;
        }

        if (imgLink.isEmpty()){
            Toast.makeText(this, "Attach image !", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        
        if (flag){
            updateToDatabase(name,causeE,causeH,cureE,cureH,regE,regH,imgLink,videoLink);
        }
        
    }

    private void updateToDatabase(String name, String causeE, String causeH, String cureE, String cureH, String regE, String regH, String imgLink, String videoLink) {

        UpcharModal upcharModal = new UpcharModal();
        upcharModal.setUpcharName(name);
        upcharModal.setSymptomE(causeE);
        upcharModal.setSymptomH(causeH);
        upcharModal.setCureE(cureE);
        upcharModal.setCureH(cureH);
        upcharModal.setRegimenE(regE);
        upcharModal.setRegimenH(regH);
        upcharModal.setIconUrl(imgLink);
        upcharModal.setVid(videoLink);
        upcharModal.setCategory("all");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Upchar");
        databaseReference.child(name).setValue(upcharModal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(AddUpcharActivity.this, "Added Successfully !", Toast.LENGTH_SHORT).show();
                AlertExit("Confirmation","Added Successfully! Do you want to edit or exit?");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddUpcharActivity.this, "Try again ! "+e, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void AlertExit(String title,String msg){

        builder.setMessage(msg).setTitle(title)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                }).setNegativeButton("Edit",null).setCancelable(false);



    }

    @Override
    public void onBackPressed() {

        if (completed){
            super.onBackPressed();
        }else{
            AlertExit("Confirmation","Not saved! Do you want to edit or exit?");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    void setWebView()
    {


        checkVideoWebView.setWebChromeClient(new WebChromeClient());

        checkVideoWebView.getSettings().setPluginState(WebSettings.PluginState.ON);

        checkVideoWebView.setWebViewClient(new WebViewClient());

        checkVideoWebView.getSettings();
        checkVideoWebView.getSettings().setJavaScriptEnabled(true);

        checkVideoWebView.setBackgroundColor(0x00000000);

        checkVideoWebView.setKeepScreenOn(true);

        checkVideoWebView.setHorizontalScrollBarEnabled(false);
        checkVideoWebView.setVerticalScrollBarEnabled(false);

        checkVideoWebView.getSettings().setBuiltInZoomControls(true);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        String html = getHTML();


        checkVideoWebView.loadDataWithBaseURL("", html, mimeType, encoding, "");

    }

    public String getHTMLPlaylist(String vid)
    {

        String html = "<html>"

                + "<head>"
                + "</head>"
                + "<body style=\"border: 0; padding: 0\">"
                + "<iframe "
                + "type=\"text/html\" "
                + "class=\"youtube-player\" "
                + "width= 100%\""
                + "\" "
                + "height= 95%\""
                + "\" "
                + "src=\"http://www.youtube-nocookie.com/embed/videoseries?list="
                + vid.trim()
                + "\" frameborder=\"0\" allow=\"autoplay;encrpted-media\" allowfullscreen></iframe>"
                + "</body>"
                + "</html>";

        return html;
    }


    public String getHTML()
    {

        String html = "<html>"

                + "<head>"
                + "</head>"
                + "<body style=\"border: 0; padding: 0\">"
                + "<iframe "
                + "type=\"text/html\" "
                + "class=\"youtube-player\" "
                + "width= 100%\""
                + "\" "
                + "height= 95%\""
                + "\" "
                + "src=\"http://www.youtube-nocookie.com/embed/"
                + videoId
                + "?controls=1&showinfo=0&showsearch=0&modestbranding=0" +
                "&autoplay=1&fs=1&vq=hd720\" " + "frameborder=\"0\" allowfullscreen></iframe>"
                + "</body>"
                + "</html>";

        return html;
    }



}