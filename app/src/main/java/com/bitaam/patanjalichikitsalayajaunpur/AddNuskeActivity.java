package com.bitaam.patanjalichikitsalayajaunpur;

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
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bitaam.patanjalichikitsalayajaunpur.modals.NuskeModal;
import com.bitaam.patanjalichikitsalayajaunpur.utility.BgMediaWebView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Date;
import java.util.Locale;

public class AddNuskeActivity extends AppCompatActivity {

    Button attachImageBtn,checkVideoBtn,submitBtn;
    EditText dNameEdt,videoLinkEdt,dHowEnEdt,dHowHiEdt,dBenEnEdt,dBenHiEdt;
    TextView writerNameTv;
    BgMediaWebView checkVideoWebView;
    ImageView imageView;
    String imgLink="";
    String ImgId,videoId;
    byte[] filesInBytes;
    AlertDialog.Builder builder;
    String role="na";
    boolean completed = false;
    FirebaseUser user;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nuske);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        NuskeModal nuskeModal = (NuskeModal) intent.getSerializableExtra("nuskeInfo");
        if (intent.getStringExtra("role") != null){
            role = intent.getStringExtra("role");
        }

        builder = new AlertDialog.Builder(this);

        attachImageBtn = findViewById(R.id.attachImageBtn);
        checkVideoBtn = findViewById(R.id.videoCheckBtn);
        submitBtn = findViewById(R.id.submitBtn);

        writerNameTv = findViewById(R.id.writerName);
        dNameEdt = findViewById(R.id.dName);
        dHowEnEdt = findViewById(R.id.dHowE);
        dHowHiEdt = findViewById(R.id.dHowH);
        dBenEnEdt = findViewById(R.id.dBenifitsE);
        dBenHiEdt = findViewById(R.id.dBenifitsH);

        videoLinkEdt = findViewById(R.id.videoLink);

        checkVideoWebView = findViewById(R.id.youtubeWebembed);
        imageView = findViewById(R.id.upcharImgView);

        onClickActivities();

        if (role.equals("edit")){
            assert nuskeModal != null;
            setDataToEdit(nuskeModal);
        }


        assert user != null;
        String writer = "Written by : "+user.getDisplayName();
        writerNameTv.setText(writer);

    }

    private void setDataToEdit(NuskeModal nuskeModal) {
        String writer = "Written by : "+nuskeModal.getName();
        writerNameTv.setText(writer);
        dNameEdt.setText(nuskeModal.getNuskeName());
        dHowEnEdt.setText(nuskeModal.getHowE());
        dHowHiEdt.setText(nuskeModal.getHowH());
        dBenEnEdt.setText(nuskeModal.getImpE());
        dBenHiEdt.setText(nuskeModal.getImpH());
        videoLinkEdt.setText(nuskeModal.getVid());
        Picasso.get().load(nuskeModal.getIconUrl()).into(imageView);
        imageView.setVisibility(View.VISIBLE);
        imgLink = nuskeModal.getIconUrl();

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

                    if (videoLinkEdt.getText().toString().trim().length() > 10 && videoLinkEdt.getText().toString().trim().startsWith("https://youtu.be/")){
                        videoId = "" + videoLinkEdt.getText() + "";
                        checkForLink();
                        checkVideoWebView.setVisibility(View.VISIBLE);
                        setWebView();
                        checkVideoBtn.setText("Remove");
                        submitBtn.setEnabled(true);
                        //attachYouVidBtn.setVisibility(View.VISIBLE);
                    }else{
                        videoLinkEdt.setError("enter valid youtube video link");
                        videoLinkEdt.setFocusable(true);
                        submitBtn.setEnabled(false);
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
                if (filesInBytes!=null){
                    uploadImageToFirebase(filesInBytes);
                    progressDialogShow("Submitting","Please wait ...");
                }else{
                    Toast.makeText(getApplicationContext(), "Attach image !", Toast.LENGTH_SHORT).show();
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        final StorageReference fileRef = FirebaseStorage.getInstance().getReference("NuskeImages").child(imgId);
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
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Try again ! Failed Uploading Image",Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void getDataAndUpload() {

        String name,howE,howH,benE,benH,videoLink,writerName;
        name = dNameEdt.getText().toString();
        howE = dHowEnEdt.getText().toString();
        howH = dHowHiEdt.getText().toString();
        benE = dBenEnEdt.getText().toString();//getTextLocale().getLanguage();
        benH = dBenHiEdt.getText().toString();
        writerName = user.getDisplayName();

        videoLink = videoLinkEdt.getText().toString();

        boolean flag = true;

        if (name.isEmpty()){
            dNameEdt.setError("Name is empty !");
            dNameEdt.setFocusable(true);
            flag = false;
            progressDialog.dismiss();
        }
        if (benE.isEmpty()){
            dBenEnEdt.setError("Benefit is empty !");
            dBenEnEdt.setFocusable(true);
            flag = false;
            progressDialog.dismiss();
        }
        if (benH.isEmpty()){
            dBenHiEdt.setError("Benefit is empty");
            dBenHiEdt.setFocusable(true);
            flag = false;
            progressDialog.dismiss();
        }
        if (howE.isEmpty()){
            dHowEnEdt.setError("How to do is empty");
            dHowEnEdt.setFocusable(true);
            flag = false;
            progressDialog.dismiss();
        }
        if (howH.isEmpty()){
            dHowHiEdt.setError("How to do is empty");
            dHowHiEdt.setFocusable(true);
            flag = false;
            progressDialog.dismiss();
        }

        if (videoLink.isEmpty() || !videoLink.startsWith("https://youtu.be/")){
            videoLinkEdt.setError("Enter valid youtube video link");
            videoLinkEdt.setFocusable(true);
            flag = false;
            progressDialog.dismiss();
        }

        if (imgLink.isEmpty()){
            Toast.makeText(this, "Attach image !", Toast.LENGTH_SHORT).show();
            flag = false;
            progressDialog.dismiss();
        }

        if (flag){
            updateToDatabase(name,benE,benH,howE,howH,imgLink,videoLink,writerName);
        }else {
            progressDialog.dismiss();
        }

    }

    private void updateToDatabase(String name, String benefitE, String benefitH, String howE, String howH, String imgLink, String videoLink,String writerName) {

        NuskeModal nuskeModal = new NuskeModal();
        nuskeModal.setName(writerName);
        nuskeModal.setNuskeName(name);
        nuskeModal.setImpE(benefitE);
        nuskeModal.setImpH(benefitH);
        nuskeModal.setHowE(howE);
        nuskeModal.setHowH(howH);
        nuskeModal.setVisibility(false);
        nuskeModal.setIconUrl(imgLink);
        nuskeModal.setVid(videoLink);
        nuskeModal.setCategory("all");
        nuskeModal.setType("1");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Nuske");
        databaseReference.child(name).setValue(nuskeModal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(AddNuskeActivity.this, "Added Successfully !", Toast.LENGTH_SHORT).show();
                AlertExit("Confirmation","Added Successfully! Do you want to edit or exit?");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                completed = true;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddNuskeActivity.this, "Try again ! "+e, Toast.LENGTH_SHORT).show();
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

    private void progressDialogShow(String title,String Msg){

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(Msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressDialog.setMax(100);
        progressDialog.show();

    }


}