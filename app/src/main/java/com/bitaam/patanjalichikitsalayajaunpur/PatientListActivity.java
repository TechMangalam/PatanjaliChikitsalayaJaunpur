package com.bitaam.patanjalichikitsalayajaunpur;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.bitaam.patanjalichikitsalayajaunpur.adapters.PatientListAdapter;
import com.bitaam.patanjalichikitsalayajaunpur.modals.PatientModel;
import com.bitaam.patanjalichikitsalayajaunpur.modals.UserModal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class PatientListActivity extends AppCompatActivity {

    android.widget.SearchView searchView;
    Toolbar toolbar;
    RecyclerView patientListRecycler;
    String userRole="User",mobileNo;
    UserModal userModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        Intent intent = getIntent();
        userModal = (UserModal) intent.getSerializableExtra("userInfo");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        mobileNo = user.getPhoneNumber();

        toolbar = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        patientListRecycler = findViewById(R.id.searchRecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        patientListRecycler.setLayoutManager(linearLayoutManager);

        databaseActivities(userModal.getPhone());

    }

    private void databaseActivities(String mobNo) {

        PatientListAdapter patientListAdapter = new PatientListAdapter(patientListRecycler,new ArrayList<PatientModel>(),new ArrayList<String>(),userModal.getRole(),getApplicationContext());
        patientListRecycler.setAdapter(patientListAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Patients");

        databaseReference.orderByChild("phoneNo").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!userModal.getRole().equals("User") || Objects.equals(snapshot.child("phoneNo").getValue(), mobNo)) {
                    PatientModel patientModel = snapshot.getValue(PatientModel.class);
                    String key = snapshot.getKey();
                    ((PatientListAdapter) Objects.requireNonNull(patientListRecycler.getAdapter())).update(patientModel, key);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (android.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}