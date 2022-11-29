package com.ensias.healthcareapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ensias.healthcareapp.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    private ImageView ivLogo;
    private FirebaseAuth mAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference UsersRef = db.collection("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        ivLogo = findViewById(R.id.tvAppName);

        mAuth = FirebaseAuth.getInstance();

        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.3f);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setDuration(1000);
        ivLogo.setAnimation(anim); // to start animation
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> updateUI(currentUser), 5000);
    }

    @Override
    protected void onDestroy() {
        ivLogo.setAnimation(null);
        super.onDestroy();
    }

    private void updateUI(final FirebaseUser currentUser) {
        if(currentUser != null) {
            try {
                UsersRef.document(currentUser.getEmail()).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UsersRef.document(currentUser.getEmail()).get().addOnSuccessListener(documentSnapshot1 -> {
                            User user= documentSnapshot1.toObject(User.class);
                            Intent k;
                            if(user.getType().equals("Patient")){
                                k = new Intent(SplashScreen.this, HomeActivity.class);
                            }else{
                                k = new Intent(SplashScreen.this, DoctorHomeActivity.class);
                            }
                            startActivity(k);
                            finish();
                        });
                    } else {
                        Intent k = new Intent(SplashScreen.this, FirstSigninActivity.class);
                        startActivity(k);
                        finish();
                    }
                });
            } catch(Exception e) {
                e.printStackTrace();
            }
        }else {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}