package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitaam.patanjalichikitsalayajaunpur.modals.UserModal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignNewUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText userName ,userPassword,userConfPassword,name;
    Spinner jobSpinner;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button register;
    TextView signUpToIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        mAuth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar)findViewById(R.id.progBar);

        jobSpinner = findViewById(R.id.jobMenu);


        userName = (EditText)findViewById(R.id.email);
        name = findViewById(R.id.profileName);
        userPassword = (EditText)findViewById(R.id.passwordEdt);
        userConfPassword = findViewById(R.id.cPasswordEdt);
        signUpToIn = findViewById(R.id.signUpToIn);
        register = findViewById(R.id.register);




        onClickEvents();

    }

    private void onClickEvents() {

        ArrayAdapter<CharSequence> adapterJob = ArrayAdapter.createFromResource(getApplicationContext(),R.array.roleList,android.R.layout.simple_spinner_item);
        adapterJob.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobSpinner.setAdapter(adapterJob);
        jobSpinner.setOnItemSelectedListener(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        signUpToIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                finish();
            }
        });




    }

    private  void registerUser()
    {
        final String useremail = userName.getText().toString().trim();
        String userpassword = userPassword.getText().toString().trim();

        if(useremail.isEmpty()){

            userName.setError("Email is Empty");
            userName.requestFocus();
            return;
        }

        if(!Patterns.PHONE.matcher(useremail).matches()){
            userName.setError("Please enter valid phone no.");
            userName.requestFocus();
            return;
        }

        if(userpassword.isEmpty()){

            userPassword.setError("Password is Empty");
            userPassword.requestFocus();
            return;
        }

        if(userpassword.length()<6){
            userPassword.setError("Minimum length is 6");
            userPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(useremail+"@bitaam.online",userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    setUserProfile(String.valueOf(jobSpinner.getSelectedItem()),name.getText().toString(),useremail);

                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    userName.requestFocus();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setUserProfile(String role,String name,String phone){

        DatabaseReference databaseReference ;

        UserModal userModal = new UserModal();
        userModal.setName(name);
        userModal.setPhone(phone);
        userModal.setRole(role);
        userModal.setDoctorStatus("Not available");
        userModal.setShopStatus("Closed");

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        /*if (role.equals("User")){

        }else if (role.equals("Patanjali Chikitsalaya or store")){
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Patanjali Store");

        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Doctor");

        }*/

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userModal.getName()).build();

        user.updateProfile(profileUpdates);

        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userModal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);

                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }
        });


    }


}
