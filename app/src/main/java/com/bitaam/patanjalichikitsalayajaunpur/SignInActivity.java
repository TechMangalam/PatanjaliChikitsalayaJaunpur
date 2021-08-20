package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.modals.UserModal;
import com.bitaam.patanjalichikitsalayajaunpur.utility.CheckConnectivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button generateOtpBtn,submitOtpBtn;
    TextInputEditText nameEdt,phoneEdt,otpEdt;
    TextInputLayout otpInputLayout,nameInputLayout;
    ProgressDialog progressDialog;
    private String verificationId;
    private String otp;
    TextView signInTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progBar);
        generateOtpBtn = findViewById(R.id.generateOtpBtn);
        submitOtpBtn = findViewById(R.id.submitOtpBtn);
        otpInputLayout = findViewById(R.id.otpInput);
        nameInputLayout = findViewById(R.id.nameInput);
        signInTv = findViewById(R.id.signInTv);
        nameEdt = findViewById(R.id.name_input);
        phoneEdt = findViewById(R.id.phone_input);
        otpEdt = findViewById(R.id.otp_input);


        onClickActivities();

        CheckConnectivity connectivity = new CheckConnectivity(getApplicationContext());
        if (!connectivity.isOnline()){
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();

                alertDialog.setTitle("Info");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                });

                alertDialog.show();
            } catch (Exception e) {
                //Log.d(Constants.TAG, "Show Dialog: " + e.getMessage());
            }
        }


    }

    private void onClickActivities() {

        generateOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (TextUtils.isEmpty(nameEdt.getText())) {
                    nameEdt.setError("Enter your name.");
                    nameEdt.requestFocus();
                }
                if (TextUtils.isEmpty(phoneEdt.getText())) {
                    phoneEdt.setError("Enter valid phone no.");
                    phoneEdt.requestFocus();
                } else {
                    // if the text field is not empty we are calling our
                    // send OTP method for getting OTP from Firebase.
                    String phone = "+91" + phoneEdt.getText();
                    progressDialogShow("Sending OTP","Please wait...");
                    sendVerificationCode(phone);
                    //otpInputLayout.setEnabled(true);
                }
            }
        });

        submitOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = otpEdt.getText().toString();
                if (!otp.isEmpty()) {

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                    progressDialogShow("Logging In","Please wait..");
                    userLoginPhone(credential);
                }else{
                    otpEdt.setError("Enter valid otp");
                    otpEdt.requestFocus();
                }
            }
        });

    }

    private void sendVerificationCode(String phone) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)            // Phone number to verify
                        .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                //otpInputLayout.setEnabled(true);
                                final String code = phoneAuthCredential.getSmsCode();

                                if (code != null) {
                                    otpEdt.setText(code);

                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                                    progressDialogShow("Logging In","Please wait..");
                                    userLoginPhone(credential);
                                }
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                Toast.makeText(SignInActivity.this, "OTP SENT SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                otpInputLayout.setEnabled(true);
                                submitOtpBtn.setEnabled(true);
                                verificationId = s;
                                progressDialog.dismiss();
                            }
                        })           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void userLoginPhone(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setUserProfile(Objects.requireNonNull(nameEdt.getText()).toString(), Objects.requireNonNull(phoneEdt.getText()).toString());

                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

//    private void userLogin(){
//
//        String useremail = userName.getText().toString().trim();
//        String userpassword = userPassword.getText().toString().trim();
//
//        if(useremail.isEmpty()){
//
//            userName.setError("phone no. is Empty");
//            userName.requestFocus();
//            return;
//        }
//
//        if(!Patterns.PHONE.matcher(useremail).matches()){
//            userName.setError("Please enter valid phone no");
//            userName.requestFocus();
//            return;
//        }
//
//        if(userpassword.isEmpty()){
//
//            userPassword.setError("Password is Empty");
//            userPassword.requestFocus();
//            return;
//        }
//
//        if(userpassword.length()<6){
//            userPassword.setError("Minimum length is 6");
//            userPassword.requestFocus();
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        mAuth.signInWithEmailAndPassword(useremail+"@bitaam.online",userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                if(task.isSuccessful()){
//
//                    progressBar.setVisibility(View.GONE);
//                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                }else{
//                    Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    userName.requestFocus();
//                    progressBar.setVisibility(View.GONE);
//                }
//
//            }
//        });
//
//    }

    private void setUserProfile(String name,String phone){

        UserModal userModal = new UserModal();
        userModal.setName(name);
        userModal.setPhone(phone);
        userModal.setRole("User");
        userModal.setQualification("na");
        userModal.setShopName("na");
        userModal.setVerified(false);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        /*if (role.equals("User")){

        }else if (role.equals("Patanjali Chikitsalaya or store")){
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Patanjali Store");

        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Doctor");

        }*/

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userModal.getName()).build();

        assert user != null;
        user.updateProfile(profileUpdates);

        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userModal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();

                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

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
