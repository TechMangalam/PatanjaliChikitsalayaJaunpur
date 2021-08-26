package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackActivity extends AppCompatActivity {

    TextInputEditText feedbackEdt;
    Button submitBtn;
    FirebaseUser user;
    String no="1111111111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        no = user.getDisplayName();

        feedbackEdt = findViewById(R.id.feedback_input);
        submitBtn = findViewById(R.id.submitFeedbackBtn);

        onClickActivities();

    }

    private void onClickActivities() {

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndSendFeedback();
            }
        });

    }

    private void getAndSendFeedback() {

        String feedback = feedbackEdt.getText().toString();

        if (feedback.isEmpty()){
            feedbackEdt.setError("Enter feedback");
        }else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feedbacks");
            String key = no+""+reference.push().getKey();
            reference.child(key).setValue(feedback).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(FeedbackActivity.this, "Feedback sent", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }



    }
}