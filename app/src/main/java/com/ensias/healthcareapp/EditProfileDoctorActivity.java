package com.ensias.healthcareapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ensias.healthcareapp.Interface.HCAlertInterface;
import com.ensias.healthcareapp.model.UploadImage;
import com.ensias.healthcareapp.utils.HCAlert;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileDoctorActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "EditProfileDoctorActivity";
    private ImageView profileImage;
    private ImageButton selectImage;
    private Button updateProfile;
    private TextInputEditText doctorName;
    private TextInputEditText doctorEmail;
    private TextInputEditText doctorPhone;
    private TextInputEditText doctorAddress;
    final String currentDoctorUID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    private Uri uriImage;

    private StorageReference pStorageRef;
    private DatabaseReference pDatabaseRef;
    private FirebaseFirestore doctorRef;
    private StorageReference pathReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private DatabaseReference currentUserImg;
    private boolean isProfilePicUpdated = false;
    private boolean isProfileUpdated = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_doctor);
        doctorRef = FirebaseFirestore.getInstance();
        profileImage = findViewById(R.id.image_profile);
        selectImage = findViewById(R.id.select_image);
        updateProfile = findViewById(R.id.update);
        doctorName = findViewById(R.id.nameText);
        doctorPhone = findViewById(R.id.phoneText);
        //doctorEmail = findViewById(R.id.emailText);
        doctorAddress = findViewById(R.id.addressText);
        findViewById(R.id.ivBack).setOnClickListener(view -> finish());

        pStorageRef = FirebaseStorage.getInstance().getReference("DoctorProfile");
        pDatabaseRef = FirebaseDatabase.getInstance().getReference("DoctorProfile");

        //get the default doctor's informations from ProfileDoctorActivity
        Intent intent = getIntent(); //get the current intent
        String current_name = intent.getStringExtra("CURRENT_NAME");
        String current_phone = intent.getStringExtra("CURRENT_PHONE");
        String current_address = intent.getStringExtra("CURRENT_ADDRESS");

        //Set the default informtions in he text fields
        doctorName.setText(current_name);
        doctorPhone.setText(current_phone);
        doctorAddress.setText(current_address);
        /*
        currentUserImg = FirebaseDatabase.getInstance().getReference("DoctorProfile").child("1590965871687");
        Glide.with(this)
                .load(currentUserImg)
                .into(profileImage);
                   */
        String userPhotoPath = currentDoctorUID + ".jpg";
        pathReference = storageRef.child("DoctorProfile/" + userPhotoPath); //Doctor photo in database
        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.with(EditProfileDoctorActivity.this)
                    .load(uri)
                    .placeholder(R.drawable.doctor)
                    .fit()
                    .centerCrop()
                    .into(profileImage);//Store here the imageView

            // profileImage.setImageURI(uri);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Toast.makeText(EditProfileDoctorActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
        });


        selectImage.setOnClickListener(view -> openFileChooser());

        updateProfile.setOnClickListener(view -> {
            String updateAddress = doctorAddress.getText().toString();
            String updateName = doctorName.getText().toString();
            //String updateEmail = doctorEmail.getText().toString();
            String updatePhone = doctorPhone.getText().toString();
            progressDailog = HCAlert.showProgress(this, "Updating...");
            uploadProfileImage();
            updateDoctorInfos(updateName, updateAddress, updatePhone);
        });
    }

    /* Update the doctor info in the database */

    private AlertDialog progressDailog;

    private void updateDoctorInfos(String name, String address, String phone) {
        DocumentReference documentReference = doctorRef.collection("Doctor").document("" + doctorID + "");
        documentReference.update("adresse", address);
        //documentReference.update("email", email);
        documentReference.update("name", name);
        Activity activity = this;

        documentReference.update("tel", phone)
                .addOnSuccessListener(aVoid -> {
                    isProfileUpdated = true;
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    if(progressDailog != null) progressDailog.dismiss();
                    HCAlert.showAlert(this, "Failed to update", e.getLocalizedMessage());
//                    Toast.makeText(EditProfileDoctorActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("Androidview", e.getMessage());
                });
    }

    /* Used to choose a file */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /* used to get the data back */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriImage = data.getData();
            Picasso.with(this).load(uriImage).into(profileImage);
        }
    }

    /* Retrieve the extension of the file to upload */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /* Used to upload the doctor image in the DataBase */
    private void uploadProfileImage() {
        /* check if the image is not null */
        if (uriImage != null) {
            StorageReference storageReference = pStorageRef.child(currentDoctorUID
                    + "." + getFileExtension(uriImage));
            /*
            private void getDownloadUrl(StorageReference fileReference) {
                fileReference.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Log.d(TAG, "onSuccess" + uri);
                            }
                        });
            }
             */

            Activity activity = this;

            storageReference.putFile(uriImage).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return pStorageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.e(TAG, "then: " + downloadUri.toString());

                    UploadImage upload = new UploadImage(currentDoctorUID,
                            downloadUri.toString());
                    pDatabaseRef.push().setValue(upload);
                    isProfilePicUpdated = true;
                    dismiss();
                }

                /*
                if (uriImage != null) {
                    StorageReference fileReference = pStorageRef.child(System.currentTimeMillis()
                            + "." + getFileExtension(uriImage));
                    fileReference.putFile(uriImage)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(EditProfileDoctorActivity.this, "Update Successful", Toast.LENGTH_SHORT)
                                            .show();
                                    //Upload the image to the database
                                    UploadImage uploadImage = new UploadImage(currentDoctorUID, taskSnapshot.getDownloadUrl().toString());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProfileDoctorActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                }*/
                else {
                    if(progressDailog != null) progressDailog.dismiss();
                    String message = task.getException().getLocalizedMessage();
                    if (message == null) message = "Something went wrong, Please try again";
                    HCAlert.showAlert(activity, "Failed to upload", message);
                }
            });
        }else {
            isProfilePicUpdated = true;
        }
    }

    private void dismiss() {
        if (isProfilePicUpdated && isProfileUpdated) {
            if (progressDailog != null) progressDailog.dismiss();
            HCAlert.showAlert(this, "Success", "Profile details updated successfully", () -> finish());
        }
    }

}