package com.ensias.healthcareapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import dmax.dialog.SpotsDialog;

public class ProfilePatientActivity extends AppCompatActivity {
    private MaterialTextView doctorName;
    private MaterialTextView doctorSpe;
    private MaterialTextView doctorPhone;
    private MaterialTextView doctorEmail;
    private MaterialTextView doctorAddress;
    private MaterialTextView doctorAbout;
    private ImageView doctorImage;
    StorageReference pathReference ;
    final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("Patient").document("" + doctorID + "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_doctor);

        doctorName = findViewById(R.id.doctor_name);
        doctorSpe = findViewById(R.id.doctor_specialite);
        doctorPhone = findViewById(R.id.doctor_phone);
        doctorEmail = findViewById(R.id.doctor_email);
        doctorAddress = findViewById(R.id.doctor_address);
        doctorAbout = findViewById(R.id.doctor_about);
        doctorImage = findViewById(R.id.imageView3);
        findViewById(R.id.ivBack).setOnClickListener(view -> finish());
        findViewById(R.id.ivEdit).setOnClickListener(view -> startEditActivity());

        Drawable defaultImage = getResources().getDrawable(R.drawable.ic_anon_user_48dp); //default user image
        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).setCancelable(true).build();
        dialog.show();

        //display profile image
        String imageId = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/"+ imageId+".jpg");
        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.with(ProfilePatientActivity.this)
                    .load(uri)
                    .placeholder(R.drawable.ic_default_profile_pic)
                    .fit()
                    .centerCrop()
                    .into(doctorImage);//hna fin kayn Image view
            dialog.dismiss();
            // profileImage.setImageURI(uri);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                dialog.dismiss();
            }
        });

        docRef.addSnapshotListener(this, (documentSnapshot, e) -> {
            doctorName.setText(documentSnapshot.getString("name"));
           // doctorSpe.setText(documentSnapshot.getString("dateNaissance"));
            doctorPhone.setText(documentSnapshot.getString("tel"));
            doctorEmail.setText(documentSnapshot.getString("email"));
            doctorAddress.setText(documentSnapshot.getString("adresse"));
            doctorImage.setImageDrawable(defaultImage);
        });
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Get access to the custom title view
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void startEditActivity() {
        Intent intent = new Intent(this, EditProfilePatientActivity.class);
        intent.putExtra("CURRENT_NAME", doctorName.getText().toString());
        intent.putExtra("CURRENT_PHONE", doctorPhone.getText().toString());
        intent.putExtra("CURRENT_ADDRESS", doctorAddress.getText().toString());
        startActivity(intent);
        finish();
    }
}
