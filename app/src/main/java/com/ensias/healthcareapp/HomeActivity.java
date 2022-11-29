package com.ensias.healthcareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.ensias.healthcareapp.Common.Common;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    Button SignOutBtn;
    Button searchPatBtn;
    Button myDoctors;
    Button BtnRequst;
    Button profile;
    Button appointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.CurrentUserType = "patient";
        setContentView(R.layout.activity_home);
        appointment = findViewById(R.id.appointement2);
        appointment.setOnClickListener(view -> {
            Intent k = new Intent(HomeActivity.this, PatientAppointementsActivity.class);
            startActivity(k);
        });
        searchPatBtn = (Button)findViewById(R.id.searchBtn);
        searchPatBtn.setOnClickListener(v -> {
            Intent k = new Intent(HomeActivity.this, SearchPatActivity.class);
            startActivity(k);
        });
        SignOutBtn=findViewById(R.id.signOutBtn);
        SignOutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        myDoctors = (Button)findViewById(R.id.myDoctors);
        myDoctors.setOnClickListener(v -> {
            Intent k = new Intent(HomeActivity.this, MyDoctorsAvtivity.class);
            startActivity(k);
        });
        BtnRequst = findViewById(R.id.btnRequst);
        BtnRequst.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MedicalFile.class);
            intent.putExtra("patient_email", FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
            startActivity(intent);
        });

        profile = findViewById(R.id.profile);
        profile.setOnClickListener(v -> {
            Intent k = new Intent(HomeActivity.this, ProfilePatientActivity.class);
            startActivity(k);
        });

        Common.CurrentUserid= FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        FirebaseFirestore.getInstance().collection("User").document(Common.CurrentUserid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Common.CurrentUserName = documentSnapshot.getString("name");
            }
        });

    }
}